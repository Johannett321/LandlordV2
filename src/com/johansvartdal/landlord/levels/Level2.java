package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Main;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level2 extends Level{

    public Level2(Main plugin) {
        super(plugin, 2);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        return null;
    }

    @Override
    public void justUpgraded() {

    }
}
