package me.mastergamercz.masterauth.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import me.mastergamercz.masterauth.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

    private Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if(!plugin.getConfig().contains("authcodes." + player.getUniqueId())) {
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            GoogleAuthenticatorKey key = gAuth.createCredentials();

            player.sendMessage(plugin.getPrefix() + ChatColor.DARK_GRAY + "Your Google Auth Code is: " + ChatColor.GOLD + key.getKey());
            player.sendMessage(plugin.getPrefix() + ChatColor.DARK_GRAY + "You must enter this code in Google Auth app, before leaving the server");

            plugin.getConfig().set("authcodes." + player.getUniqueId(), key.getKey());
            plugin.saveConfig();
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Please authenticate.");
            plugin.getAuthLocked().add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (plugin.getAuthLocked().contains(player.getUniqueId())) {
           player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Cannot process command, until you are authenticated !");
            System.out.println("DEBUG 1");
           e.setCancelled(true);
        } else {
            System.out.println("DEBUG 2");
        }
    }

    private boolean playerInputCode(Player player, int code) {
        String secretKey = plugin.getConfig().getString("authcodes." + player.getUniqueId());

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean codeIsValid = gAuth.authorize(secretKey, code);

        if (codeIsValid) {
            plugin.getAuthLocked().remove(player.getUniqueId());
            return codeIsValid;
        } else {
            return codeIsValid;
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        final Player player = e.getPlayer();
        String message = e.getMessage();

        if (plugin.getAuthLocked().contains(player.getUniqueId())) {
            try {
                Integer code = Integer.parseInt(message);
                if (playerInputCode(player, code)) {
                    plugin.getAuthLocked().remove(player.getUniqueId());
                    player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Successfully authenticated !");

                    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            player.performCommand("connect lobby");

                        }
                    }, 0, 20);
                } else {
                    player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid or expired code");
                }
            } catch (Exception exception) {
                player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Invalid or expired code");
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void moveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (plugin.getAuthLocked().contains(player.getUniqueId())) {
            player.teleport(e.getFrom());
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You must authenticate through Google Authenticator app !");
        }
    }

    @EventHandler
    public void onDestroy(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (plugin.getAuthLocked().contains(player.getUniqueId())) {
            e.setCancelled(true);
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You must authenticate through Google Authenticator app !");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (plugin.getAuthLocked().contains(player.getUniqueId())) {
            e.setCancelled(true);
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "You must authenticate through Google Authenticator app !");
        }
    }
}
