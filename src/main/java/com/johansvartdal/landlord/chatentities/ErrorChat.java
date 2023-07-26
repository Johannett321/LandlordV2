package com.johansvartdal.landlord.chatentities;

import org.bukkit.ChatColor;

public class ErrorChat extends ChatEntity{

    @Override
    public String getDisplayName() {
        return "ERROR";
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.RED;
    }

    @Override
    public ChatColor getMessageColor() {
        return ChatColor.RED;
    }
}
