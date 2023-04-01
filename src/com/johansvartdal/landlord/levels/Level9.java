package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level9 extends Level{

    public Level9(Main plugin) {
        super(plugin, 1, 9);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();
        //TODO NOT DONE!!
        requiredItems.add(new ItemStack(Material.COBBLESTONE, 300 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 240 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CACTUS, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.KELP, 64 * Main.properties.getNumberOfPlayers()));
        return requiredItems;
    }

    @Override
    public void justUpgraded() {

    }

    @Override
    public int getRouletteGamePrice() {
        return 700;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }
}
