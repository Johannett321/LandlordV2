package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level10 extends Level{

    public Level10(Main plugin) {
        super(plugin, 2, 1);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

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
}
