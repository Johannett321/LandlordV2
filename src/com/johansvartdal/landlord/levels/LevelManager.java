package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LevelManager {

    private Main plugin;

    private static Level currentLevel;
    private Level[] allLevels;
    private ArrayList<String> acceptedPlayers = new ArrayList<>();
    private static final HashMap<String, LvlSeasonRelation> featureLevels = new HashMap<>();

    public LevelManager(Main plugin) {
        this.plugin = plugin;
        load();
        populateCommandLevels();
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

        // update scoreboard
        Main.scoreboardHelper.warnNewLevel(getCurrentDisplaySeasonNum(), getCurrentDisplayLevelNum());
    }

    public void proceedToNextLevel() {
        currentLevel = getLevel(currentLevel.getLevelNumber()+1);
        acceptedPlayers.clear();

        Tools.broadcastMessage("The town was just upgraded to level " + currentLevel.getDisplayLevelNumber() + "!", ChatColor.GREEN);
        currentLevel.justUpgraded();
        save();

        // update scoreboard
        Main.scoreboardHelper.warnNewLevel(getCurrentDisplaySeasonNum(), getCurrentDisplayLevelNum());
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
        checkIfUpgradeShouldBeScheduled(false);
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

    public void forceUpgrade(Player player) {
        if (currentLevel.getRemainingItemsForNextLevel().size() > 0) {
            Tools.tellPlayer(player, "You cannot force upgrade without all items being collected", ChatColor.RED);
            return;
        }
        checkIfUpgradeShouldBeScheduled(true);
    }

    public void playerAcceptsUpgrade(Player player) {
        God.speak("Citizens, " + player.getDisplayName() + " just accepted the upgrade!");
        acceptedPlayers.add(player.getDisplayName());

        checkIfUpgradeShouldBeScheduled(false);
    }

    public boolean playerHasAccepted(Player player) {
        if (acceptedPlayers.contains(player.getDisplayName())) {
            return true;
        }
        return false;
    }

    private void checkIfUpgradeShouldBeScheduled(boolean force) {
        // make sure no items remain
        if (currentLevel.getRemainingItemsForNextLevel().size() > 0) {
            return;
        }

        // proceed if everyone accepted or force is true
        if (acceptedPlayers.size() >= Main.playerDataManager.getPlayerDataList().size() ||  force) {
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

    public static Integer getCurrentDisplayLevelNum() {
        if (currentLevel == null) {
            return 0;
        }
        return currentLevel.getDisplayLevelNumber();
    }

    public static int getCurrentDisplaySeasonNum() {
        return 1;
    }

    public int getRouletteGamePrice() {
        return currentLevel.getRouletteGamePrice();
    }

    public Location getWildernessLocation() {
        Location location = new Location(Bukkit.getWorld("world"), 15000,0,15000);
        switch (getCurrentDisplaySeasonNum()) {
            case 1:
                break;
            case 2:
                location = new Location(Bukkit.getWorld("world"), 30000,0,30000);
                break;
            case 3:
                location = new Location(Bukkit.getWorld("world"), 60000,0,60000);
                break;
        }

        Tools.highestStandingPoint(location);
        return location;
    }

    public int getWildernessPrice() {
        switch (getCurrentDisplaySeasonNum()) {
            case 1:
                return 100;
            case 2:
                return 200;
            case 3:
                return 300;
        }
        return 0;
    }

    public int getNetherWildernessPrice() {
        switch (getCurrentDisplaySeasonNum()) {
            case 1:
                return 200;
            case 2:
                return 300;
            case 3:
                return 400;
        }
        return 0;
    }

    public ArrayList<ItemStack> getListOfRemainingItems() {
        return currentLevel.getRemainingItemsForNextLevel();
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        return currentLevel.getRequiredItemsForNextLevel();
    }

    public ItemStack getRemainingItem(Material type) {
        for (ItemStack itemStack : currentLevel.getRemainingItemsForNextLevel()) {
            if (itemStack.getType().equals(type)) {
                return itemStack;
            }
        }
        return null;
    }

    private static class LvlSeasonRelation {
        int seasonNum;
        int levelNum;
        public LvlSeasonRelation(int seasonNum, int levelNum) {
            this.seasonNum = seasonNum;
            this.levelNum = levelNum;
        }
    }

    private static void populateCommandLevels() {
        // season 1
        featureLevels.put("roulette", new LvlSeasonRelation(1,2));
        featureLevels.put("home", new LvlSeasonRelation(1,3));
        featureLevels.put("wildworld", new LvlSeasonRelation(1,4));
        featureLevels.put("capture", new LvlSeasonRelation(1,5));
        featureLevels.put("visit", new LvlSeasonRelation(1,7));

        // season 2
        featureLevels.put("stocks", new LvlSeasonRelation(2,1));
        featureLevels.put("wildnether", new LvlSeasonRelation(2,2));
        featureLevels.put("wildmining", new LvlSeasonRelation(2,3));
        featureLevels.put("day", new LvlSeasonRelation(2,4));
    }

    public static boolean featureUnlocked(String command) {
        // always allow while in DEBUG MODE
        if (Properties.DEBUG_MODE) {
            return true;
        }

        // make sure the command requires a level
        if (!featureLevels.containsKey(command.toLowerCase())) {
            return true;
        }

        // check season
        LvlSeasonRelation lvlSeasonRelation = featureLevels.get(command);
        if (lvlSeasonRelation.seasonNum < getCurrentDisplaySeasonNum()) {
            // season already completed
            return true;
        }else if (lvlSeasonRelation.seasonNum > getCurrentDisplaySeasonNum()) {
            // season not even started
            return false;
        }

        // is the level of the command lower or equals than the current display level num?
        return lvlSeasonRelation.levelNum <= getCurrentDisplayLevelNum();
    }
}
