package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.lan.LanController;
import com.johansvartdal.landlord.lan.LanLightsController;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static com.johansvartdal.landlord.Tools.debugLog;

public class LevelManager {

    private static Main plugin;

    private static Level currentLevel;
    private static Level[] allLevels;
    private static final ArrayList<String> acceptedPlayers = new ArrayList<>();
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
                new S1Level1(plugin),
                new S1Level2(plugin),
                new S1Level3(plugin),
                new S1Level4(plugin),
                new S1Level5(plugin),
                new S1Level6(plugin),
                new S1Level7(plugin),
                new S1Level8(plugin),
                new S1Level9(plugin),
                new S2Level1(plugin),
                new S2Level2(plugin),
                new S2Level3(plugin),
                new S2Level4(plugin),
                new S2Level5(plugin),
                new S2Level6(plugin),
                new S2Level7(plugin),
                new S2Level8(plugin),
                new S2Level9(plugin),
                new S3Level1(plugin),
                new S3Level2(plugin),
                new S3Level3(plugin),
                new S3Level4(plugin),
                new S3Level5(plugin),
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
                Tools.givePlayerItemOrDrop(player, currentLevel.getBook().produceAndGetBook(), true);
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
        Tools.broadcastMessage(LangDict.getString("donate.townJustUpgraded") + currentLevel.getDisplayLevelNumber() + "!", ChatColor.GREEN);
        currentLevel.justUpgraded();

        // save
        save();

