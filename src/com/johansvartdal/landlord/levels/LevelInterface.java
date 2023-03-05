package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface LevelInterface {

    public int getLevelNumber();
    public ArrayList<ItemStack> getRemainingItemsForNextLevel();
    ArrayList<ItemStack> getRequiredItemsForNextLevel();
    public void load();

    public void justUpgraded();

    public void donateItem(Player player, ItemStack itemStack);

    int getRouletteGamePrice();
}
