package com.johansvartdal.landlord.webserver;

import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.PlayerData;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.levels.Level;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class HomeDataSupplier extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // collect data
        int numberOfPlayers = Bukkit.getOnlinePlayers().size();

        // start creating response
        resp.setContentType("application/json");
        JSONObject jsonObject = new JSONObject();

        // supply data
        jsonObject.put("numPlayers", numberOfPlayers);
        jsonObject.put("season", LevelManager.getCurrentDisplaySeasonNum());
        jsonObject.put("level", LevelManager.getCurrentDisplayLevelNum());

        jsonObject.put("remainingItems", getListOfRequiredItems());
        jsonObject.put("playerStatuses", getPlayerStatuses());
        jsonObject.put("playerBanks", getPlayerBanks());

        // remainingItems, playerStatuses, playerBanks (key, value)

        // send data
        resp.getWriter().print(jsonObject.toJSONString());
        resp.getWriter().flush();
    }

    private JSONArray getListOfRequiredItems() {
        // collect data
        Level currentLevel = LevelManager.getCurrentLevel();
        ArrayList<ItemStack> remainingItems = currentLevel.getRemainingItemsForNextLevel();

        // create the list
        JSONArray array = new JSONArray();
        for (ItemStack itemStack : remainingItems) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", itemStack.getType().name().toLowerCase());
            jsonObject.put("value", itemStack.getAmount());
            array.add(jsonObject);
        }
        return array;
    }

    private JSONArray getPlayerStatuses() {
        // Get list of playerdata
        ArrayList<PlayerData> listOfPlayerData = Main.playerDataManager.getPlayerDataList();

        // Create the list
        JSONArray array = new JSONArray();
        for (PlayerData playerData: listOfPlayerData) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", playerData.getUsername());
            jsonObject.put("value", playerData.getStatus());
            array.add(jsonObject);
        }
        return array;
    }

    private JSONArray getPlayerBanks() {
        // Get list of playerdata
        ArrayList<PlayerData> listOfPlayerData = Main.playerDataManager.getPlayerDataList();

        // Create the list
        JSONArray array = new JSONArray();
        for (PlayerData playerData: listOfPlayerData) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", playerData.getUsername());
            jsonObject.put("value", Tools.formatCurrency(playerData.getBalance()));
            array.add(jsonObject);
        }
        return array;
    }
}
