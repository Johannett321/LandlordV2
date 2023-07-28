package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S2Level3 extends Level{

    public S2Level3(Main plugin) {
        super(plugin, 2, 1);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        //TODO NOT DONE!!
        requiredItems.add(new ItemStack(Material.FLINT, 34 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.POINTED_DRIPSTONE, 23 * Main.properties.getNumberOfPlayers()));


        requiredItems.add(new ItemStack(Material.MANGROVE_LOG, 23 * Main.properties.getNumberOfPlayers()));


        requiredItems.add(new ItemStack(Material.KELP, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.MILK_BUCKET, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COCOA_BEANS, 256 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WHITE_WOOL, 100 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.WARPED_WART_BLOCK, 19 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SHROOMLIGHT, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WEEPING_VINES, 20 * Main.properties.getNumberOfPlayers()));

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
