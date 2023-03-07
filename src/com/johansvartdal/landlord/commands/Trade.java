package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Trade implements CommandExecutor {

    private Main plugin;

    public Trade(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("trade").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;

        Location location = Main.tradeCenter.getLocation();
        location = Tools.highestStandingPoint(location);
        player.teleport(location);
        Tools.tellPlayer(player, "You have magically been teleported to the trading station");
        return true;
    }
}
