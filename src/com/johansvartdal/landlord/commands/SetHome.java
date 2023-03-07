package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
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
        Location currentLocation = player.getLocation();

        if (!Main.playerDataManager.getPlayerData(player).ownsChunk(currentLocation.getChunk())) {
            Tools.tellPlayer(player, "You can only set your home inside a chunk you own", ChatColor.RED);
            return true;
        }

        currentLocation.setX(currentLocation.getX());
        currentLocation.setZ(currentLocation.getZ());
        Main.playerDataManager.getPlayerData(player).setHome(currentLocation);
        Tools.tellPlayer(player, "Your home was update");
        return true;
    }
}
