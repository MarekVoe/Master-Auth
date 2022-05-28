package me.mastergamercz.masterauth.commands;

import me.mastergamercz.masterauth.listeners.PluginChannelListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConnectCommand implements CommandExecutor {

    private PluginChannelListener pluginMessage = new PluginChannelListener();


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
          Player player = (Player) sender;


          if(cmd.getName().equalsIgnoreCase(label)) {
              if (args.length == 1) {
                 pluginMessage.connect(player, args[0]);
              } else {
                  player.sendMessage(ChatColor.RED + "/connect <server>" );
              }
          }
        }
        return true;
    }
}
