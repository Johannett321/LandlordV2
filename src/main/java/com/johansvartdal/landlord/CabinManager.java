package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;

public class CabinManager {

    private static Location cabinLocationsBase = new Location(Bukkit.getWorld("world"), -30000, 100, -30000);

    public static boolean hasCabin(Player player) {
        return Main.playerDataManager.getPlayerData(player).getCabinLocation() != null;
    }

    public static void createCabin(Player player) {
        Chunk cabinChunk = getRandomCabinChunk();

        // claim the chunk
        ChunkBuilder.createChunk(player, cabinChunk);

        // update cabin location
        Location cabinSpawnLocation = new Location(cabinChunk.getWorld(), cabinChunk.getX()*16+8, 0, cabinChunk.getZ()*16+8);
        cabinSpawnLocation = Tools.highestStandingPoint(cabinSpawnLocation);
        Main.playerDataManager.getPlayerData(player).setCabinLocation(cabinSpawnLocation);
    }

    private static Chunk getRandomCabinChunk() {
        Location location = new Location(
                cabinLocationsBase.getWorld(),
                cabinLocationsBase.getX(),
                cabinLocationsBase.getY(),
                cabinLocationsBase.getZ()
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
            return getRandomCabinChunk();
        }

        return requestedChunk;
    }
}
