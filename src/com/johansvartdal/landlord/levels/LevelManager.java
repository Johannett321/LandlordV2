package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.json.simple.JSONObject;

public class LevelManager {

    private Main plugin;

    private Level currentLevel;
    private Level[] allLevels;

    public LevelManager(Main plugin) {
        this.plugin = plugin;
        populateLevels();
        load();
    }

    public Level getLevel(int levelNum) {
        return allLevels[levelNum];
    }

    public void populateLevels() {
        allLevels = new Level[] {
                new Level1(plugin)
        };
    }

    public void startLevel1() {
        currentLevel = getLevel(0);
        currentLevel.justUpgraded();
        Main.properties.setGameState(Properties.GameState.NORMAL);
        save();
    }

    public void proceedToNextLevel() {
        Challenge challenge = currentLevel.getUpgradeChallenge();
        if (challenge != null) {
            challenge.setOnEventEndListener(new OnLandlordEventEndListener() {
                @Override
                public void onEnd() {
                    challengeCompleted();
                }
            });
            Main.properties.setGameState(Properties.GameState.EVENT_RUNNING);
            challenge.startEvent();
        }else {
            challengeCompleted();
        }
    }

    private void challengeCompleted() {
        LandlordEvent event = currentLevel.getUpgradeEvent();
        if (event != null) {
            event.setOnEventEndListener(new OnLandlordEventEndListener() {
                @Override
                public void onEnd() {
                    performUpgrade();
                }
            });
            Main.properties.setGameState(Properties.GameState.EVENT_RUNNING);
            event.startEvent();
        }else {
            performUpgrade();
        }
    }

    private void performUpgrade() {
        int nextLvlNum = currentLevel.getLevelNumber() + 1;
        nextLvlNum ++;
        currentLevel = getLevel(nextLvlNum);
        currentLevel.justUpgraded();
        Main.properties.setGameState(Properties.GameState.NORMAL);
        save();
    }

    public void load() {
        JSONObject lvlInfo = Tools.loadJson("Level.json");
        if (lvlInfo != null) {
            currentLevel = getLevel((int) (long) lvlInfo.get("currentLevel"));
        }
    }

    public void save() {
        JSONObject lvlInfo = new JSONObject();
        lvlInfo.put("currentLevel", currentLevel.getLevelNumber());
        Tools.saveJsonToFile("Level.json", lvlInfo);
    }
}
