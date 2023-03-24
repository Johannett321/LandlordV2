package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.events.adventure.ValleyVillageAdventure;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level5 extends Level{

    public Level5(Main plugin) {
        super(plugin, 1, 5);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 64 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.CACTUS, 50 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.LAVA_BUCKET, 2));
        requiredItems.add(new ItemStack(Material.IRON_BLOCK, 2 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.POPPY, 8 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
    }

    @Override
    public int getRouletteGamePrice() {
        return 500;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return new ValleyVillageAdventure(plugin);
    }
}
