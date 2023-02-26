package com.johansvartdal.landlord;

import org.bukkit.entity.Player;

public class Bank {

    public static boolean playerCanAfford(Player player, int price) {
        return Main.playerDataManager.getPlayerData(player).canAfford(price);
    }

    public static void withdrawPlayer(Player player, int amount) {
        Main.playerDataManager.getPlayerData(player).withdrawBalance(amount);
    }

    public static void depositPlayer(Player player, int amount) {
        Main.playerDataManager.getPlayerData(player).depositBalance(amount);
    }
}
