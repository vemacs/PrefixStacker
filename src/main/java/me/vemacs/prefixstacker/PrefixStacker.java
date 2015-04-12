package me.vemacs.prefixstacker;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PrefixStacker extends JavaPlugin implements Listener {
    public static Permission permission = null;
    public static Chat chat = null;

    private static Map<String, String> groupPrefixCache;

    @Override
    public void onEnable() {
        setupPermissions();
        setupChat();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("psinvalidate").setExecutor(new InvalidateCommand());
    }

    public static void updateGroupPrefixCache() {
        for (String group : permission.getGroups()) {
            groupPrefixCache.put(group,
                    chat.getGroupPrefix((String) null, group));
        }
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

    public boolean hasSpecialPrefix(Player player) {
        String playerPrefix = chat.getPlayerPrefix(player);
        return !(playerPrefix == null || playerPrefix.isEmpty()) && !groupPrefixCache.containsValue(playerPrefix);
    }

    public String getStackedPrefix(Player player) {
        List<String> groups = new ArrayList<>(Arrays.asList(permission.getPlayerGroups(player)));
        Collections.reverse(groups);
        for (String group : groups) {
            String prefix = groupPrefixCache.get(group);
            if (prefix != null && !prefix.isEmpty()) return prefix;
        }
        return "";
    }

    private void updatePrefix(Player p) {
        if (groupPrefixCache == null) {
            groupPrefixCache = new ConcurrentHashMap<>();
            updateGroupPrefixCache();
        }
        if (!hasSpecialPrefix(p)) {
            chat.setPlayerPrefix(p, getStackedPrefix(p));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePrefix(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        updatePrefix(event.getPlayer());
    }
}