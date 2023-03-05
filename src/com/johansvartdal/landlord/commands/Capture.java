package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Capture implements CommandExecutor {

    private Main plugin;

    public Capture(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("capture").setExecutor(this);
    }

    public static String getCaptureAnimalsString() {
        return "Cow, Sheep, Chicken, Pig, LLama, Squid, Villager";
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (Main.levelManager.getCurrentDisplayLevelNum() < 6 && !Properties.DEBUG_MODE) {
            Tools.tellPlayer(player, "This command has not been unlocked yet", ChatColor.RED);
            return true;
        }

        int priceToWithdraw = StaticValues.CAPTURE_PRICE;
        if (!Bank.playerCanAfford(player, StaticValues.CAPTURE_PRICE)) {
            Tools.tellPlayer(player, "You need " + StaticValues.CAPTURE_PRICE + LangDict.getString("currency") + " to capture an animal", ChatColor.RED);
            return true;
        }

        // find the animal
        List<Entity> near = player.getNearbyEntities(6,6,6);
        double lowestDistance = 0;
        int entityIndexLowest = 0;
        for (int i = 0; i < near.size(); i++) {
            Location entityLoc = near.get(i).getLocation();
            double distance = player.getLocation().distance(entityLoc);
            if (distance < lowestDistance) {
                lowestDistance = distance;
                entityIndexLowest = i;
            }
        }

        if (near.size() == 0) {
            Tools.tellPlayer(player, "You have to get closer to the animal", ChatColor.RED);
            return true;
        }

        ItemStack items;

        if (near.get(entityIndexLowest).getType() == EntityType.COW) {
            items = new ItemStack(Material.COW_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.SHEEP) {
            items = new ItemStack(Material.SHEEP_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.CHICKEN) {
            items = new ItemStack(Material.CHICKEN_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.PIG) {
            items = new ItemStack(Material.PIG_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.LLAMA) {
            items = new ItemStack(Material.LLAMA_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.SQUID) {
            items = new ItemStack(Material.SQUID_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.BEE) {
            items = new ItemStack(Material.BEE_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.VILLAGER) {
            if (Bank.playerCanAfford(player, StaticValues.VILLAGER_CAPTURE_PRICE)) {
                items = new ItemStack(Material.VILLAGER_SPAWN_EGG);
            }else {
                Tools.tellPlayer(player, "You need at least " + StaticValues.VILLAGER_CAPTURE_PRICE + LangDict.getString("currency") + " to capture a villager", ChatColor.RED);
                return true;
            }
        }else {
            Tools.tellPlayer(player, "This animal cannot be captured", ChatColor.RED);
            return true;
        }

        // add the item
        player.getInventory().addItem(items);

        // withdraw
        Bank.withdrawPlayer(player, priceToWithdraw);

        // remove animal
        player.playEffect(near.get(entityIndexLowest).getLocation(), Effect.ELECTRIC_SPARK, null);
        player.playSound(near.get(entityIndexLowest).getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0);
        near.get(entityIndexLowest).remove();

        // inform player
        Tools.tellPlayer(player, "You successfully captured the animal!", ChatColor.GREEN);
        return true;
    }
}
