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

    public int getNumberOfPlayers() {
        return Tools.getNumberOfFilesInDirectory("players");
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