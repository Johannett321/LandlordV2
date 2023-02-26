package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import org.bukkit.entity.Item;
import org.w3c.dom.events.Event;

import java.util.ArrayList;

public abstract class Level implements LevelInterface {

    private Main plugin;
    private int levelNumber;

    public Level(Main plugin, int levelNumber) {
        this.levelNumber = levelNumber;
    }

    @Override
    public int getLevelNumber() {
        return levelNumber;
    }

    @Override
    public void beforeUpgradeChallengeEvent() {

    }

    @Override
    public Event upgradeChallengeEvent() {
        return null;
    }

    @Override
    public void afterUpgradeChallengeEvent() {

    }

    @Override
    public void beforeUpgradeEvent() {

    }

    @Override
    public LandlordEvent upgradeEvent() {
        return null;
    }

    @Override
    public void afterUpgradeEvent() {

    }
}
