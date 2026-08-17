package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHome implements CommandExecutor {

    private Main plugin;

    public SetHome(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("sethome").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;
        if (!Main.playerDataManager.playerExists(player)) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Location currentLocation = player.getLocation();

        PlayerData playerData = Main.playerDataManager.getPlayerData(player);

        if (!playerData.ownsChunk(currentLocation.getChunk())) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("home.onlySetHomeInHomeChunk"), ChatColor.RED);
            return true;
        }

        if (playerData.getHomeLocation().distance(currentLocation) > 5000) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }



        currentLocation.setX(currentLocation.getX());
        currentLocation.setZ(currentLocation.getZ());
        Main.playerDataManager.getPlayerData(player).setHomeLocation(currentLocation);
        player.setBedSpawnLocation(currentLocation, true);
        Tools.tellPlayer(player, LangDict.getString("home.homeUpdated"), ChatColor.GREEN);
        return true;
    }
}
