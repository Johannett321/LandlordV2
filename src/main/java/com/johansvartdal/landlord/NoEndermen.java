package com.johansvartdal.landlord;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class NoEndermen {

    private final Plugin plugin;

    public void startScheduler() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (Properties.DEBUG_LOGGING) {
                System.out.println("Killing endermen in chunks");
            }

            // loop over playerData, for each: loop over their chunks
            Main.playerDataManager.getPlayerDataList().forEach(playerData -> playerData.getOwnedChunks().forEach(chunkCords -> {
                Chunk chunk = Bukkit.getWorlds().get(0).getChunkAt(chunkCords[0], chunkCords[1]);
                killEndermenInChunk(chunk);
            }));
        }, Tools.secToTicks(20), Tools.secToTicks(20));
    }

    public void killEndermenInChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Enderman) {
                entity.remove();
            }
        }
    }
}
