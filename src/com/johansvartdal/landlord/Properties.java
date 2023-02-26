package com.johansvartdal.landlord;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.Serializable;

public class Properties implements Serializable {

    public enum GameState {
        NOT_STARTED,
        PREPARATIONS,
        NORMAL
    }

    private GameState currentGameState = GameState.NOT_STARTED;
    public static final boolean DEBUG_MODE = true;

    public Properties() {
        if (!Tools.fileExists("Properties.json")) {
            save();
            return;
        }
        JSONObject loadedProps = Tools.loadJson("Properties.json");
        if (loadedProps == null) {
            System.out.println("ERROR! PROPERTIES JSON OBJECT IS NULL EVEN THOUGH THE JSON FILE EXISTS!");
            return;
        }

        System.out.println("Loading properties");
        currentGameState = GameState.valueOf((String) loadedProps.get("currentGameState"));
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

    public void save() {
        JSONObject properties = new JSONObject();
        properties.put("currentGameState", currentGameState.toString());
        Tools.saveJsonToFile("Properties.json", properties);
    }

    public void load() {
        JSONObject properties = Tools.loadJson("Properties.json");
        if (properties != null) {
            currentGameState = GameState.valueOf((String) properties.get("currentGameState"));
        }
    }
}
