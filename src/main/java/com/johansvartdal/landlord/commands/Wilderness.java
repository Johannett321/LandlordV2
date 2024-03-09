package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.levels.LevelManager;
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
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        // inform about commands
        if (args.length == 0) {
            Tools.printMenuHeader(player, LangDict.getString("generalSentenceParts.commands"));
            Tools.printMenuOption(player, "/wilderness", "world");
            if (LevelManager.featureUnlocked("wildnether")) {
                Tools.printMenuOption(player, "/wilderness", "nether");
            }

            if (LevelManager.featureUnlocked("wildmining")) {
                Tools.printMenuOption(player, "/wilderness", "mine");
            }
            Tools.printMenuOption(player, "/wilderness", "time");
            Tools.printMenuOption(player, "/wilderness", "extend");
            Tools.printMenuOption(player, "/wilderness", "info");
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
            return true;
        }else if (args[0].equals("nether")) {
            attemptNetherWilderness(player);
            return true;
        }else if (args[0].equals("mine")) {
            attemptMineWilderness(player);
            return true;
        }else if (args[0].equals("extend")) {
            extendWilderness(player);
            return true;
        }else if (args[0].equals("time")) {
            time(player);
            return true;
        }else if (args[0].equals("info")) {
            Tools.printMenuHeader(player, "Wilderness info");
            Tools.printMenuOption(player, LangDict.getString("playerEvents.wilderness.price") + " (world)", LevelManager.getWildernessPrice() + LangDict.getString(LangDict.CURRENCY) + " (" + LangDict.getString("playerEvents.wilderness.every") + 7 + LangDict.getString("generalSentenceParts.minutes") + ")");
            if (LevelManager.featureUnlocked("wildnether")) {
                Tools.printMenuOption(player, LangDict.getString("playerEvents.wilderness.price") + " (nether)", LevelManager.getNetherWildernessPrice() + LangDict.getString(LangDict.CURRENCY) + " (" + LangDict.getString("playerEvents.wilderness.every") + 7 + LangDict.getString("generalSentenceParts.minutes") + ")");
            }
            if (LevelManager.featureUnlocked("wildmining")) {
                Tools.printMenuOption(player, LangDict.getString("playerEvents.wilderness.price") + " (mine)", StaticValues.MINING_PRICE + LangDict.getString(LangDict.CURRENCY) + " (" + LangDict.getString("playerEvents.wilderness.every") + 45 + LangDict.getString("generalSentenceParts.minutes") + ")");
            }
            return true;
        }
        return false;
    }

    public void attemptWorldWilderness(Player player) {
        int wildernessPrice = LevelManager.getWildernessPrice();

        if (!LevelManager.featureUnlocked("wildworld")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return;
        }

        if (PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Bank.tellPlayerTheyNeed(player, wildernessPrice, LangDict.getString("playerEvents.wilderness.toAccessWilderness"));
            return;
        }

        Bank.withdrawPlayer(LangDict.getString("playerEvents.wilderness.title"), player, wildernessPrice);
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.welcomeToWild") + wildernessPrice + LangDict.getString(LangDict.CURRENCY) + LangDict.getString("playerEvents.wilderness.expires"), ChatColor.GREEN);
        WildernessEvent event = new WildernessEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void attemptMineWilderness(Player player) {
        int minePrice = StaticValues.MINING_PRICE;

        // Make sure mining is unlocked
        if (!LevelManager.featureUnlocked("wildmining")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return;
        }

        // Make sure player is not in a player event
        if (PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        // Make sure player can afford
        if (!Bank.playerCanAfford(player, minePrice)) {
            Bank.tellPlayerTheyNeed(player, minePrice, LangDict.getString("playerEvents.wilderness.toAccessWilderness"));
            return;
        }

        // Withdraw and start mining event
        Bank.withdrawPlayer(LangDict.getString("playerEvents.wilderness.wildernessMine"), player, minePrice);
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.welcomeToWildMine") + minePrice + LangDict.getString(LangDict.CURRENCY) + LangDict.getString("playerEvents.wilderness.expires"), ChatColor.GREEN);
        MiningEvent event = new MiningEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void attemptNetherWilderness(Player player) {
        int wildernessPrice = LevelManager.getNetherWildernessPrice();

        if (!LevelManager.featureUnlocked("wildnether")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return;
        }

        if (PlayerEventManager.playerIsInEvent(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }
        if (!Bank.playerCanAfford(player, wildernessPrice)) {
            Bank.tellPlayerTheyNeed(player, wildernessPrice, LangDict.getString("playerEvents.wilderness.toAccessWilderness"));
            return;
        }

        Bank.withdrawPlayer(LangDict.getString("playerEvents.wilderness.wildernessNether"), player, wildernessPrice);
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.welcomeWildNether") + wildernessPrice + LangDict.getString(LangDict.CURRENCY) + LangDict.getString("playerEvents.wilderness.expires"), ChatColor.GREEN);
        NetherWildernessEvent event = new NetherWildernessEvent(plugin, player);
        PlayerEventManager.startPlayerEvent(event);
    }

    private void extendWilderness(Player player) {
        PlayerEvent event = PlayerEventManager.getEventForPlayer(player);

        // make sure event is not null
        // make sure player actually is in wilderness
        if (!(event instanceof WildernessEvent) && !(event instanceof NetherWildernessEvent) && !(event instanceof MiningEvent)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        // get wilderness price, and make sure player can afford
        int extensionPrice = event.getExtensionPrice();
        if (!Bank.playerCanAfford(player, extensionPrice)) {
            Bank.tellPlayerTheyNeed(player, extensionPrice, LangDict.getString("playerEvents.wilderness.toExtendThisJourney"));
            return;
        }

        // perform purchase
        Bank.withdrawPlayer(LangDict.getString("playerEvents.wilderness.wildernessExtension"), player, extensionPrice);
        event.extend();
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.extended") + event.getTitle() + LangDict.getString("sellItem.with") + event.getTextTimeLeft() + LangDict.getString("sellItem.for") + extensionPrice + LangDict.getString(LangDict.CURRENCY), ChatColor.GREEN);
    }

    private void time(Player player) {
        PlayerEvent playerEvent = PlayerEventManager.getEventForPlayer(player);

        // make sure player actually is in wilderness
        if (!(playerEvent instanceof WildernessEvent)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return;
        }

        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.youHave") + PlayerEventManager.getEventForPlayer(player).getTextTimeLeft() + LangDict.getString("playerEvents.wilderness.leftOfWildTime"));
    }
}
