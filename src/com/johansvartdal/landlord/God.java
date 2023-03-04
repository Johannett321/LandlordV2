package com.johansvartdal.landlord;

import org.bukkit.ChatColor;

public class God {

    public static void speak(String message) {
        Tools.broadcastMessage(ChatColor.GREEN + LangDict.getString("god") + ChatColor.WHITE + " " + message);
    }
}