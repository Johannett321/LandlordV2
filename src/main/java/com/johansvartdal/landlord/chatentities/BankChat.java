package com.johansvartdal.landlord.chatentities;

import org.bukkit.ChatColor;

public class BankChat extends ChatEntity{
    @Override
    public String getDisplayName() {
        return "BANK";
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GREEN;
    }
}
