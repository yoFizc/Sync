package dev.fiz.sync.command;

import dev.fiz.sync.annotation.Choice;
import dev.fiz.sync.annotation.Executor;
import dev.fiz.sync.annotation.Option;
import dev.fiz.sync.annotation.SlashCommand;
import dev.fiz.sync.data.CommandInfo;
import dev.fiz.sync.data.Response;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandHandler extends ListenerAdapter {
    private final HashMap<String, CommandInfo> commandMap = new HashMap<>();

    private final JDA jda;

    public CommandHandler(JDA jda){
        this.jda = jda;
    }

    public void registerCommands(Object... objects){
        for(Object o : objects){
            registerCommand(o);
        }
    }

    public void registerCommand(Object object){
        if(!object.getClass().isAnnotationPresent(SlashCommand.class)){
            return;
        }
        CommandInfo data = getCommandInfo(object);
        commandMap.put(data.getAnnotation().label(), data);
        parseCommand(data);
    }

    private void parseCommand(CommandInfo info){
        SlashCommandData data = Commands.slash(info.getAnnotation().label(), info.getAnnotation().description());
        data.addOptions(parseOption(info.getCallback()));
        data.setGuildOnly(info.getAnnotation().guildOnly());
        for(CommandInfo group : info.getSub()){

            if(group.getCallback() != null){
                data.addSubcommands(new SubcommandData(group.getAnnotation().label(), group.getAnnotation().description()).addOptions(parseOption(group.getCallback())));
                continue;
            }

            SubcommandGroupData subcommandGroupData = new SubcommandGroupData(group.getAnnotation().label(), group.getAnnotation().description());

            for(CommandInfo subcommand : group.getSub()){
                subcommandGroupData.addSubcommands(new SubcommandData(subcommand.getAnnotation().label(), subcommand.getAnnotation().description()).addOptions(parseOption(subcommand.getCallback())));
            }
            data.addSubcommandGroups(subcommandGroupData);
        }
        for(Guild guild : jda.getGuilds()){
            guild.upsertCommand(data).queue();
        }
    }

    private ArrayList<OptionData> parseOption(Method method){
        ArrayList<OptionData> data = new ArrayList<>();
        if(method == null) return data;
        for(Annotation[] annotations : method.getParameterAnnotations()){
            if(annotations.length == 0) continue;

            Option option = (Option) getAnnotationFromMethod(annotations, Option.class);
            if(option == null) continue;

            OptionData optionData = new OptionData(option.optionType(), option.name(), option.description(), option.required());
            for(Choice choice : option.choices()){
                optionData.addChoice(choice.name(), choice.value().equals("") ? choice.name() : choice.value());
            }
            data.add(optionData);
        }

        return data;
    }

    private CommandInfo getCommandInfo(Object object){
        SlashCommand slashCommandData = object.getClass().getAnnotation(SlashCommand.class);
        Method callback = getCallbackFromObject(object.getClass());
        CommandInfo data = new CommandInfo(object, slashCommandData, callback, new ArrayList<>());

        for(Class<?> subGroup : object.getClass().getClasses()){
            if(!subGroup.isAnnotationPresent(SlashCommand.class)) continue;

            CommandInfo subgroupInfo = getSubcommandInfo(subGroup, object);

            for(Class<?> subCommand : subGroup.getClasses()){
                if(!subCommand.isAnnotationPresent(SlashCommand.class)) continue;

                CommandInfo subcommandInfo = getSubcommandInfo(subCommand, subgroupInfo.getObject());
                if(subcommandInfo.getCallback() != null) {
                    subgroupInfo.addSub(subcommandInfo);
                }

            }
            data.addSub(subgroupInfo);
        }

        return data;
    }

    private CommandInfo getSubcommandInfo(Class c, Object o){
        Object instance;
        try {
            instance = c.getConstructor(o.getClass()).newInstance(o);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new CommandInfo(instance, instance.getClass().getAnnotation(SlashCommand.class), getCallbackFromObject(c), new ArrayList<>());
    }

    private Method getCallbackFromObject(Class c){
        for(Method m : c.getMethods()){
            if(m.isAnnotationPresent(Executor.class)){
                return m;
            }
        }
        return null;
    }

    public Annotation getAnnotationFromMethod(Annotation[] annotations, Class<?> annotationClass){
        for(Annotation annotation : annotations){
            if(annotation.annotationType().equals(annotationClass)){
                return annotation;
            }
        }
        return null;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e){
        String[] path = e.getCommandPath().split("/");
        CommandInfo info = commandMap.get(path[0]);
        if(path.length == 2){
            info = info.getSub(path[1]);
        }
        if(path.length == 3){
            info = info.getSub(path[2]);
        }

        Object[] args = new Response[e.getOptions().size()+1];
        args[0] = new Response(e);
        for(int i=1; i<args.length; i++){
            args[i] = new Response(e.getOptions().get(i-1));
        }

        try {
            info.getCallback().invoke(info.getObject(), args); // i need help with this
        } catch (IllegalAccessException | InvocationTargetException ex) {
            System.out.println("Cant invoke callback. Command: "+e.getCommandPath() + "  |  Cause:"+ex.getCause());
        }
    }
}