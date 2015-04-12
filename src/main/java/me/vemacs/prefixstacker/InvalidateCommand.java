package me.vemacs.prefixstacker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class InvalidateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("psinvalidate")) {
            PrefixStacker.updateGroupPrefixCache();
            sender.sendMessage(ChatColor.GREEN + "Invalidated group prefix cache.");
            return true;
        }
        return false;
    }
}
