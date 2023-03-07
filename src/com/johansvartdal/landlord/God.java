package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class God {

    public static void speak(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GREEN + LangDict.getString("god") + ChatColor.WHITE + " " + message);
        }
    }
}