package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.events.arenafight.ArenaFight1;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S2Level2 extends Level{

    public S2Level2(Main plugin) {
        super(plugin, 2, 2);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.FLINT, 34 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CLAY, 28 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.POINTED_DRIPSTONE, 23 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.CHERRY_SAPLING, 37 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SPRUCE_LOG, 70*Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.KELP, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.HAY_BLOCK, 25 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.NETHER_WART, 30 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.GLOWSTONE, 16 * Main.properties.getNumberOfPlayers()));
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
        return new ArenaFight1(plugin);
    }

    @Override
    public Book getBook() {
        return null;
    }
}
