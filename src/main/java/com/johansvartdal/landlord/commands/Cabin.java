package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.chatentities.InfoChat;
import com.johansvartdal.landlord.levels.LevelManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cabin implements CommandExecutor {

    private final Main plugin;

    public Cabin(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("cabin").setExecutor(this);
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

        if (!LevelManager.featureUnlocked("cabin")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        if (args.length == 0) {
            if (!CabinManager.hasCabin(player)) {
                Tools.printMenuHeader(player, "Cabin commands");
                Tools.printMenuOption(player, "/cabin", "create (" + Bank.getPriceDisplayWithTax(StaticValues.CABIN_PRICE) + ")");
                Tools.printMenuOption(player, "/cabin", "info");
            }else {
                teleportToCabin(player);
            }
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                createCabin(player);
                return true;
            }else if (args[0].equalsIgnoreCase("info")) {
                Tools.printMenuHeader(player, "Cabin info");
                Tools.tellPlayer(player, "Buy a chunk far away where you can build your cabin. You can only have one cabin");
                return true;
            }
        }
        return false;
    }

    private void teleportToCabin(Player player) {
        PlayerData playerData = Main.playerDataManager.getPlayerData(player);
        player.teleport(playerData.getCabinLocation());

        // update status
        PlayerDataManager.updatePlayerStatus(player, "in cabin");

        // set survival
        player.setGameMode(GameMode.SURVIVAL);
    }

    private void createCabin(Player player) {
        if (CabinManager.hasCabin(player)) {
            Tools.tellPlayer(new ErrorChat(), player, "You already have a cabin");
            return;
        }

        if (!Bank.playerCanAfford(player, StaticValues.CABIN_PRICE)) {
            Bank.tellPlayerCannotAfford(player, "a cabin", StaticValues.CABIN_PRICE);
            return;
        }

        // withdraw the player
        Bank.withdrawPlayer(LangDict.getString("chunks.aChunk"), player, StaticValues.CABIN_PRICE);  // money

        // inform everyone
        Tools.tellPlayer(new InfoChat(), player, "Congratulations with your new cabin!", ChatColor.GREEN);
        Tools.broadcastMessage(new InfoChat(), player.getDisplayName() + " just bought a cabin!", new Player[]{player});

        // play sounds
        player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 0);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 0), Tools.secToTicks(1));

        // create the cabin and teleport the player there
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            CabinManager.createCabin(player);
            teleportToCabin(player);
        }, Tools.secToTicks(3));
    }
}
