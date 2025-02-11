package com.johansvartdal.landlord.chatentities;

import org.bukkit.ChatColor;

public class BusinessChat extends ChatEntity{

    private String businessName;

    public BusinessChat(String businessName) {
        this.businessName = businessName;
    }

    @Override
    public String getDisplayName() {
        return businessName;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.AQUA;
    }
}
