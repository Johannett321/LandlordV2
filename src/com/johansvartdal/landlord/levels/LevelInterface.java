package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Challenge;
import com.johansvartdal.landlord.LandlordEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface LevelInterface {

    public int getLevelNumber();
    public ArrayList<ItemStack> getRequiredItemsForNextLevel();
    public void load();

    public void justUpgraded();

    public Challenge getUpgradeChallenge();
    public LandlordEvent getUpgradeEvent();
}
