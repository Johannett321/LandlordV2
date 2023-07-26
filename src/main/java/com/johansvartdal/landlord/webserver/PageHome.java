package com.johansvartdal.landlord.webserver;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.commands.Upgrade;
import com.johansvartdal.landlord.levels.Level;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;

public class PageHome extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        if (!Main.properties.gameHasStarted()) {
            resp.getWriter().println(WebServerManager.getSiteHeader() + "<h1>" + LangDict.getString("webserver.preparationsPageNotAvailable") + "</h1>" + WebServerManager.getSiteEnding());
            return;
        }

        resp.getWriter().println(Tools.readInternal("/web/home.html"));
    }

    /**
     * Get a list of players, their money and if they are online
     * @return A StringBuilder with a HTML element.
     */
    private StringBuilder getListOfPlayers() {
        // Get list of playerdata
        StringBuilder listOfPlayers = new StringBuilder();
        ArrayList<PlayerData> listOfPlayerData = Main.playerDataManager.getPlayerDataList();

        // Create the list
        for (PlayerData playerData: listOfPlayerData) {
            String playerOnlineInfo = "";
            Player player = Bukkit.getPlayer(playerData.getUsername());

            // Check if player is online
            if (player != null && player.isOnline()) {
                playerOnlineInfo = "online-player";
            }

            // Add the player to the return element
            listOfPlayers.append("<div class='row'><div class='col-md-4 " + playerOnlineInfo + "'>" + playerData.getUsername() + "</div><div class='col-md-4'>" + playerData.getStatus() + "</div><div class='col-md-4'>" + playerData.getBalance() + "</div></div>");
        }

        return listOfPlayers;
    }

    private StringBuilder getListOfRequiredItems() {
        Level currentLevel = LevelManager.getCurrentLevel();

        StringBuilder formatted = new StringBuilder();
        ArrayList<ItemStack> remainingItems = currentLevel.getRemainingItemsForNextLevel();
        for (ItemStack itemStack : remainingItems) {
            formatted.append("<div class='row'><div class='col-md-6'>" + itemStack.getType().name().toLowerCase() + "</div>" + "<div class='col-md-6'>" + itemStack.getAmount() + "</div></div>");
        }

        return formatted;
    }
}
