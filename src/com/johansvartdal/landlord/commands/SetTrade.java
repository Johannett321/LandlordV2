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

public class SetTrade implements CommandExecutor {

    private Main plugin;

    public SetTrade(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("settrade").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;

        if (!player.isOp()) {
            Tools.tellPlayer(player, "This command can only be run by an OP", ChatColor.RED);
            return true;
        }

        if (!Main.tradeCenter.getLocation().getChunk().equals(player.getLocation().getChunk())) {
            Tools.tellPlayer(player, "The trade location must be set within the trade chunk", ChatColor.RED);
            return true;
        }

        Main.tradeCenter.setLocation(player.getLocation());
        Tools.tellPlayer(player, "Trade center location updated!", ChatColor.GREEN);
        return true;
    }
}
