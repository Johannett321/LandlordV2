package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class LevelManager {

    private Main plugin;

    private Level currentLevel;
    private Level[] allLevels;
    private ArrayList<String> acceptedPlayers = new ArrayList<>();

    public LevelManager(Main plugin) {
        this.plugin = plugin;
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
        save();
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

    public void load() {
        JSONObject lvlInfo = Tools.loadJson("Level.json");
        if (lvlInfo != null) {
            populateLevels();
            currentLevel = getLevel((int) (long) lvlInfo.get("currentLevel"));
            currentLevel.load();
            if (lvlInfo.containsKey("remainingItems")) {
                this.currentLevel.setRemainingItems(itemsFromJSONArr((JSONArray) lvlInfo.get("remainingItems")));
            }
        }
    }

    public void save() {
        JSONObject lvlInfo = new JSONObject();
        lvlInfo.put("currentLevel", currentLevel.getLevelNumber());
        lvlInfo.put("remainingItems", remainingItemsToJsonArr());
        Tools.saveJsonToFile("Level.json", lvlInfo);
    }

    public JSONArray remainingItemsToJsonArr() {
        ArrayList<ItemStack> currentRemaining = currentLevel.getRemainingItemsForNextLevel();
        JSONArray jsonArray = new JSONArray();

        System.out.println("DEBUG!!!!");
        for (ItemStack itemStack : currentRemaining) {
            JSONObject object = new JSONObject();
            object.put("material", itemStack.getType().toString());
            object.put("amount", itemStack.getAmount());
            System.out.println("Adding: " + object.toJSONString());
            jsonArray.add(object);
        }
        return jsonArray;
    }

    public ArrayList<ItemStack> itemsFromJSONArr(JSONArray jsonArray) {
        ArrayList<ItemStack> itemStackArrayList = new ArrayList<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String matString = (String) jsonObject.get("material");
            int amount = (int)(long) jsonObject.get("amount");

            Material material = Material.getMaterial(matString);
            ItemStack itemStack = new ItemStack(material);
            itemStack.setAmount(amount);
            itemStackArrayList.add(itemStack);
        }
        return itemStackArrayList;
    }

    public int getCurrentDisplayLevelNum() {
        return currentLevel.getDisplayLevelNumber();
    }
}
