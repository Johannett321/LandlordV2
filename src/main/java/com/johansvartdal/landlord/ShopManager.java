package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class ShopManager {

    private static Location shopLocationBase = new Location(Bukkit.getWorld("world"), -30000, 100, -30000);

    public static boolean hasShop(Player player) {
        return Main.playerDataManager.getPlayerData(player).getShopLocation() != null;
    }

    public static void createShop(Player player) {
        Chunk shopChunk = getRandomShopChunk();

        // claim the chunk
        ChunkBuilder.createChunk(player, shopChunk);

        // update cabin location
        Location shopChunkSpawnLocation = new Location(shopChunk.getWorld(), shopChunk.getX()*16 + 8, 0, shopChunk.getZ()*16 + 8);
        shopChunkSpawnLocation = Tools.highestStandingPoint(shopChunkSpawnLocation);
        Main.playerDataManager.getPlayerData(player).setShopLocation(shopChunkSpawnLocation);
    }

    private static Chunk getRandomShopChunk() {
        Location location = new Location(
                shopLocationBase.getWorld(),
                shopLocationBase.getX(),
                shopLocationBase.getY(),
                shopLocationBase.getZ()
        );

        Random random = new Random();
        location.setX(location.getX()-random.nextInt(5000));
        location.setZ(location.getZ()-random.nextInt(5000));

        Chunk requestedChunk = location.getChunk();
        Chunk neighbourChunk1 = Bukkit.getWorld("world").getChunkAt(requestedChunk.getX()+1, requestedChunk.getZ());
        Chunk neighbourChunk2 = Bukkit.getWorld("world").getChunkAt(requestedChunk.getX()-1, requestedChunk.getZ());
        Chunk neighbourChunk3 = Bukkit.getWorld("world").getChunkAt(requestedChunk.getX(), requestedChunk.getZ()+1);
        Chunk neighbourChunk4 = Bukkit.getWorld("world").getChunkAt(requestedChunk.getX(), requestedChunk.getZ()-1);

        // if someone already owns the chunk, find a new one
        if (
                ChunkBuilder.someoneOwnsChunk(requestedChunk) ||
                        ChunkBuilder.someoneOwnsChunk(neighbourChunk1) ||
                        ChunkBuilder.someoneOwnsChunk(neighbourChunk2) ||
                        ChunkBuilder.someoneOwnsChunk(neighbourChunk3) ||
                        ChunkBuilder.someoneOwnsChunk(neighbourChunk4)
        ) {
            return getRandomShopChunk();
        }

        return requestedChunk;
    }
}
