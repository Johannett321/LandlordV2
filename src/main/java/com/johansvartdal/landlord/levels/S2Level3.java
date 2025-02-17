package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.events.LandlordEvent;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S2Level3 extends Level{

    public S2Level3(Main plugin) {
        super(plugin, 2, 3);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 170 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.MANGROVE_LOG, 23 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.KELP, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BAMBOO_MOSAIC, 11 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_MUTTON, 5 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_SALMON, 7 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.DIAMOND, 2 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.RAW_IRON, 16 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.GOLD_INGOT, 19 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.EMERALD, Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {

    }

    @Override
    public int getRouletteGamePrice() {
        return 800;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }

    @Override
    public Book getBook() {
        return null;
    }
}
