package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S2Level1 extends Level{

    public S2Level1(Main plugin) {
        super(plugin, 2, 1);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.TUFF, 40 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.POTATO, 240 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CARROT, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SEA_PICKLE, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.PUMPKIN, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COCOA_BEANS, 256 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.OAK_LOG, 176 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 144 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.EGG, 24));
        requiredItems.add(new ItemStack(Material.WHITE_WOOL, 21 * Main.properties.getNumberOfPlayers()));
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
