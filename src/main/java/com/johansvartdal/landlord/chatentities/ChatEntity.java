package com.johansvartdal.landlord.chatentities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@Getter
public abstract class ChatEntity {
    public abstract String getDisplayName();
    public abstract ChatColor getChatColor();
    public ChatColor getMessageColor() {
        return ChatColor.WHITE;
    }
}
