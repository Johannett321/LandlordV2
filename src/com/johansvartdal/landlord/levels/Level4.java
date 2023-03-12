package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level4 extends Level{

    public Level4(Main plugin) {
        super(plugin, 1, 4);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 192 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.PUMPKIN_SEEDS, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 144 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 144 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
    }

    @Override
    public int getRouletteGamePrice() {
        return 500;
    }
}
