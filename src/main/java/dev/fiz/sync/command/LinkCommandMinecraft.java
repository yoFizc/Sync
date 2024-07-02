package dev.fiz.sync.command;


import dev.fiz.sync.Manager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LinkCommandMinecraft implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            TextComponent text = new TextComponent(Manager.hasCode(p) ? "Your new link code is ready! " : "Your link code is ready! ");
            text.setColor(ChatColor.DARK_PURPLE);

            TextComponent code = new TextComponent("[Copy Your Code!]");
            code.setColor(ChatColor.GREEN);
            code.setBold(true);
            code.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, Manager.generateCode(10, p)));

            p.spigot().sendMessage(text, code);
        } else {
            sender.sendMessage(ChatColor.RED + "You cant use this command!");
        }
        return true;
    }
}