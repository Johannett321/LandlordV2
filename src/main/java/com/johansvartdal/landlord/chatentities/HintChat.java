package com.johansvartdal.landlord.chatentities;

import org.bukkit.ChatColor;

public class HintChat extends ChatEntity{
    @Override
    public String getDisplayName() {
        return "TIP";
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GRAY;
    }

    @Override
    public ChatColor getMessageColor() {
        return ChatColor.GRAY;
    }
}
