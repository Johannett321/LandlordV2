package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendHome implements CommandExecutor {

    private Main plugin;

    public SendHome(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("sendhome").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;
        if (args.length != 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase(player.getDisplayName())) {
            Tools.tellPlayer(player, "You cannot use this command on yourself", ChatColor.RED);
            return true;
        }

        sendHomePlayer(player, args[0]);
        return true;
    }

    private void sendHomePlayer(Player owningPlayer, String playerName) {
        for (Player player:Bukkit.getOnlinePlayers()) {
            if (!player.getDisplayName().equalsIgnoreCase(playerName)) {
                continue;
            }

            if(!Main.playerDataManager.getPlayerData(owningPlayer).ownsChunk(player.getLocation().getChunk())) {
                Tools.tellPlayer(owningPlayer, "The player is not in one of your chunks", ChatColor.RED);
                return;
            }

            Location homeLoc = Main.playerDataManager.getPlayerData(player).getHomeLocation();
            player.teleport(homeLoc);
            player.setGameMode(GameMode.SURVIVAL);

            Tools.tellPlayer(owningPlayer, "Player was sent home", ChatColor.GREEN);
            Tools.tellPlayer(player, "You were sent home by the owner", ChatColor.RED);
            return;
        }

        Tools.tellPlayer(owningPlayer, "Could not find the player: " + playerName, ChatColor.RED);
    }
}
