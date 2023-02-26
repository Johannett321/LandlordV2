package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEvent;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.w3c.dom.events.Event;

import java.util.ArrayList;

public interface LevelInterface {

    public int getLevelNumber();
    public ArrayList<ItemStack> getRequiredItemsForNextLevel();

    public void beforeUpgradeChallengeEvent();
    public Event upgradeChallengeEvent();
    public void afterUpgradeChallengeEvent();

    public void beforeUpgradeEvent();
    public LandlordEvent upgradeEvent();
    public void afterUpgradeEvent();
}
