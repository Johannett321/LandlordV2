package com.johansvartdal.landlord.business;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import com.johansvartdal.landlord.chatentities.BusinessChat;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.UUID;

@Getter
@Setter
public abstract class Business {

    private Main plugin;
    private BusinessChat businessChatEntity;

    private String name;
    private int bankAccount = 0;
    private Double revenue = 0.0;
    private String ownerUUID;


    protected abstract int getRegistrationFee();
    protected abstract BusinessType getType();
    protected abstract void onBusinessRegistered();
    protected abstract void onBusinessUnregistered();
    protected abstract boolean handleCommand(Player player, String[] args);
    protected abstract BusinessType getBusinessType();

    public Business(Main plugin, Player player, String name) {
        this.plugin = plugin;
        this.ownerUUID = player.getUniqueId().toString();
        this.name = name;
        this.businessChatEntity = new BusinessChat(this.name);
    }

    public Business(Main plugin, JSONObject businessJson) {
        this.plugin = plugin;
        this.name = businessJson.get("name").toString();
        this.revenue = (Double) businessJson.get("revenue");
        this.bankAccount = ((Long) businessJson.get("bankAccount")).intValue();
        this.ownerUUID = (String) businessJson.get("ownerUUID");
        this.businessChatEntity = new BusinessChat(this.name);
    }

    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("type", getBusinessType().toString());
        jsonObject.put("revenue", revenue);
        jsonObject.put("bankAccount", bankAccount);
        jsonObject.put("ownerUUID", ownerUUID);
        return jsonObject;
    }

    public void withdrawBank(int amount) {
        bankAccount -= amount;
    }

    public void depositBank(int amount) {
        revenue += amount;
        bankAccount += amount;
    }

    public boolean canAfford(int amount) {
        return bankAccount >= amount;
    }

    public void tellCannotAfford(String what, int amount) {
        Player player = Bukkit.getPlayer(ownerUUID);
        if (player != null) {
            Tools.tellPlayer(getBusinessChatEntity(), player, LangDict.getString("business.cannotAfford") + what + LangDict.getString("sellItem.for") + Tools.formatCurrency(amount), ChatColor.RED);
        }
    }
}
