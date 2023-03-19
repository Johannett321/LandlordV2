package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface LevelInterface {

    ArrayList<ItemStack> getRequiredItemsForNextLevel();
    void justUpgraded();
    int getRouletteGamePrice();
    ItemStack getBook();

    LandlordEvent getLevelStartEvent();
}
