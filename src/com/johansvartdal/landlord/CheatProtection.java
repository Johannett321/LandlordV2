package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CheatProtection {

    private final Main plugin;

    public CheatProtection(Main plugin) {
        this.plugin = plugin;

        // no nether portals
        plugin.getServer().getPluginManager().registerEvents(new NoNetherPortal(plugin), plugin);

        // no staying outside chunk
        scheduleCheckPlayerLocations();
    }

    public void scheduleCheckPlayerLocations() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            checkPlayerLocations();
            scheduleCheckPlayerLocations();
        }, Tools.secToTicks(5));
    }

    public void checkPlayerLocations() {
        if (Properties.DEBUG_MODE) {
            return;
        }

        // don't run if game state is not normal
        if (!Main.properties.gameStateIsNormal()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            // don't run if player is in trade center
            if (Main.tradeCenter.getLocation().getChunk().equals(player.getLocation().getChunk())) {
                continue;
            }

            // don't run if player is in playerEvent
            if (PlayerEventManager.playerIsInEvent(player)) {
                continue;
            }

            // allow visiting
            if (ChunkBuilder.someoneOwnsChunk(player.getLocation().getChunk())) {
                continue;
            }

            // make sure player owns the chunk
            if (!Main.playerDataManager.getPlayerData(player).ownsChunk(player.getLocation().getChunk())) {
                Location homeLoc = Main.playerDataManager.getPlayerData(player).getHomeLocation();
                Tools.tellPlayer(player, LangDict.getString("cheatProtectionOutsideChunk"), ChatColor.YELLOW);
                player.teleport(homeLoc);
            }
        }
    }
}
