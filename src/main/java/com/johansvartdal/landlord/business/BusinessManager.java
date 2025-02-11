package com.johansvartdal.landlord.business;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.InfoChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusinessManager {

    private Main plugin;
    ArrayList<Business> businesses = new ArrayList<>();

    public BusinessManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerBusiness(Player player, Business business) {
        // make sure player can afford and withdraw
        if (!Bank.playerCanAfford(player, business.getRegistrationFee())) {
            Bank.tellPlayerTheyNeed(player, business.getRegistrationFee(), LangDict.getString("business.toStartABusiness"));
            return;
        }
        Bank.withdrawPlayer(LangDict.getString("business.startingABusiness"), player, business.getRegistrationFee());

        // add and save business
        businesses.add(business);
        saveBusinesses();

        // broadcast business registered
        Tools.broadcastMessage(new InfoChat(), player.getDisplayName() + LangDict.getString("business.justFoundedANewBusiness") + business.getName());
        player.sendTitle(ChatColor.GOLD + business.getName(), ChatColor.RED + LangDict.getString("business.ceo") + player.getDisplayName());

        // Notify business registered
        business.onBusinessRegistered();
    }

    public Business getPlayerBusiness(Player player) {
        return businesses.stream()
                .filter(business -> business.getOwnerUUID().equals(player.getUniqueId().toString()))
                .findFirst()
                .orElse(null);
    }

    public void saveBusinesses() {
        List<JSONObject> businessesJsonObjects = businesses.stream().map(Business::getJson).toList();

        JSONArray businessesJsonArray = new JSONArray();
        businessesJsonArray.addAll(businessesJsonObjects);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("businesses", businessesJsonArray);

        Tools.saveJsonToFile("businesses.json", jsonObject);
    }

    public void loadBusinesses() {
        businesses = new ArrayList<>();

        JSONObject businessesJsonObject = Tools.loadJson("businesses.json");
        JSONArray businessesJsonArray = (JSONArray) businessesJsonObject.get("businesses");

        businessesJsonArray.forEach(business -> {
            JSONObject businessJsonObject = (JSONObject) business;
            businesses.add(new ExportBusiness(plugin, businessJsonObject)); //TODO Fix
        });
    }
}
