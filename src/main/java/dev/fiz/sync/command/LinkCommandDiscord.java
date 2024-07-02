package dev.fiz.sync.command;

import dev.fiz.sync.Manager;
import dev.fiz.sync.Sync;
import dev.fiz.sync.annotation.Executor;
import dev.fiz.sync.annotation.Option;
import dev.fiz.sync.annotation.SlashCommand;
import dev.fiz.sync.data.Response;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.CompletableFuture;

@SlashCommand(label = "link", description = "Links your discord and minecraft accounts")
public class LinkCommandDiscord {

    private final Sync main;

    public LinkCommandDiscord(Sync main){
        this.main = main;
    }

    @Executor
    public void callback(Response e, @Option(name = "code", description = "Your linking code", required = true) Response linkCode){
        String code = linkCode.getString();

        if(main.getConfig().contains("verified."+e.getEvent().getMember().getId())){
            e.getEvent().reply("Your account is already linked with a minecraft account").setEphemeral(true).queue();
            return;
        }

        if(Manager.isValid(code)){
            OfflinePlayer player = Bukkit.getOfflinePlayer(Manager.getPlayer(code));
            e.getEvent().reply("Your account linked with `"+player.getName()+"`").setEphemeral(true).queue();
            main.getConfig().set("verified."+e.getEvent().getMember().getId(), Manager.getPlayer(code).toString());
            CompletableFuture.runAsync(main::saveConfig);
            Manager.deleteCode(code);
        } else {
            e.getEvent().reply("Invalid code").setEphemeral(true).queue();
        }
    }
}