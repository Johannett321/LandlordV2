package com.johansvartdal.landlord.chatentities;

import com.johansvartdal.landlord.LangDict;
import org.bukkit.ChatColor;

public class WarningChat extends ChatEntity{

    @Override
    public String getDisplayName() {
        return LangDict.getString("generalSentenceParts.warningChat");
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GOLD;
    }

    @Override
    public ChatColor getMessageColor() {
        return ChatColor.YELLOW;
    }
}
