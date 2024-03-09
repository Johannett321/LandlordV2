package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.scheduler.BukkitTask;

import java.util.PrimitiveIterator;

import static com.johansvartdal.landlord.Tools.errorLog;

public abstract class RentableItem implements Listener {

    @Getter
    private ItemStack rentedItem;
    @Getter
    private Player player;
    private final Main plugin;
    private BukkitTask renewalLoop = null;
    private BukkitTask repairLoop = null;
    private int renewalSeconds = 120;

    public RentableItem(Main plugin) {
        this.plugin = plugin;
    }

    public void startRentForPlayer(Player player) {
        this.player = player;

        // give item to player
        rentedItem = modifyCraftedItem(craftItem());
        Tools.givePlayerItemOrDrop(player, rentedItem, true);

        // schedule renewal
        scheduleRenewal();

        // repair loop
        itemRepairLoop();
    }

    protected void itemRepairLoop() {
        ItemMeta itemMeta = rentedItem.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(rentedItem.getType().getMaxDurability()/2);
            rentedItem.setItemMeta(damageable);
        }

        // schedule new repair
        repairLoop = Bukkit.getScheduler().runTaskLater(plugin, this::itemRepairLoop, Tools.secToTicks(3));
    }

    protected ItemStack modifyCraftedItem(ItemStack craftedItem) {
        ItemMeta itemMeta = craftedItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(LangDict.getString("itemRent.rentedPrefix") + getItemName());
        }

        if (itemMeta instanceof Repairable repairable) {
            repairable.setRepairCost(98);
        }

        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(craftedItem.getType().getMaxDurability()-(560/3));
            damageable.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        }

        if (itemMeta != null) {
            craftedItem.setItemMeta(itemMeta);
        }
        craftedItem.addEnchantment(Enchantment.DURABILITY, 3);
        craftedItem.addEnchantment(Enchantment.VANISHING_CURSE, 1);
        craftedItem.setAmount(1);
        return craftedItem;
    }

    private void scheduleRenewal() {
        renewalLoop = Bukkit.getScheduler().runTaskLater(plugin, this::renewItem, Tools.secToTicks(renewalSeconds));
    }

    public boolean equalsTheRentedItem(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        // check type
        if (itemStack.getType() != rentedItem.getType()) {
            return false;
        }

        // check meta
        ItemMeta rentedMeta = rentedItem.getItemMeta();
        if (rentedMeta != null) {
            ItemMeta itemToCheck = itemStack.getItemMeta();
            if (itemToCheck == null) {
                return false;
            }

            if (!itemToCheck.getDisplayName().equals(rentedMeta.getDisplayName())) {
                return false;
            }

            if (!itemToCheck.getEnchants().equals(rentedMeta.getEnchants())) {
                return false;
            }
        }

        return true;
    }

    private static final int CHESTPLATE = 122131;
    private static final int HELMET = 5767445;
    private static final int CURSOR = 352342;
    private static final int SECOND_HAND = 982373;
    private static final int NOT_FOUND = -1;

    private int getIndexOfRentedItem() {
        for (int i = 0; i < 36; i++) {
            ItemStack currentItem = player.getInventory().getItem(i);
            if (equalsTheRentedItem(currentItem)) {
                return i;
            }
        }

        // does the player wear the item?
        if (equalsTheRentedItem(player.getInventory().getChestplate())) {
            return CHESTPLATE;
        }

        // does the player wear the item?
        if (equalsTheRentedItem(player.getInventory().getHelmet())) {
            return HELMET;
        }

        // does the player have it in their cursor?
        if (equalsTheRentedItem(player.getItemOnCursor())) {
            return CURSOR;
        }

        // does the player have it in their second hand?
        if (equalsTheRentedItem(player.getInventory().getItemInOffHand())) {
            return SECOND_HAND;
        }
        return NOT_FOUND;
    }

    private void renewItem() {
        // try to find the item in the players inventory
        int indexOfRentedItem = getIndexOfRentedItem();

        // if item is not present, attempt to purchase item
        if (indexOfRentedItem == NOT_FOUND) {
            if (!Bank.playerCanAfford(player, getItemPurchaseFullPrice())) {
                // send to jail as player could not afford to purchase item
                Bank.bankruptPlayer(player);
                JailManager.sendToJail(plugin, player, LangDict.getString("itemRent.attemptingToStealA") + getItemName(), 60*8);
                RentManager.notifyItemRentEnded(this);
                return;
            }

            // withdraw full price
            Tools.tellPlayer(player, LangDict.getString("itemRent.theRented") + getItemName() + LangDict.getString("itemRent.itemNotFoundInInv"), ChatColor.RED);
            Bank.withdrawPlayer(LangDict.getString("itemRent.paidFullPrice") + getItemName(), player, getItemPurchaseFullPrice());
            RentManager.notifyItemRentEnded(this);
            return;
        }

        // player cannot afford
        if (!Bank.playerCanAfford(player, getItemRentPrice())) {
            removeItemFromPlayersInv(indexOfRentedItem);
            RentManager.notifyItemRentEnded(this);
            Tools.tellPlayer(player, LangDict.getString("itemRent.gaveBackStart") + getItemName() + LangDict.getString("itemRent.gaveBackEnd"), ChatColor.RED);
            return;
        }

        // renew item, and reschedule
        Bank.withdrawPlayer(LangDict.getString("itemRent.renewingRented") + getItemName(), player, getItemRentPrice());
        scheduleRenewal();
    }

    private void removeItemFromPlayersInv(int indexOfRentedItem) {
        // not found
        if (indexOfRentedItem == NOT_FOUND) {
            errorLog("Attempted to remove a rented item from a players inventory. It was found, when looking, but could not be found when about to be removed.");
            return;
        }

        // remove chestplate
        if (indexOfRentedItem == CHESTPLATE) {
            player.getInventory().setChestplate(null);
            return;
        }

        // remove helmet
        if (indexOfRentedItem == HELMET) {
            player.getInventory().setHelmet(null);
            return;
        }

        // remove cursor
        if (indexOfRentedItem == CURSOR) {
            player.setItemOnCursor(null);
            return;
        }

        // remove second hand
        if (indexOfRentedItem == SECOND_HAND) {
            player.getInventory().setItemInOffHand(null);
            return;
        }

        // remove normal inventory item
        player.getInventory().clear(indexOfRentedItem);
    }

    public void attemptEndRent() {
        // make sure correct item is held
        if (!equalsTheRentedItem(player.getInventory().getItemInMainHand())) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("itemRent.pleaseHoldMainHand"));
            return;
        }

        // cleanup
        endRentCleanup(true);

        // remove item
        player.getInventory().remove(player.getInventory().getItemInMainHand());
        Tools.tellPlayer(player, LangDict.getString("itemRent.deliveredBackRented") + getItemName(), ChatColor.GREEN);
    }

    public void forceEndRent() {
        // remove the item
        player.getInventory().remove(rentedItem);
        if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().equals(rentedItem)) {
            player.getInventory().setChestplate(null);
        }else if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().equals(rentedItem)) {
            player.getInventory().setHelmet(null);
        }

        // cleanup
        endRentCleanup(false);
    }

    /**
     * this is the preferred method to call to end the rent
     * @param notifyManager
     */
    public void endRentCleanup(boolean notifyManager) {
        // cancel renewalLoop
        if (renewalLoop != null) {
            renewalLoop.cancel();
        }

        // cancel repair loop
        if (repairLoop != null) {
            repairLoop.cancel();
        }

        if (notifyManager) {
            RentManager.notifyItemRentEnded(this);
        }
    }

    public abstract String getItemName();
    public abstract ItemStack craftItem();
    public abstract int getItemRentPrice();
    protected abstract int getItemPurchaseFullPrice();
}
