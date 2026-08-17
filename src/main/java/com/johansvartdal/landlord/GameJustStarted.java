package com.johansvartdal.landlord;

import com.johansvartdal.landlord.levels.LevelManager;
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

        Player[] players = plugin.getServer().getOnlinePlayers().toArray(new Player[0]);

        for (int i = 0; i < players.length; i ++) {
            Player currentPlayer = players[i];
            setupForPlayer(currentPlayer, i, players.length);
        }
    }

    public void createCirclesAroundPlayer(Player player) {
        ChunkBuilder.createChunk(player.getWorld(), player.getLocation().getChunk());
        int numberOfChunks = 5;
        for (int i = 0; i < numberOfChunks; i++) {
            double[] chunkCord = getChunkCord(i, player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ(), numberOfChunks);
            Chunk chunk = player.getWorld().getChunkAt((int) chunkCord[0], (int) chunkCord[1]);
            System.out.println("Building chunk " + chunk.getX()*16 + ", " + chunk.getZ()*16);
            ChunkBuilder.createChunk(player.getWorld(), chunk);
            player.sendMessage("Built chunk " + i);
        }
    }

    public double[] getChunkCord(int playerNumber, double centerX, double centerZ, int totalChunks) {
        return getRadialChunkPosition(centerX, centerZ, 4, totalChunks)[playerNumber];
    }

    public double[][] getRadialChunkPosition(double centerX, double centerZ, double radius, int totalSquares) {
        double[][] positions = new double[totalSquares][2];

        for (int i = 0; i < totalSquares; i++) {
            double angle = 2 * Math.PI * i / totalSquares; // Evenly spaced angles
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);
            positions[i] = new double[]{x, z};
        }

        return positions;
    }

    public void setupForPlayer(Player player, int chunkNumber, int totalChunks) {
        Chunk centerChunk = Main.tradeCenter.getLocation().getChunk();

        int[] chunkPosition;
        if (totalChunks <= 4) {
            chunkPosition = getXZChunkPosition(chunkNumber, centerChunk);
        }else {
            double[] radialChunkPosition = getRadialChunkPosition(centerChunk.getX(), centerChunk.getZ(), 4, totalChunks)[chunkNumber];
            chunkPosition = new int[]{(int) radialChunkPosition[0], (int) radialChunkPosition[1]};
        }
        Chunk playerChunk = mainWorld.getChunkAt(chunkPosition[0], chunkPosition[1]);
        ChunkBuilder.createChunk(player, mainWorld, playerChunk);

        Location location = new Location(mainWorld, playerChunk.getX()*16+8, 319, playerChunk.getZ()*16+8);
        location = Tools.middlePointBlock(location);
        location = Tools.highestStandingPoint(location);

        // teleport players home
        player.teleport(location);
        player.setRespawnLocation(location, true);
        Main.playerDataManager.getPlayerData((Player) player).setHomeLocation(location);

        // show title
        player.sendTitle(LangDict.getString("events.preparations.welcomeTitle") + ChatColor.DARK_PURPLE + "Landlord", ChatColor.RED + "V2");

        // give players ice
        ItemStack iceBlocks = new ItemStack(Material.ICE);
        iceBlocks.setAmount(2);
        Tools.givePlayerItemOrDrop(player, iceBlocks, true);
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
