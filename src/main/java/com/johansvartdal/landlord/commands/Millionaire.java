package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.mysterychest.SupplyCrateChest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

import static com.johansvartdal.landlord.Tools.round;

public class Millionaire implements CommandExecutor {

    private final Main plugin;

    public Millionaire(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("millionaire").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players");
            return true;
        }

        Player player = (Player) sender;
        if (!plugin.properties.gameHasStarted() || !Main.playerDataManager.playerExists(player) || !Bank.isMillionaire(player)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.cmdNotNow"), ChatColor.RED);
            return true;
        }

        if (args.length == 0) {
            Tools.printMenuHeader(player, LangDict.getString("millionaire.millionaireTitle"));
            Tools.printMenuOption(player, "/millionaire", "buy supplycrate (" + Tools.formatCurrency(StaticValues.BUY_SUPPLY_CRATE_PRICE) + ")");
            Tools.printMenuOption(player, "/millionaire", "buy xp (" + Tools.formatCurrency(StaticValues.MILLIONAIRE_BUY_XP) + ")");
            return true;
        }

        if (args[0].equalsIgnoreCase("buy")) {
            if (args[1].equalsIgnoreCase("supplycrate")) {
                buySupplyCrate(player);
            }else if (args[1].equalsIgnoreCase("xp")) {
                buyXp(player);
            }
            return true;
        }

        return false;
    }

    private void buyXp(Player player) {
        // give player 2 stacks of Experience Bottle (Bottle o Enchanting)
    }

    private void buySupplyCrate(Player player) {
        Random random = new Random();
        int timeTillCrateSpawn = random.nextInt(60*4);

        if (Properties.DEV_CHEAT_MODE) {
            timeTillCrateSpawn = 0;
        }

        if (!Bank.playerCanAfford(player, StaticValues.BUY_SUPPLY_CRATE_PRICE)) {
            Bank.tellPlayerCannotAfford(player, "SUPPLY_CRATE", StaticValues.BUY_SUPPLY_CRATE_PRICE);
            return;
        }

        Bank.withdrawPlayer("SUPPLY_CRATE", player, StaticValues.BUY_SUPPLY_CRATE_PRICE);

        // spawn the crate

        Location location = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
        String direction = Tools.getPlayerFacingDirection(player);

        switch (direction) {
            case "north":
                location.setZ(location.getZ() - 1);
                break;
            case "south":
                location.setZ(location.getZ() + 1);
                break;
            case "east":
                location.setX(location.getX() + 1);
                break;
            case "west":
                location.setX(location.getX() - 1);
                break;
            default:
                break;
        }

        Tools.tellPlayer(player, LangDict.getString("millionaire.supplyCrateTimer"), ChatColor.GREEN);

        SupplyCrateChest supplyCrateChest = new SupplyCrateChest();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            supplyCrateChest.spawnChest(location);
            supplyCrateChest.fillChest();
            Tools.tellPlayer(player, LangDict.getString("millionaire.supplyCrateSpawned"), ChatColor.GREEN);
            Tools.playSoundForSinglePlayer(player, Sound.BLOCK_NOTE_BLOCK_CHIME);
        }, Tools.secToTicks(timeTillCrateSpawn + 60));
    }
}