        // receive book
        if (currentLevel.getBook() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Tools.givePlayerItemOrDrop(player, currentLevel.getBook().produceAndGetBook(), true);
            }
        }

        // give everyone a chunk
        Main.playerDataManager.giveEveryoneChunkPoints(1);

        // update scoreboard
        Main.scoreboardHelper.warnNewLevel(getCurrentDisplaySeasonNum(), getCurrentDisplayLevelNum());

        // effects
        SpecialEffects.blastFireworks(6);
        Tools.playSoundForEveryone(Sound.UI_TOAST_CHALLENGE_COMPLETE);

        // start event if any
        LandlordEventManager.notifyLevelReached(currentLevel);

        // play lights
        LanLightsController lanLightsController = LanController.getLightsController();
        if (lanLightsController != null) {
            lanLightsController.playLevelUpEffect();
        }
    }

    public static void forceProceedToNextLevel() {
        if (!Properties.DEV_CHEAT_MODE) {
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

    public static String getRemainingItemsText() {
        StringBuilder remainingText = new StringBuilder();

        if (currentLevel == null) {
            return LangDict.getString("commandResponses.errorMessages.notAvailableRN");
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
            return LangDict.getString("generalSentenceParts.none");
        }

        return remainingText.toString();
    }

    public static String getAcceptedPlayersText() {
        return acceptedPlayers.toString();
    }

    public static void forceUpgrade(Player player) {
        if (!currentLevel.getRemainingItemsForNextLevel().isEmpty()) {
            Tools.tellPlayer(player, LangDict.getString("upgrade.cannotForceUpWithoutAllItems"), ChatColor.RED);
            return;
        }
        proceedToNextLevel();
    }

    public static void playerAcceptsUpgrade(Player playerThatAccepted) {
        UpgradeDecision upgradeDecision = getUpgradeStatus();
        switch (upgradeDecision) {
            case NOT_EVERYONE_HAS_ACCEPTED, NOT_ENOUGH_ITEMS -> informAboutAcceptanceFromPlayer(playerThatAccepted);
            case UPGRADE -> proceedToNextLevelAndInformAbout(playerThatAccepted);
            case PLAYER_IN_EVENT -> Tools.tellPlayer(new ErrorChat(), playerThatAccepted, LangDict.getString("commandResponses.errorMessages.upgradePlayerInEvent"));
            case GAME_STATE_NOT_NORMAL -> Tools.tellPlayer(new ErrorChat(), playerThatAccepted, LangDict.getString(LangDict.CMD_NOT_NOW));
        }
    }

    private static void proceedToNextLevelAndInformAbout(Player acceptingPlayer) {
        informAboutAcceptanceFromPlayer(acceptingPlayer);
        proceedToNextLevel();
    }

    private static void informAboutAcceptanceFromPlayer(Player player) {
        // tell the world that the player accepted
        God.speak(LangDict.getString("upgrade.citizens") + player.getDisplayName() + LangDict.getString("upgrade.justAcceptedUp"));
        acceptedPlayers.add(player.getDisplayName());
    }

    public static boolean playerHasAccepted(Player player) {
        if (acceptedPlayers.contains(player.getDisplayName())) {
            return true;
        }
        return false;
    }

    public static int getAmountRequiredForItem(Material type) {
        ItemStack itemStack = getRemainingItem(type);
        if (itemStack != null) {
            return itemStack.getAmount();
        }
        return 0;
    }

    public static void updateRequiredAmountForItem(Material type, int newRequired) {
        System.out.println("Step2: " + type.name());
        getCurrentLevel().updateRequiredItem(type, newRequired);
        save();
    }

    public enum UpgradeDecision {
        PLAYER_IN_EVENT,
        GAME_STATE_NOT_NORMAL,
        NOT_ENOUGH_ITEMS,
        NOT_EVERYONE_HAS_ACCEPTED,
        UPGRADE
    }

    public static UpgradeDecision getUpgradeStatus() {
        // make sure no items remain
        if (currentLevel.getRemainingItemsForNextLevel().size() > 0) {
            return UpgradeDecision.NOT_ENOUGH_ITEMS;
        }

        // make sure everyone has accepted
        if (acceptedPlayers.size() < Main.playerDataManager.getPlayerDataList().size()-1) {
            return UpgradeDecision.NOT_EVERYONE_HAS_ACCEPTED;
        }

        // make sure there is no global event
        if (!Main.properties.gameStateIsNormal()) {
            return UpgradeDecision.GAME_STATE_NOT_NORMAL;
        }

        // check if any player is in event
        if (PlayerEventManager.anyPlayersInEvent()) {
            return UpgradeDecision.PLAYER_IN_EVENT;
        }

        return UpgradeDecision.UPGRADE;
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

        for (ItemStack itemStack : currentRemaining) {
            JSONObject object = new JSONObject();
            object.put("material", itemStack.getType().toString());
            object.put("amount", itemStack.getAmount());
            if (Properties.DEBUG_LOGGING) {
                System.out.println("Adding: " + object.toJSONString());
            }
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
            debugLog("Warning! Season is 0");
            return 0;
        }
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
            case 1 -> 1990;
            case 2 -> 4990;
            case 3 -> 9990;
            default -> 0;
        };
    }

    public static int getNetherWildernessPrice() {
        return switch (getCurrentDisplaySeasonNum()) {
            case 1 -> 1490;
            case 2 -> 3490;
            case 3 -> 5990;
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
        featureLevels.put("rent_basic_tool", new LvlSeasonRelation(1,2));
        featureLevels.put("pay", new LvlSeasonRelation(1,3));
        featureLevels.put("wildworld", new LvlSeasonRelation(1,4));
        featureLevels.put("capture", new LvlSeasonRelation(1,5));
        featureLevels.put("chunkguard", new LvlSeasonRelation(1,6));
        featureLevels.put("visit", new LvlSeasonRelation(1,7));
        featureLevels.put("wildmining", new LvlSeasonRelation(1,8));
        featureLevels.put("wildnether", new LvlSeasonRelation(1,9));

        // season 2
        featureLevels.put("rent_diamond_tools", new LvlSeasonRelation(1,1));
        featureLevels.put("stocks", new LvlSeasonRelation(2,2));
        featureLevels.put("rent_turtle_shell", new LvlSeasonRelation(2,4));
        featureLevels.put("rent_elytra", new LvlSeasonRelation(2,5));
        featureLevels.put("fly", new LvlSeasonRelation(2,7));
        featureLevels.put("day", new LvlSeasonRelation(2,9));

        // season 3
    }

    public static boolean featureUnlocked(String featureName) {
        // always allow while in DEBUG MODE
        if (Properties.DEV_CHEAT_MODE) {
            return true;
        }

        // make sure the featureName requires a level
        if (!featureLevels.containsKey(featureName.toLowerCase())) {
            return true;
        }

        // check season
        LvlSeasonRelation lvlSeasonRelation = featureLevels.get(featureName.toLowerCase());
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
