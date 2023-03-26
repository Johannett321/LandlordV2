package com.johansvartdal.landlord;

import com.johansvartdal.landlord.lan.AudioLayer;
import com.johansvartdal.landlord.lan.LanController;
import com.johansvartdal.landlord.levels.*;
import com.johansvartdal.landlord.playerevents.PlayerEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LevelManager {

    private static Main plugin;

    private static Level currentLevel;
    private static Level[] allLevels;
    private static ArrayList<String> acceptedPlayers = new ArrayList<>();
    private static final HashMap<String, LvlSeasonRelation> featureLevels = new HashMap<>();

    public static void init(Main plugin) {
        LevelManager.plugin = plugin;
        load();
        populateCommandLevels();
    }

    public static Level getLevel(int levelNum) {
        return allLevels[levelNum];
    }

    public static void populateLevels() {
        allLevels = new Level[] {
                new Level1(plugin),
                new Level2(plugin),
                new Level3(plugin),
                new Level4(plugin),
                new Level5(plugin)
        };
    }

    public static void startLevel1() {
        System.out.println("Loading level 1...");
        if (allLevels == null) {
            System.out.println("WARNING! Attempting to load level 1, but we have not created level1");
        }
        currentLevel = getLevel(0);
        currentLevel.justUpgraded();
        Main.properties.setGameState(Properties.GameState.NORMAL);
        save();

        // receive book
        if (currentLevel.getBook() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().addItem(currentLevel.getBook());
            }
        }

        // update scoreboard
        Main.scoreboardHelper.warnNewLevel(getCurrentDisplaySeasonNum(), getCurrentDisplayLevelNum());
    }

    private static void proceedToNextLevel() {

        // end all events
        if (PlayerEventManager.anyPlayersInEvent()) {
            PlayerEventManager.forceEndAllEvents();
        }

        // get new level
        currentLevel = getLevel(currentLevel.getLevelNumber()+1);
        acceptedPlayers.clear();

        // run upgrade and inform
        Tools.broadcastMessage("Congratulations! The town was just upgraded to level " + currentLevel.getDisplayLevelNumber() + "!", ChatColor.GREEN);
        currentLevel.justUpgraded();

        // save
        save();

        // receive book
        if (currentLevel.getBook() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().addItem(currentLevel.getBook());
            }
        }

        // give everyone a chunk
        Main.playerDataManager.giveEveryoneChunkPoints(1);

        // update scoreboard
        Main.scoreboardHelper.warnNewLevel(getCurrentDisplaySeasonNum(), getCurrentDisplayLevelNum());

        // effects
        SpecialEffects.blastFireworks(6);
        LanController.getLightsController().playLevelUpEffect();

        // start event if any
        LandlordEventManager.notifyLevelReached(currentLevel);
    }

    public static void forceProceedToNextLevel() {
        if (!Properties.DEBUG_MODE) {
            return;
        }
        proceedToNextLevel();
    }

    public static boolean itemRequiredForUpgrade(ItemStack itemInMainHand) {
        ArrayList<ItemStack> requiredForUpgrade = currentLevel.getRemainingItemsForNextLevel();
        for (int i = 0; i < requiredForUpgrade.size(); i++) {
            if (requiredForUpgrade.get(i).getType() == itemInMainHand.getType()) {
                return true;
            }
        }
        return false;
    }

    public static void donateItem(Player player, ItemStack itemStack) {
        currentLevel.donateItem(player, itemStack);
        checkIfUpgradeShouldBeScheduled(false);
        save();
    }

    public static String getRemainingItemsText() {
        StringBuilder remainingText = new StringBuilder();

        if (currentLevel == null) {
            return "Not available at the moment";
        }

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

    public static String getAcceptedPlayersText() {
        return acceptedPlayers.toString();
    }

    public static void forceUpgrade(Player player) {
        if (currentLevel.getRemainingItemsForNextLevel().size() > 0) {
            Tools.tellPlayer(player, "You cannot force upgrade without all items being collected", ChatColor.RED);
            return;
        }
        checkIfUpgradeShouldBeScheduled(true);
    }

    public static void playerAcceptsUpgrade(Player player) {
        God.speak("Citizens, " + player.getDisplayName() + " just accepted the upgrade!");
        acceptedPlayers.add(player.getDisplayName());

        checkIfUpgradeShouldBeScheduled(false);
    }

    public static boolean playerHasAccepted(Player player) {
        if (acceptedPlayers.contains(player.getDisplayName())) {
            return true;
        }
        return false;
    }

    private static void checkIfUpgradeShouldBeScheduled(boolean force) {
        // make sure no items remain
        if (currentLevel.getRemainingItemsForNextLevel().size() > 0) {
            return;
        }

        // proceed if everyone accepted or force is true
        if (acceptedPlayers.size() >= Main.playerDataManager.getPlayerDataList().size() ||  force) {
            proceedToNextLevel();
        }
    }

    public static void load() {
        JSONObject lvlInfo = Tools.loadJson("Level.json");
        populateLevels();
        if (lvlInfo != null) {
            currentLevel = getLevel((int) (long) lvlInfo.get("currentLevel"));
            if (lvlInfo.containsKey("remainingItems")) {
                currentLevel.setRemainingItems(itemsFromJSONArr((JSONArray) lvlInfo.get("remainingItems")));
            }
        }
    }

    public static void save() {
        JSONObject lvlInfo = new JSONObject();
        lvlInfo.put("currentLevel", currentLevel.getLevelNumber());
        lvlInfo.put("remainingItems", remainingItemsToJsonArr());
        Tools.saveJsonToFile("Level.json", lvlInfo);
    }

    public static JSONArray remainingItemsToJsonArr() {
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

    public static ArrayList<ItemStack> itemsFromJSONArr(JSONArray jsonArray) {
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
        if (currentLevel == null) {
            System.out.println("Warning! Seasong is 0");
            return 0;
        }
        System.out.println("Returning season: " + currentLevel.getDisplaySeasonNumber());
        return currentLevel.getDisplaySeasonNumber();
    }

    public static int getRouletteGamePrice() {
        return currentLevel.getRouletteGamePrice();
    }

    public static Location getWildernessLocation() {
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

    public static int getWildernessPrice() {
        return switch (getCurrentDisplaySeasonNum()) {
            case 1 -> 300;
            case 2 -> 600;
            case 3 -> 900;
            default -> 0;
        };
    }

    public static int getNetherWildernessPrice() {
        return switch (getCurrentDisplaySeasonNum()) {
            case 1 -> 200;
            case 2 -> 300;
            case 3 -> 400;
            default -> 0;
        };
    }

    public static ArrayList<ItemStack> getListOfRemainingItems() {
        return currentLevel.getRemainingItemsForNextLevel();
    }

    public static Level getCurrentLevel() {
        return currentLevel;
    }

    public static ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        return currentLevel.getRequiredItemsForNextLevel();
    }

    public static ItemStack getRemainingItem(Material type) {
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
        featureLevels.put("wildworld", new LvlSeasonRelation(1,4));
        featureLevels.put("capture", new LvlSeasonRelation(1,5));
        featureLevels.put("visit", new LvlSeasonRelation(1,7));

        // season 2
        featureLevels.put("stocks", new LvlSeasonRelation(2,1));
        featureLevels.put("wildnether", new LvlSeasonRelation(2,2));
        featureLevels.put("wildmining", new LvlSeasonRelation(2,3));
        featureLevels.put("day", new LvlSeasonRelation(2,4));
    }

    public static boolean featureUnlocked(String featureName) {
        // always allow while in DEBUG MODE
        if (Properties.DEBUG_MODE) {
            return true;
        }

        // make sure the featureName requires a level
        if (!featureLevels.containsKey(featureName.toLowerCase())) {
            return true;
        }

        // check season
        LvlSeasonRelation lvlSeasonRelation = featureLevels.get(featureName);
        if (lvlSeasonRelation.seasonNum < getCurrentDisplaySeasonNum()) {
            // season already completed
            return true;
        }else if (lvlSeasonRelation.seasonNum > getCurrentDisplaySeasonNum()) {
            // season not even started
            return false;
        }

        // is the level of the featureName lower or equals than the current display level num?
        return lvlSeasonRelation.levelNum <= getCurrentDisplayLevelNum();
    }
}
