package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.LandlordEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface LevelInterface {

    ArrayList<ItemStack> getRequiredItemsForNextLevel();
    void justUpgraded();
    int getRouletteGamePrice();
    Book getBook();

    LandlordEvent getEventToStartBeforeLevel();
}
