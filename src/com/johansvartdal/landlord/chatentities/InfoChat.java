package com.johansvartdal.landlord.chatentities;

import org.bukkit.ChatColor;

public class InfoChat extends ChatEntity{

    @Override
    public String getDisplayName() {
        return "INFO";
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.YELLOW;
    }
}
