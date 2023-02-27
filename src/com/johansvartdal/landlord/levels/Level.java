package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEventInterface;
import com.johansvartdal.landlord.Main;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class Level implements LevelInterface {

    private Main plugin;
    private int levelNumber;

    public Level(Main plugin, int levelNumber) {
        this.plugin = plugin;
        this.levelNumber = levelNumber;
    }

    @Override
    public int getLevelNumber() {
        return levelNumber-1;
    }

    public int getDisplayLevelNumber() {
        return levelNumber;
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        return null;
    }

    @Override
    public void load() {

    }
}
