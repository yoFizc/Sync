package dev.fiz.sync.command;

import dev.fiz.sync.Sync;
import dev.fiz.sync.annotation.Executor;
import dev.fiz.sync.annotation.SlashCommand;
import dev.fiz.sync.data.Response;

import java.util.concurrent.CompletableFuture;

@SlashCommand(label = "unlink", description = "Unlink your minecraft account")
public class UnlinkCommand {
    private final Sync main;

    public UnlinkCommand(Sync main){
        this.main = main;
    }

    @Executor
    public void callback(Response e){
        if(!main.getConfig().contains("verified."+e.getEvent().getMember().getId())){
            e.getEvent().reply("Your account is not linked with any minecraft account").setEphemeral(true).queue();
            return;
        }
        e.getEvent().reply("Your account is unlinked with your minecraft account").setEphemeral(true).queue();
        main.getConfig().set("verified."+e.getEvent().getMember().getId(), null);
        CompletableFuture.runAsync(main::saveConfig);
    }

}