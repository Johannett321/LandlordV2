package com.johansvartdal.landlord.mysterychest;

import com.johansvartdal.landlord.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BasicMysteryChest extends MysteryChest {
    @Override
    public ChatColor getChestTierChatColor() {
        return ChatColor.GRAY; // Represents Basic tier
    }

    @Override
    public String getChestTierName() {
        return "BASIC";
    }

    @Override
    public ItemStack[] getFillerItems() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Filler items (useful but common)
        items.add(new ItemStack(Material.DIRT, 8 * Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.BREAD, 3 * Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.COAL, 4 * Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.BOOK, Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.CARROT, 2));
        items.add(new ItemStack(Material.POTATO, 2));

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier1Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        items.add(new ItemStack(Material.STONE_SWORD));

        items.add(new ItemStack(Material.BEEF, 8* Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.CAKE, Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.COAL, 6 * Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.IRON_INGOT, 4 * Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.BLAZE_ROD, 2 * Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.SLIME_BALL, 2 * Main.properties.getNumberOfPlayers()));

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier2Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        items.add(new ItemStack(Material.IRON_SWORD, 1));

        items.add(new ItemStack(Material.IRON_PICKAXE, 1));

        items.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
        items.add(new ItemStack(Material.IRON_HELMET, 1));

        items.add(new ItemStack(Material.EMERALD, 4));

        items.add(new ItemStack(Material.DIAMOND, 2));

        return items.toArray(ItemStack[]::new);
    }
}