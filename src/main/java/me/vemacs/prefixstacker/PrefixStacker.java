package me.vemacs.prefixstacker;

import com.google.common.base.Joiner;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PrefixStacker extends JavaPlugin implements Listener {
    public static Permission permission = null;
    public static Chat chat = null;

    @Override
    public void onEnable() {
        setupPermissions();
        setupChat();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    public String getStackedPrefix(Player player) {
        String playerPrefix = chat.getPlayerPrefix(player);
        if (playerPrefix != null && !playerPrefix.isEmpty()) return playerPrefix;
        List<String> groups = new ArrayList<>(Arrays.asList(permission.getPlayerGroups(player)));
        Collections.reverse(groups);
        for (String group : groups) {
            String prefix = chat.getGroupPrefix(player.getWorld(), group);
            if (prefix != null && !prefix.isEmpty()) return prefix;
        }
        return "";
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.setFormat(event.getFormat().replace("VAULT_STACKED_PREFIX",
                ChatColor.translateAlternateColorCodes('&', getStackedPrefix(event.getPlayer()))));
    }
}