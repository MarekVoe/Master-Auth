package me.mastergamercz.masterauth;

import me.mastergamercz.masterauth.commands.ConnectCommand;
import me.mastergamercz.masterauth.listeners.PlayerListener;
import me.mastergamercz.masterauth.listeners.PluginChannelListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.UUID;

public class Main extends JavaPlugin {
    private ArrayList<UUID> authLocked;
    private static Main instance;
    private String prefix = (ChatColor.translateAlternateColorCodes('&', "&8[&6MasterAuth&8] "));


    public void onEnable() {
        instance = this;
        authLocked = new ArrayList<UUID>();

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginChannelListener());

        getCommand("connect").setExecutor(new ConnectCommand());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this),this);
    }

    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public String getPrefix() {
        return prefix;
    }

    public static Main getInstance() {
        return instance;
    }

    public ArrayList<UUID> getAuthLocked() {
        return authLocked;
    }
}
