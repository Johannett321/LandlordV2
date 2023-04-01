package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level3 extends Level{

    public Level3(Main plugin) {
        super(plugin, 1, 3);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 80 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR, 448 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 144 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LOG, 176 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("level3Welcome1"));
        God.speak(LangDict.getString("level3Welcome2"));
    }

    @Override
    public int getRouletteGamePrice() {
        return 300;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }
}
