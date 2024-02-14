package com.johansvartdal.landlord.chatentities;

import org.bukkit.ChatColor;

public class ChunkGuardChat extends ChatEntity{
    @Override
    public String getDisplayName() {
        return "CHUNKGUARD";
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.DARK_GREEN;
    }
}
