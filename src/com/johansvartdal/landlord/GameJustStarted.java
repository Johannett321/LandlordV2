package com.johansvartdal.landlord;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

public class GameJustStarted {

    private final Main plugin;
    private final World mainWorld;

    public GameJustStarted(Main plugin, World mainWorld) {
        this.plugin = plugin;
        this.mainWorld = mainWorld;
    }

    public void doStart() {
        Main.tradeCenter.build();
        Chunk centerChunk = Main.tradeCenter.getLocation().getChunk();

        OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();

        for (int i = 0; i < players.length; i ++) {
            int[] chunkPosition = getXZChunkPosition(i, centerChunk);
            Chunk playerChunk = mainWorld.getChunkAt(chunkPosition[0], chunkPosition[1]);
            ChunkBuilder.createChunk((Player) players[i], mainWorld, playerChunk);

            Location location = new Location(mainWorld, playerChunk.getX()*16+8, 256, playerChunk.getZ()*16+8);
            location = Tools.highestStandingPoint(location);
            ((Player) players[i]).teleport(location);
            Main.playerDataManager.getPlayerData((Player) players[i]).setHome(location);
        }
        // Calculate position of chunk for each player
        // Run ChunkBuilder.createChunk() to create a chunk there
    }

    private int[] getXZChunkPosition(int playerNumber, Chunk centerChunk) {
        int chunkX;
        int chunkZ;

        switch (playerNumber) {
            case 0:
                chunkX = centerChunk.getX() + 2;
                chunkZ = centerChunk.getZ();
                return new int[]{chunkX,chunkZ};
            case 1:
                chunkX = centerChunk.getX() - 2;
                chunkZ = centerChunk.getZ();
                return new int[]{chunkX,chunkZ};
            case 2:
                chunkX = centerChunk.getX();
                chunkZ = centerChunk.getZ() + 2;
                return new int[]{chunkX,chunkZ};
            case 3:
                chunkX = centerChunk.getX();
                chunkZ = centerChunk.getZ() - 2;
                return new int[]{chunkX,chunkZ};
        }
        return null;
    }
}
