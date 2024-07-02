package dev.fiz.sync.data;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class Response {
    private SlashCommandInteractionEvent event;
    private OptionMapping value;

    public Response(SlashCommandInteractionEvent event){
        this.event = event;
    }

    public Response(OptionMapping value){
        this.value = value;
    }

    public SlashCommandInteractionEvent getEvent() {
        return event;
    }

    public GuildChannelUnion getChannel(){
        return value.getAsChannel();
    }

    public String getString(){
        return value.getAsString();
    }

    public long getLong(){
        return value.getAsLong();
    }

    public int getInt(){
        return value.getAsInt();
    }

    public double getDouble(){
        return value.getAsDouble();
    }

    public Member getMember(){
        return value.getAsMember();
    }

    public IMentionable getMentionable(){
        return value.getAsMentionable();
    }

    public boolean getBoolean(){
        return value.getAsBoolean();
    }

    public User getUser(){
        return value.getAsUser();
    }

    public Role getRole(){
        return value.getAsRole();
    }

    public Message.Attachment getAttachment(){
        return value.getAsAttachment();
    }
}