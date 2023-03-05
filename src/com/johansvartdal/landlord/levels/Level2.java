package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level2 extends Level{

    public Level2(Main plugin) {
        super(plugin, 2);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        ItemStack cobble = new ItemStack(Material.COBBLESTONE, 16 * Main.properties.getNumberOfPlayers());
        requiredItems.add(cobble);

        ItemStack wheat = new ItemStack(Material.LEGACY_CROPS, 64 * Main.properties.getNumberOfPlayers());
        requiredItems.add(wheat);

        return requiredItems;
    }

    @Override
    public void justUpgraded() {

    }

    @Override
    public int getRouletteGamePrice() {
        return 800;
    }
}
