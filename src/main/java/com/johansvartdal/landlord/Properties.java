package com.johansvartdal.landlord;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.io.Serializable;

public class Properties implements Serializable {

    public enum GameState {
        NOT_STARTED,
        EVENT_RUNNING,
        PREPARATIONS,
        NORMAL
    }

    private GameState currentGameState = GameState.NOT_STARTED;
    private long gameInitiallyStartedAt = 0;

    @Getter
    @Setter
    private double chunkDiscountPercentPoint = 0;

    public static final boolean DEV_CHEAT_MODE = false;
    public static final boolean DEV_UNLOCK_ALL = false;
    public static final boolean DEBUG_LOGGING = false;

    public Properties() {
        if (!Tools.fileExists("Properties.json")) {
            save();
            return;
        }
        load();
    }

    public void setGameState(GameState gameState) {
        currentGameState = gameState;
        save();
    }

    public GameState getGameState() {
        return currentGameState;
    }

    public boolean gameStateIsNormal() {
        return getGameState() == GameState.NORMAL;
    }

    public boolean gameHasStarted() {
        return getGameState() != GameState.NOT_STARTED;
    }

    /**
     * Number of players registered in the game, never less than 1.
     * <p>
     * This value is used as a multiplier for item quantities throughout the levels and
     * mystery chests. On a server where no game has been started yet there are no player
     * files, so the raw count is 0 — which produced zero-sized ItemStacks and made the
     * plugin fail to enable, because Bukkit rejects an ItemStack with amount 0.
     * Clamping to 1 keeps those quantities valid before the first player joins.
     */
    public int getNumberOfPlayers() {
        return Math.max(1, Tools.getNumberOfFilesInDirectory("players"));
    }

    /**
     * Returns the number of milliseconds since the game initially started. That means when the user wrote /landlord start
     * @return
     */
    public long getMillisSinceGameInitiallyStarted() {
        return System.currentTimeMillis() - gameInitiallyStartedAt;
    }

    /**
     * Returns the millisecond of when the /landlord start command when executed.
     * @return
     */
    public long getGameInitiallyStartedMillis() {
        return gameInitiallyStartedAt;
    }

    public void notifyGameStarted() {
        if (gameInitiallyStartedAt != 0) {
            return;
        }
        gameInitiallyStartedAt = System.currentTimeMillis();
        save();
    }

    public void save() {
        JSONObject properties = new JSONObject();
        properties.put("currentGameState", currentGameState.toString());
        properties.put("gameInitiallyStartedAt", gameInitiallyStartedAt);
        properties.put("chunkDiscountPercentPoint", chunkDiscountPercentPoint);
        Tools.saveJsonToFile("Properties.json", properties);
    }

    public void load() {
        JSONObject properties = Tools.loadJson("Properties.json");
        if (properties != null) {
            currentGameState = GameState.valueOf((String) properties.get("currentGameState"));

            // set "game initially started at" time.
            if (properties.containsKey("gameInitiallyStartedAt")) {
                gameInitiallyStartedAt = (long) properties.get("gameInitiallyStartedAt");
            }else if (currentGameState != GameState.NOT_STARTED) {
                gameInitiallyStartedAt = System.currentTimeMillis();
            }

            if (properties.containsKey("chunkDiscountPercentPoint")) {
                chunkDiscountPercentPoint = (double) properties.get("chunkDiscountPercentPoint");
            }
        }
    }
}