package com.johansvartdal.landlord;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GameJustStarted {

    private final Main plugin;
    private final World mainWorld;

    public GameJustStarted(Main plugin, World mainWorld) {
        this.plugin = plugin;
        this.mainWorld = mainWorld;
    }

    public void doStart() {
        LevelManager.populateLevels();
        Main.tradeCenter.build();
        Chunk centerChunk = Main.tradeCenter.getLocation().getChunk();

        Player[] players = plugin.getServer().getOnlinePlayers().toArray(new Player[0]);

        for (int i = 0; i < players.length; i ++) {
            Player currentPlayer = players[i];
            int[] chunkPosition = getXZChunkPosition(i, centerChunk);
            Chunk playerChunk = mainWorld.getChunkAt(chunkPosition[0], chunkPosition[1]);
            ChunkBuilder.createChunk(players[i], mainWorld, playerChunk);

            Location location = new Location(mainWorld, playerChunk.getX()*16+8, 319, playerChunk.getZ()*16+8);
            location = Tools.middlePointBlock(location);
            location = Tools.highestStandingPoint(location);

            // teleport players home
            currentPlayer.teleport(location);
            currentPlayer.setBedSpawnLocation(location, true);
            Main.playerDataManager.getPlayerData((Player) players[i]).setCurrentHomeLocation(location);

            // show title
            currentPlayer.sendTitle(LangDict.getString("events.preparations.welcomeTitle") + ChatColor.DARK_PURPLE + "Landlord", ChatColor.RED + "V2");

            // give players ice
            ItemStack iceBlocks = new ItemStack(Material.ICE);
            iceBlocks.setAmount(2);
            Tools.givePlayerItemOrDrop(currentPlayer, iceBlocks, true);
        }
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
            case 4:
                chunkX = centerChunk.getX() + 3;
                chunkZ = centerChunk.getZ() + 3;
                return new int[]{chunkX,chunkZ};
            case 5:
                chunkX = centerChunk.getX() - 3;
                chunkZ = centerChunk.getZ() + 3;
                return new int[]{chunkX,chunkZ};
            case 6:
                chunkX = centerChunk.getX() + 3;
                chunkZ = centerChunk.getZ() - 3;
                return new int[]{chunkX,chunkZ};
            case 7:
                chunkX = centerChunk.getX() - 3;
                chunkZ = centerChunk.getZ() - 3;
                return new int[]{chunkX,chunkZ};
        }
        return null;
    }
}
