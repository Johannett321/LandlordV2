package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level6 extends Level{

    public Level6(Main plugin) {
        super(plugin, 1, 6);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 64 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR, 448 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BIRCH_LOG, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SNOWBALL, 8 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
    }

    @Override
    public int getRouletteGamePrice() {
        return 600;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }
}
