package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.LevelManager;
import com.johansvartdal.landlord.playerevents.MiningEvent;
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

        if (!LevelManager.featureUnlocked("wildworld")) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        if (args.length == 0) {
            Tools.printMenuHeader(player, "WILDERNESS");
            Tools.printMenuOption(player, "/wilderness", "world");
            Tools.printMenuOption(player, "/wilderness", "nether");
            Tools.printMenuOption(player, "/wilderness", "mine");
            Tools.printMenuOption(player, "/wilderness", "time");
            Tools.printMenuOption(player, "/wilderness", "extend");
            return true;
        }

        if (args[0].equals("world")) {
            attemptWorldWilderness(player);
        }else if (args[0].equals("nether")) {
            attemptNetherWilderness(player);
        }else if (args[0].equals("mine")) {
            attemptMineWilderness(player);
        }else if (args[0].equals("extend")) {
            extendWilderness(player);
        }else if (args[0].equals("time")) {
            time(player);
        }
        return true;
    }

    public void attemptWorldWilderness(Player player) {
        int wildernessPrice = LevelManager.getWildernessPrice();

        if (!LevelManager.featureUnlocked("wildworld")) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return;
        }

        if (PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(player, "You are already in an event", ChatColor.RED);
            return;
        }

        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Tools.tellPlayer(player, "You cannot afford the wilderness price of " + wildernessPrice + LangDict.getString("currency"), ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer("wilderness", player, wildernessPrice);
        Tools.tellPlayer(player, "Welcome to the wilderness. You have 7 minutes before your " + wildernessPrice + LangDict.getString("currency") + " expires!", ChatColor.GREEN);
        WildernessEvent event = new WildernessEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void attemptMineWilderness(Player player) {
        int minePrice = StaticValues.MINING_PRICE;

        if (!LevelManager.featureUnlocked("wildmining")) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return;
        }

        if (PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(player, "You are already in an event", ChatColor.RED);
            return;
        }

        if (!Bank.playerCanAfford(player, minePrice)) {
            Tools.tellPlayer(player, "You cannot afford the wilderness price of " + minePrice + LangDict.getString("currency"), ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer("wilderness (mine)", player, minePrice);
        Tools.tellPlayer(player, "Welcome to the mine. You have 45 minutes before your " + minePrice + LangDict.getString("currency") + " expires!", ChatColor.GREEN);
        MiningEvent event = new MiningEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void attemptNetherWilderness(Player player) {
        int wildernessPrice = LevelManager.getNetherWildernessPrice();

        if (!LevelManager.featureUnlocked("wildnether")) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return;
        }

        if (PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(player, "You are already in an event", ChatColor.RED);
            return;
        }
        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Tools.tellPlayer(player, "You cannot afford the nether wilderness price of " + wildernessPrice + LangDict.getString("currency"), ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer("wilderness (nether)", player, wildernessPrice);
        Tools.tellPlayer(player, "Welcome to nether. You have 7 minutes before your " + wildernessPrice + LangDict.getString("currency") + " expires!", ChatColor.GREEN);
        NetherWildernessEvent event = new NetherWildernessEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void extendWilderness(Player player) {
        PlayerEvent event = PlayerEventManager.getEventForPlayer(player);

        // make sure event is not null
        if (event == null) {
            Tools.tellPlayer(player, "This command can only be used while in wilderness", ChatColor.RED);
            return;
        }

        // get wilderness price, and make sure player can afford
        int extensionPrice = event.getExtensionPrice();
        if (!Bank.playerCanAfford(player, extensionPrice)) {
            Tools.tellPlayer(player, "You cannot afford to extend your wilderness journey", ChatColor.RED);
            return;
        }

        // perform purchase
        Bank.withdrawPlayer("wilderness (extension)", player, extensionPrice);
        event.extend();
        Tools.tellPlayer(player, "Extended " + event.getTitle() + " with " + event.getTextTimeLeft() + " for " + extensionPrice + LangDict.getString("currency"), ChatColor.GREEN);
    }

    private void time(Player player) {
        if (!PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(player, "This command can only be used while in wilderness", ChatColor.RED);
            return;
        }

        Tools.tellPlayer(player, "You have " + PlayerEventManager.getEventForPlayer(player).getTextTimeLeft() + " left of your wilderness time");
    }
}
