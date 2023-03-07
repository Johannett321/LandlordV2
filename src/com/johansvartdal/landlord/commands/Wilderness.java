package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.playerevents.NetherWildernessEvent;
import com.johansvartdal.landlord.playerevents.PlayerEvent;
import com.johansvartdal.landlord.playerevents.WildernessEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Wilderness implements CommandExecutor {

    private final Main plugin;

    public Wilderness(Main plugin) {
        this.plugin = plugin;

        plugin.getCommand("wilderness").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (Tools.stateNotNormal(commandSender)) {
            Tools.tellPlayer(commandSender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) commandSender;

        if (Main.levelManager.getCurrentDisplayLevelNum() < 4 && !Properties.DEBUG_MODE) {
            Tools.tellPlayer(player, "This command has not been unlocked yet", ChatColor.RED);
            return true;
        }

        if (args.length == 0) {
            Tools.printMenuHeader(player, "WILDERNESS");
            Tools.printMenuOption(player, "/wilderness", "world");
            Tools.printMenuOption(player, "/wilderness", "nether");
            Tools.printMenuOption(player, "/wilderness", "time");
            Tools.printMenuOption(player, "/wilderness", "extend");
            return true;
        }

        if (args[0].equals("world")) {
            attemptWorldWilderness(player);
        }else if (args[0].equals("nether")) {
            attemptNetherWilderness(player);
        }else if (args[0].equals("extend")) {
            extendWilderness(player);
        }else if (args[0].equals("time")) {
            time(player);
        }
        return true;
    }

    public void attemptWorldWilderness(Player player) {
        int wildernessPrice = Main.levelManager.getWildernessPrice();
        if (PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(player, "You are already in an event", ChatColor.RED);
            return;
        }
        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Tools.tellPlayer(player, "You cannot afford the wilderness price of " + wildernessPrice + LangDict.getString("currency"), ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer(player, wildernessPrice);
        Tools.tellPlayer(player, "Welcome to the wilderness. You have 5 minutes before your " + wildernessPrice + LangDict.getString("currency") + " expires!", ChatColor.GREEN);
        WildernessEvent event = new WildernessEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void attemptNetherWilderness(Player player) {
        int wildernessPrice = Main.levelManager.getNetherWildernessPrice();
        if (PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(player, "You are already in an event", ChatColor.RED);
            return;
        }
        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Tools.tellPlayer(player, "You cannot afford the nether wilderness price of " + wildernessPrice + LangDict.getString("currency"), ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer(player, wildernessPrice);
        Tools.tellPlayer(player, "Welcome to nether. You have 5 minutes before your " + wildernessPrice + LangDict.getString("currency") + " expires!", ChatColor.GREEN);
        NetherWildernessEvent event = new NetherWildernessEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void extendWilderness(Player player) {
        int wildernessPrice = Main.levelManager.getWildernessPrice();
        PlayerEvent event = PlayerEventManager.getEventForPlayer(player);
        if (event == null) {
            Tools.tellPlayer(player, "This command can only be used while in wilderness", ChatColor.RED);
            return;
        }

        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Tools.tellPlayer(player, "You cannot afford to extend your wilderness journey", ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer(player, wildernessPrice);
        Tools.tellPlayer(player, "Extending wilderness with 5 minutes for " + wildernessPrice + LangDict.getString("currency"), ChatColor.GREEN);
        event.extend();
    }

    private void time(Player player) {
        if (!PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(player, "This command can only be used while in wilderness", ChatColor.RED);
            return;
        }

        Tools.tellPlayer(player, "You have " + PlayerEventManager.getEventForPlayer(player).getTextTimeLeft() + " left of your wilderness time");
    }
}
