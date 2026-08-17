package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.chatentities.InfoChat;
import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.playerevents.PlayerEvent;
import com.johansvartdal.landlord.playerevents.PlayerEventManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Shop implements CommandExecutor {

    private final Main plugin;

    public Shop(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("shop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;
        if (!Main.playerDataManager.playerExists(player)) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        if (!LevelManager.featureUnlocked("shop")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        if (args.length == 0) {
            Tools.printMenuHeader(player, "Shop commands");
            if (!ShopManager.hasShop(player)) {
                Tools.printMenuOption(player, "/shop", "create (" + Bank.getPriceDisplayWithTax(StaticValues.SHOP_PRICE) + ")");
            }else {
                Tools.printMenuOption(player, "/shop", "visit");
                Tools.printMenuOption(player, "/shop", "visit <player>");
            }
            Tools.printMenuOption(player, "/shop", "info");
            return true;
        }

        // end event if player is in one
        if (PlayerEventManager.playerIsInEvent(player)) {
            if (!PlayerEventManager.getEventForPlayer(player).playerTPAwayAllowed()) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
                return true;
            }
            PlayerEventManager.forceEndPlayerEvent(player);
        }

        // handle different command variants
        if (args[0].equalsIgnoreCase("create")) {
            createShop(player);
            return true;
        }else if (args[0].equalsIgnoreCase("visit")) {
            if (args.length == 1) {
                teleportToShop(player);
            }else if (args.length == 2) {
                visitSomeonesShop(player, args[1]);
            }
            return true;
        }else if (args[0].equalsIgnoreCase("info")) {
            Tools.printMenuHeader(player, "Shop info");
            Tools.tellPlayer(player, "Start your own shop, and let other people teleport to your shop and buy items from you. You can only have one shop");
            return true;
        }
        return false;
    }

    private void visitSomeonesShop(Player player, String playerToVisit) {
        PlayerData owningShopPlayerData = Main.playerDataManager.getPlayerData(playerToVisit);
        if (owningShopPlayerData == null) {
            Tools.tellPlayer(new ErrorChat(), player, "Player does not exist");
            return;
        }

        if (owningShopPlayerData.getShopLocation() == null) {
            Tools.tellPlayer(new ErrorChat(), player, "Player does not have a shop");
            return;
        }

        // teleport and update status
        //TODO: Start a PlayerEvent instead, so it does not automatically teleport home, and is handled correctly elsewhere in code
        player.teleport(owningShopPlayerData.getShopLocation());
        PlayerDataManager.updatePlayerStatus(player, "Visiting " + playerToVisit + "'s shop");

        player.setGameMode(GameMode.ADVENTURE);
    }

    private void teleportToShop(Player player) {
        PlayerData playerData = Main.playerDataManager.getPlayerData(player);
        player.teleport(playerData.getShopLocation());

        // update status
        PlayerDataManager.updatePlayerStatus(player, "in their shop");
    }

    private void createShop(Player player) {
        if (ShopManager.hasShop(player)) {
            Tools.tellPlayer(new ErrorChat(), player, "You already have a shop");
            return;
        }

        if (!Bank.playerCanAfford(player, StaticValues.SHOP_PRICE)) {
            Bank.tellPlayerCannotAfford(player, "a shop", StaticValues.SHOP_PRICE);
            return;
        }

        // withdraw the player
        Bank.withdrawPlayer(LangDict.getString("chunks.aChunk"), player, StaticValues.SHOP_PRICE);  // money

        // inform everyone
        Tools.tellPlayer(new InfoChat(), player, "Congratulations with your new shop!", ChatColor.GREEN);
        Tools.broadcastMessage(new InfoChat(), player.getDisplayName() + " just bought a shop!", new Player[]{player});

        // play sounds
        player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 0);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 0), Tools.secToTicks(1));

        // create the shop and teleport the player there
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            ShopManager.createShop(player);
            teleportToShop(player);
        }, Tools.secToTicks(3));
    }
}
