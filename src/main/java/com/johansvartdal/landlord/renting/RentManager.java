package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public class RentManager {

    public static class InventoryWatcher implements Listener {
        private Main plugin;
        public InventoryWatcher(Main plugin) {
            this.plugin = plugin;
        }
        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent event) {
            Item droppedItem = event.getItemDrop();
            ItemStack itemStack = droppedItem.getItemStack();

            if (itemIsRented(itemStack)) {
                // The player has attempted to drop a rented item
                event.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryClick(InventoryClickEvent event) {
            InventoryAction action = event.getAction();

            // Was the item clicked or moved?
            if (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD || event.getClick().isKeyboardClick()) {

                // get the hotbar slot
                int hotbarSlot = event.getHotbarButton();
                PlayerInventory playerInventory = event.getWhoClicked().getInventory();
                ItemStack movedItem = playerInventory.getItem(hotbarSlot);
                if (itemIsRented(movedItem)) {
                    event.setCancelled(true);
                }
                return;
            }

            // Check if the clicked inventory is NOT the player's inventory
            if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
                ItemStack itemInCursor = event.getCursor();
                if (itemInCursor == null) {
                    event.setCancelled(true);
                    return;
                }

                // remove if item is rented
                if (itemIsRented(itemInCursor)) {
                    event.setCancelled(true);
                }
                return;
            }

            // shift click
            if (event.getClick().isShiftClick()) {
                ItemStack clickedItem = event.getCurrentItem();
                if (itemIsRented(clickedItem)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            Inventory inventory = event.getInventory();
            InventoryType inventoryType = inventory.getType();

            // Check if the closed inventory is a chest or other container
            if (inventoryType != InventoryType.PLAYER && inventoryType != InventoryType.CREATIVE) {
                for (ItemStack content : inventory.getContents()) {
                    if (itemIsRented(content)) {
                        inventory.remove(content);
                        Tools.tellPlayer(event.getPlayer(), LangDict.getString("itemRent.stolenItemFound"), ChatColor.RED);

                        // cleanup rent
                        RentableItem rentableItem = getRentedItem(content);
                        if (rentableItem != null) {
                            rentableItem.endRentCleanup(true);
                        }
                    }
                }
            }
        }
    }

    private static final ArrayList<RentableItem> rentedItems = new ArrayList<>();

    public static void cancelRentOfItem(Player player) {
        for (RentableItem rentedItem : rentedItems) {
            if (rentedItem.equalsTheRentedItem(player.getInventory().getItemInMainHand())) {
                rentedItem.attemptEndRent();
                return;
            }
        }
    }

    public static void registerListeners(Main plugin) {
        InventoryWatcher inventoryWatcher = new InventoryWatcher(plugin);
        plugin.getServer().getPluginManager().registerEvents(inventoryWatcher, plugin);
    }

    public static void rentItem(Player player, RentableItem rentableItem) {
        rentableItem.startRentForPlayer(player);
        rentedItems.add(rentableItem);
    }

    public static void notifyItemRentEnded(RentableItem rentableItem) {
        rentedItems.remove(rentableItem);
    }

    public static boolean itemIsRented(ItemStack itemStack) {
        for (RentableItem rentedItem : rentedItems) {
            if (rentedItem.equalsTheRentedItem(itemStack)) {
                return true;
            }
        }
        return false;
    }

    private static RentableItem getRentedItem(ItemStack itemStack) {
        for (RentableItem rentedItem : rentedItems) {
            if (rentedItem.equalsTheRentedItem(itemStack)) {
                return rentedItem;
            }
        }
        return null;
    }

    public static boolean playerCurrentlyRentingItem(RentableItem rentableItem) {
        for (RentableItem rentedItem : rentedItems) {
            // the player already rents this
            if (rentedItem.getClass().equals(rentableItem.getClass())) {
                return true;
            }
        }
        return false;
    }

    public static void forceEndAllRents() {
        for (RentableItem rentedItem : rentedItems) {
            rentedItem.forceEndRent();
        }
    }
}
