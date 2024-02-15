package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.playerevents.WildernessEvent;
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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (Tools.stateNotNormal(commandSender)) {
            Tools.tellPlayer(commandSender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) commandSender;

        // Make sure the command has been unlocked
        if (!LevelManager.featureUnlocked("capture")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        // make sure the player is in wilderness. Should not be able to capture animal anywhere else
        if (!(PlayerEventManager.getEventForPlayer(player) instanceof WildernessEvent)) {
            Tools.tellPlayer(new ErrorChat(), player, "commandResponses.errorMessages.cmdOnlyInWilderness");
            return true;
        }

        // Make sure player has enough bal
        int priceToWithdraw = StaticValues.CAPTURE_PRICE;
        if (!Bank.playerCanAfford(player, StaticValues.CAPTURE_PRICE)) {
            Bank.tellPlayerTheyNeed(player, StaticValues.CAPTURE_PRICE, LangDict.getString("capture.toCapture"));
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

        if (near.isEmpty()) {
            Tools.tellPlayer(player, LangDict.getString("capture.captureGetCloser"), ChatColor.RED);
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
        }else if (near.get(entityIndexLowest).getType() == EntityType.CAT) {
            items = new ItemStack(Material.CAT_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.OCELOT) {
            items = new ItemStack(Material.OCELOT_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.DOLPHIN) {
            items = new ItemStack(Material.DOLPHIN_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.HORSE) {
            items = new ItemStack(Material.HORSE_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.CAMEL) {
            items = new ItemStack(Material.CAMEL_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.DONKEY) {
            items = new ItemStack(Material.DONKEY_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.FROG) {
            items = new ItemStack(Material.FROG_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.TURTLE) {
            items = new ItemStack(Material.TURTLE_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.GOAT) {
            items = new ItemStack(Material.GOAT_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.PANDA) {
            items = new ItemStack(Material.PANDA_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.RABBIT) {
            items = new ItemStack(Material.RABBIT_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.PARROT) {
            items = new ItemStack(Material.PARROT_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.WOLF) {
            items = new ItemStack(Material.WOLF_SPAWN_EGG);
        }else if (near.get(entityIndexLowest).getType() == EntityType.VILLAGER) {
            if (Bank.playerCanAfford(player, StaticValues.VILLAGER_CAPTURE_PRICE)) {
                items = new ItemStack(Material.VILLAGER_SPAWN_EGG);
            }else {
                Bank.tellPlayerTheyNeed(player, StaticValues.VILLAGER_CAPTURE_PRICE, LangDict.getString("capture.toCaptureVillager"));
                return true;
            }
        }else {
            Tools.tellPlayer(player, LangDict.getString("capture.cannotCaptureAnimal"), ChatColor.RED);
            return true;
        }

        // add the item
        Tools.givePlayerItemOrDrop(player, items, true);

        // withdraw
        Bank.withdrawPlayer(LangDict.getString("banking.forCapturing"), player, priceToWithdraw);

        // remove animal
        player.playEffect(near.get(entityIndexLowest).getLocation(), Effect.ELECTRIC_SPARK, null);
        player.playSound(near.get(entityIndexLowest).getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 0);
        near.get(entityIndexLowest).remove();
        return true;
    }
}
