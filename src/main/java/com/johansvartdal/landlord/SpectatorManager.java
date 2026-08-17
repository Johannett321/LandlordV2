package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpectatorManager {

    public static void lockSpectatorsInSpectatorMode(Main plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!Main.properties.gameHasStarted()) {
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Main.playerDataManager.playerExists(player)) {
                    continue; // skip players
                }
                if (player.getGameMode() != GameMode.SPECTATOR) {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
        }, Tools.secToTicks(2),Tools.secToTicks(2));
    }
}
