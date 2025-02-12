package com.johansvartdal.landlord.business;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.chatentities.BusinessChat;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

@Getter
@Setter
public abstract class Business {

    private Main plugin;
    private BusinessChat businessChatEntity;

    private String name;
    private int bankAccount = 0;
    private Double revenue = 0.0;
    private String ownerUUID;
    private BusinessType type;

    protected abstract int getRegistrationFee();
    protected abstract BusinessType getType();
    protected abstract void onBusinessRegistered();
    protected abstract void onBusinessUnregistered();
    protected abstract boolean handleCommand(Player player, String[] args);

    public Business(Main plugin, Player player, String name) {
        this.plugin = plugin;
        this.ownerUUID = player.getUniqueId().toString();
        this.name = name;
        this.businessChatEntity = new BusinessChat(this.name);
    }

    public Business(Main plugin, JSONObject businessJson) {
        this.plugin = plugin;
        this.name = businessJson.get("name").toString();
        this.type = stringToType(businessJson.get("type").toString());
        this.revenue = (Double) businessJson.get("revenue");
        this.ownerUUID = (String) businessJson.get("ownerUUID");
        this.businessChatEntity = new BusinessChat(this.name);
    }

    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("type", type.toString());
        jsonObject.put("revenue", revenue);
        jsonObject.put("ownerUUID", ownerUUID);
        return jsonObject;
    }

    public BusinessType stringToType(String type) {
        switch (type) {
            case "EXPORT": return BusinessType.EXPORT;
        }
        return null;
    }

    public void withdrawBank(int amount) {
        bankAccount -= amount;
    }

    public void depositBank(int amount) {
        revenue += amount;
        bankAccount += amount;
    }
}
