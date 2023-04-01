package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
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
        // state normal
        if (Tools.stateNotNormal(commandSender)) {
            Tools.tellPlayer(commandSender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) commandSender;

        // check if feature unlocked
        if (!LevelManager.featureUnlocked("wildworld")) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        // inform about commands
        if (args.length == 0) {
            Tools.printMenuHeader(player, LangDict.getString("commands"));
            Tools.printMenuOption(player, "/wilderness", "world");
            Tools.printMenuOption(player, "/wilderness", "nether");
            Tools.printMenuOption(player, "/wilderness", "mine");
            Tools.printMenuOption(player, "/wilderness", "time");
            Tools.printMenuOption(player, "/wilderness", "extend");
            return true;
        }

        // check if player is flying
        if (PlayerEventManager.playerIsInFlyingEvent(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.END_FLIGHT_FIRST));
            return true;
        }

        // command args
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
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Tools.tellPlayer(player, LangDict.getString("youNeed") + wildernessPrice + LangDict.getString("currency") + LangDict.getString("toAccessWilderness"), ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer(LangDict.getString("wilderness"), player, wildernessPrice);
        Tools.tellPlayer(player, LangDict.getString("welcomeToWild") + wildernessPrice + LangDict.getString("currency") + LangDict.getString("expires"), ChatColor.GREEN);
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
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        if (!Bank.playerCanAfford(player, minePrice)) {
            Tools.tellPlayer(player, LangDict.getString("youNeed") + minePrice + LangDict.getString("currency") + LangDict.getString("toAccessWilderness"), ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer(LangDict.getString("wildernessMine"), player, minePrice);
        Tools.tellPlayer(player, LangDict.getString("welcomeToWildMine") + minePrice + LangDict.getString("currency") + LangDict.getString("expires"), ChatColor.GREEN);
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
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }
        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Tools.tellPlayer(player, LangDict.getString("youNeed") + wildernessPrice + LangDict.getString("currency") + LangDict.getString("toAccessWilderness"), ChatColor.RED);
            return;
        }

        Bank.withdrawPlayer(LangDict.getString("wildernessNether"), player, wildernessPrice);
        Tools.tellPlayer(player, LangDict.getString("welcomeWildNether") + wildernessPrice + LangDict.getString("currency") + LangDict.getString("expires"), ChatColor.GREEN);
        NetherWildernessEvent event = new NetherWildernessEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void extendWilderness(Player player) {
        PlayerEvent event = PlayerEventManager.getEventForPlayer(player);

        // make sure event is not null
        if (event == null) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        // get wilderness price, and make sure player can afford
        int extensionPrice = event.getExtensionPrice();
        if (!Bank.playerCanAfford(player, extensionPrice)) {
            Tools.tellPlayer(player, LangDict.getString("youNeed") + extensionPrice + LangDict.getString("currency") + LangDict.getString("toExtendThisJourney"), ChatColor.RED);
            return;
        }

        // perform purchase
        Bank.withdrawPlayer(LangDict.getString("wildernessExtension"), player, extensionPrice);
        event.extend();
        Tools.tellPlayer(player, LangDict.getString("extended") + event.getTitle() + LangDict.getString("with") + event.getTextTimeLeft() + LangDict.getString("for") + extensionPrice + LangDict.getString("currency"), ChatColor.GREEN);
    }

    private void time(Player player) {
        if (!PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        Tools.tellPlayer(player, LangDict.getString("youHave") + PlayerEventManager.getEventForPlayer(player).getTextTimeLeft() + LangDict.getString("leftOfWildTime"));
    }
}
