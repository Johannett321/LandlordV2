package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class LevelManager {

    private Main plugin;

    private Level currentLevel;
    private Level[] allLevels;
    private ArrayList<String> acceptedPlayers = new ArrayList<>();

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
                new Level1(plugin),
                new Level2(plugin)
        };
    }

    public void startLevel1() {
        currentLevel = getLevel(0);
        currentLevel.justUpgraded();
        Main.properties.setGameState(Properties.GameState.NORMAL);
        save();
    }

    public void proceedToNextLevel() {
        currentLevel = getLevel(currentLevel.getLevelNumber()+1);
        acceptedPlayers.clear();

        God.speak("The town was just upgraded to level " + currentLevel.getDisplayLevelNumber() + "!");
        currentLevel.justUpgraded();
        save();
    }

    public void load() {
        JSONObject lvlInfo = Tools.loadJson("Level.json");
        if (lvlInfo != null) {
            currentLevel = getLevel((int) (long) lvlInfo.get("currentLevel"));
            currentLevel.load();
        }
    }

    public void save() {
        JSONObject lvlInfo = new JSONObject();
        lvlInfo.put("currentLevel", currentLevel.getLevelNumber());
        Tools.saveJsonToFile("Level.json", lvlInfo);
    }

    public boolean itemRequiredForUpgrade(ItemStack itemInMainHand) {
        ArrayList<ItemStack> requiredForUpgrade = currentLevel.getRemainingItemsForNextLevel();
        for (int i = 0; i < requiredForUpgrade.size(); i++) {
            if (requiredForUpgrade.get(i).getType() == itemInMainHand.getType()) {
                return true;
            }
        }
        return false;
    }

    public void donateItem(Player player, ItemStack itemStack) {
        currentLevel.donateItem(player, itemStack);
        checkIfUpgradeShouldBeScheduled();
    }

    public String getRemainingItemsText() {
        StringBuilder remainingText = new StringBuilder();

        ArrayList<ItemStack> remaining = currentLevel.getRemainingItemsForNextLevel();
        for (ItemStack itemStack : remaining) {
            if (!remainingText.toString().equals("")) {
                remainingText.append(", ");
            }
            remainingText.append(Tools.getDisplayNameOfItem(itemStack));
            remainingText.append(" ");
            remainingText.append("(");
            remainingText.append(itemStack.getAmount());
            remainingText.append(")");
        }

        if (remainingText.isEmpty()) {
            return "None";
        }

        return remainingText.toString();
    }

    public String getAcceptedPlayersText() {
        return acceptedPlayers.toString();
    }

    public void playerAcceptsUpgrade(Player player) {
        God.speak("Citizens, " + player.getDisplayName() + " just accepted the upgrade!");
        acceptedPlayers.add(player.getDisplayName());

        checkIfUpgradeShouldBeScheduled();
    }

    public boolean playerHasAccepted(Player player) {
        if (acceptedPlayers.contains(player.getDisplayName())) {
            return true;
        }
        return false;
    }

    private void checkIfUpgradeShouldBeScheduled() {
        if (currentLevel.getRemainingItemsForNextLevel().size() > 0) {
            return;
        }
        if (acceptedPlayers.size() >= Main.playerDataManager.getPlayerDataList().size()) {
            proceedToNextLevel();
        }
    }
}
