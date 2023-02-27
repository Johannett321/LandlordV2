package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players");
            return true;
        }

        Player player = (Player) sender;

        if (!Main.properties.gameStateIsNormal()) {
            sender.sendMessage("You cannot execute this command at the moment");
            return true;
        }

        Location location = Main.tradeCenter.getLocation();
        location = Tools.highestStandingPoint(location);
        player.teleport(location);
        sender.sendMessage("You have magically been teleported to the trading station");
        return true;
    }
}
