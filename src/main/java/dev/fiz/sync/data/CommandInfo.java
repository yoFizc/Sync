package dev.fiz.sync.data;

import dev.fiz.sync.annotation.SlashCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.ArrayList;

@RequiredArgsConstructor
@AllArgsConstructor
public class CommandInfo {

    @Getter
    private final Object object;

    @Getter
    private final SlashCommand annotation;

    @Getter
    @Setter
    private Method callback;

    @Getter
    private ArrayList<CommandInfo> sub = new ArrayList<>();

    public CommandInfo getSub(String label){
        for(CommandInfo sub : this.sub){
            if(sub.getAnnotation().label().equalsIgnoreCase(label)){
                return sub;
            }
        }
        return null;
    }

    public void addSub(CommandInfo info){
        this.sub.add(info);
    }
}