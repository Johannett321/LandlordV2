package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.Bank;
import com.johansvartdal.landlord.JailManager;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.scheduler.BukkitTask;

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

        player.getInventory().addItem(rentedItem);

        // schedule renewal
        scheduleRenewal();

        // repair loop
        itemRepairLoop();
    }

    protected void itemRepairLoop() {
        ItemMeta itemMeta = rentedItem.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(rentedItem.getType().getMaxDurability()/2);
        }

        // schedule new repair
        repairLoop = Bukkit.getScheduler().runTaskLater(plugin, this::itemRepairLoop, Tools.secToTicks(10));
    }

    protected ItemStack modifyCraftedItem(ItemStack craftedItem) {
        ItemMeta itemMeta = craftedItem.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("[RENTED] " + getItemName());
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

    private void renewItem() {
        // try to find the item in the players inventory
        boolean itemIsPresentInInventory = false;
        for (int i = 0; i < 36; i++) {
            ItemStack currentItem = player.getInventory().getItem(i);
            if (equalsTheRentedItem(currentItem)) {
                itemIsPresentInInventory = true;
            }
        }

        // does the player wear the item?
        if (equalsTheRentedItem(player.getInventory().getChestplate()) || equalsTheRentedItem(player.getInventory().getHelmet())) {
            itemIsPresentInInventory = true;
        }

        // does the player have it in their cursor?
        if (equalsTheRentedItem(player.getItemOnCursor())) {
            itemIsPresentInInventory = true;
        }

        // does the player have it in their second hand?
        if (equalsTheRentedItem(player.getInventory().getItemInOffHand())) {
            itemIsPresentInInventory = true;
        }

        // if item is not present, attempt to purchase item
        if (!itemIsPresentInInventory) {
            if (!Bank.playerCanAfford(player, getItemPurchaseFullPrice())) {
                // send to jail as player could not afford to purchase item
                JailManager.sendToJail(plugin, player, "attempting to steal a " + getItemName(), 60*8);
                RentManager.notifyItemRentEnded(this);
                return;
            }

            // withdraw full price
            Tools.tellPlayer(player, "The rented " + getItemName() + " was not found in your inventory, and you had to pay the full price of the item!", ChatColor.RED);
            Bank.withdrawPlayer("the full price of the rented " + getItemName(), player, getItemPurchaseFullPrice());
            RentManager.notifyItemRentEnded(this);
            return;
        }

        // player cannot afford
        if (!Bank.playerCanAfford(player, getItemRentPrice())) {
            player.getInventory().remove(rentedItem);
            RentManager.notifyItemRentEnded(this);
            Tools.tellPlayer(player, "You gave back the " + getItemName() + ", as you could not afford to use it anymore", ChatColor.RED);
            return;
        }

        // renew item, and reschedule
        Bank.withdrawPlayer("renewing the rented " + getItemName(), player, getItemRentPrice());
        scheduleRenewal();
    }

    public void attemptEndRent() {
        // make sure correct item is held
        if (!equalsTheRentedItem(player.getInventory().getItemInMainHand())) {
            Tools.tellPlayer(new ErrorChat(), player, "You must hold the " + getItemName() + " in your hand first when typing this command");
            return;
        }

        // cleanup
        endRentCleanup(true);

        // remove item
        player.getInventory().remove(player.getInventory().getItemInMainHand());
        Tools.tellPlayer(player, "Your rent of " + getItemName() + " ended", ChatColor.GREEN);
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

    public void endRentCleanup(boolean notifyManager) {
        // this is the preferred method to call to end the rent

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
