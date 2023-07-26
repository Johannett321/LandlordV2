package com.johansvartdal.landlord.chatentities;

import org.bukkit.ChatColor;

public class RouletteChat extends ChatEntity{

    @Override
    public String getDisplayName() {
        return "ROULETTE";
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public ChatColor getMessageColor() {
        return ChatColor.DARK_PURPLE;
    }
}
