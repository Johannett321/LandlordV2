package com.johansvartdal.landlord;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ChunkBuilder {

    private static JSONObject replacedBlocks = new JSONObject();

    public static void load() {
        JSONObject loadedReplacedBlocks = Tools.loadJson("ReplacedBlocks.json");
        if (loadedReplacedBlocks != null) {
            replacedBlocks = loadedReplacedBlocks;
        }
    }


    public static void createChunk(Player player, Chunk chunk) {
        createChunk(player, player.getWorld(), chunk);
    }

    public static void createChunk(World world, Chunk chunk) {
        createChunk(null, world, chunk);
    }

    public static void createChunk(Player player, World world, Chunk chunk) {
        int centerX = chunk.getX()*16+8;
        int centerZ = chunk.getZ()*16+8;

        buildEastWall(world, centerX-9, centerZ-9, 18, Material.BARRIER);
        buildEastWall(world, centerX-9, centerZ+8, 18, Material.BARRIER);

        buildSouthWall(world, centerX-9, centerZ-9, 18, Material.BARRIER);
        buildSouthWall(world, centerX+8, centerZ-9, 18, Material.BARRIER);

        Tools.saveJsonToFile("ReplacedBlocks.json", replacedBlocks);

        if (player != null) {
            Main.playerDataManager.getPlayerData(player).addOwnedChunk(chunk.getX(), chunk.getZ());
        }
    }


    public static void unlockEast(Player player) {
        int workingChunkX = player.getLocation().getChunk().getX()+1;
        int workingChunkZ = player.getLocation().getChunk().getZ();

        int centerX = workingChunkX*16+8;
        int centerZ = workingChunkZ*16+8;

        buildEastWall(player.getWorld(), centerX-9, centerZ-9, 18, Material.BARRIER);
        buildEastWall(player.getWorld(), centerX-9, centerZ+8, 18, Material.BARRIER);
        buildSouthWall(player.getWorld(), centerX+8, centerZ-9, 18, Material.BARRIER);

        Main.playerDataManager.addChunkToPlayer(player, workingChunkX, workingChunkZ);
        clearBarriersWithinOwnedChunks(player.getWorld(), Main.playerDataManager.getPlayerData(player));

        Tools.saveJsonToFile("ReplacedBlocks.json", replacedBlocks);
    }

    public static void unlockWest(Player player) {
        int workingChunkX = player.getLocation().getChunk().getX()-1;
        int workingChunkZ = player.getLocation().getChunk().getZ();

        int centerX = workingChunkX*16+8;
        int centerZ = workingChunkZ*16+8;

        buildEastWall(player.getWorld(), centerX-9, centerZ-9, 18, Material.BARRIER);
        buildEastWall(player.getWorld(), centerX-9, centerZ+8, 18, Material.BARRIER);
        buildSouthWall(player.getWorld(), centerX-9, centerZ-9, 18, Material.BARRIER);

        Main.playerDataManager.addChunkToPlayer(player, workingChunkX, workingChunkZ);
        clearBarriersWithinOwnedChunks(player.getWorld(), Main.playerDataManager.getPlayerData(player));

        Tools.saveJsonToFile("ReplacedBlocks.json", replacedBlocks);
    }

    public static void unlockNorth(Player player) {
        int workingChunkX = player.getLocation().getChunk().getX();
        int workingChunkZ = player.getLocation().getChunk().getZ()-1;

        int centerX = workingChunkX*16+8;
        int centerZ = workingChunkZ*16+8;

        buildEastWall(player.getWorld(), centerX-9, centerZ-9, 18, Material.BARRIER);
        buildSouthWall(player.getWorld(), centerX-9, centerZ-10, 18, Material.BARRIER);
        buildSouthWall(player.getWorld(), centerX+8, centerZ-10, 18, Material.BARRIER);

        Main.playerDataManager.addChunkToPlayer(player, workingChunkX, workingChunkZ);
        clearBarriersWithinOwnedChunks(player.getWorld(), Main.playerDataManager.getPlayerData(player));

        Tools.saveJsonToFile("ReplacedBlocks.json", replacedBlocks);
    }

    public static void unlockSouth(Player player) {
        int workingChunkX = player.getLocation().getChunk().getX();
        int workingChunkZ = player.getLocation().getChunk().getZ()+1;

        int centerX = workingChunkX*16+8;
        int centerZ = workingChunkZ*16+8;

        buildEastWall(player.getWorld(), centerX-9, centerZ+8, 18, Material.BARRIER);
        buildSouthWall(player.getWorld(), centerX-9, centerZ-9, 18, Material.BARRIER);
        buildSouthWall(player.getWorld(), centerX+8, centerZ-9, 18, Material.BARRIER);

        Main.playerDataManager.addChunkToPlayer(player, workingChunkX, workingChunkZ);
        clearBarriersWithinOwnedChunks(player.getWorld(), Main.playerDataManager.getPlayerData(player));

        Tools.saveJsonToFile("ReplacedBlocks.json", replacedBlocks);
    }

    private static void clearBarriersWithinOwnedChunks(World world, PlayerData playerData) {
        for (int[] owned : playerData.getOwnedChunks()) {
            clearChunkOfBarriers(world, owned[0], owned[1]);
        }
    }

    public static void buildEastWall (World world, int currentBuildX, int currentBuildZ, int length, Material material) {
        for (int x = currentBuildX + 1; x < currentBuildX + length; x++) {
            for (int y = 1; y < 256; y++) {
                Location location = new Location(world, x, y, currentBuildZ);

                if (location.getBlock().getType() != Material.AIR &&
                        location.getBlock().getType() != Material.BARRIER &&
                        material != null) {

                    JSONObject block = new JSONObject();
                    block.put("material", location.getBlock().getType().toString());
                    String blockName = x + "," + y + "," + currentBuildZ;
                    replacedBlocks.put(blockName, block);
                }

                location.getBlock().setType(material);
            }
        }
    }

    public static void buildSouthWall (World world, int currentBuildX, int currentBuildZ, int length, Material material) {
        for (int z = currentBuildZ + 1; z < currentBuildZ + length; z++) {
            for (int y = 1; y < 256; y++) {
                Location location = new Location(world, currentBuildX, y, z);

                if (location.getBlock().getType() != Material.AIR &&
                        location.getBlock().getType() != Material.BARRIER &&
                        material != null)
                {
                    JSONObject block = new JSONObject();
                    block.put("material", location.getBlock().getType().toString());
                    String blockName = currentBuildX + "," + y + "," + z;
                    replacedBlocks.put(blockName, block);
                }

                location.getBlock().setType(material);
            }
        }
    }

    public static void clearChunkOfBarriers (World world, int chunkX, int chunkZ) {
        int xStart = chunkX*16;
        int yStart = 1;
        int zStart = chunkZ*16;

        for (int x = xStart; x < xStart+16; x++) {
            for (int y = yStart; y < yStart+256; y++) {
                for (int z = zStart; z < zStart+16; z++) {
                    Location location = new Location(world, x, y, z);
                    if (location.getBlock().getType() == Material.BARRIER) {
                        JSONObject replacedBlock = (JSONObject) replacedBlocks.get(x + "," + y + "," + z);

                        if (replacedBlock != null) {
                            String replacedMatString = (String) replacedBlock.get("material");
                            Material replacedMat = Material.getMaterial(replacedMatString);
                            location.getBlock().setType(replacedMat);
                            replacedBlocks.remove(x + "," + y + "," + z);
                            continue;
                        }
                        location.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    public static void unlockDirection(Player player, String direction) {
        switch (direction) {
            case "north":
                unlockNorth(player);
                break;
            case "south":
                unlockSouth(player);
                break;
            case "west":
                unlockWest(player);
                break;
            case "east":
                unlockEast(player);
                break;
        }
    }

    public static boolean chunkIsAvailableForPurchaseBy(Player player, Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        System.out.println("Checking if someone own chunks at: " + chunkX + " : " + chunkZ);

        for (PlayerData playerData : Main.playerDataManager.getPlayerDataList()) {
            // Make sure player cannot buy already owned chunks by himself
            if (playerData.getUsername().equals(player.getName())) {
                if (playerData.ownsChunkAtLocation(chunkX, chunkZ)) {
                    return false;
                }
                continue;
            }

            // Make sure player cannot buy chunks owned by someone else or close to someone else
            if (playerData.ownsChunkAtLocation(chunkX, chunkZ) ||
                    playerData.ownsChunkAtLocation(chunkX+1, chunkZ) ||
                    playerData.ownsChunkAtLocation(chunkX, chunkZ+1) ||
                    playerData.ownsChunkAtLocation(chunkX, chunkZ-1) ||
                    playerData.ownsChunkAtLocation(chunkX-1, chunkZ)) {
                return false;
            }
        }

        // Make sure player cannot buy chunk next to trade center
        Chunk tradeCenterChunk = Main.tradeCenter.getLocation().getChunk();
        int tradeCenterChunkX =  tradeCenterChunk.getX();
        int tradeCenterChunkZ =  tradeCenterChunk.getZ();
        if (chunkX == tradeCenterChunkX && chunkZ == tradeCenterChunkZ) {
            return false;
        }else if (chunkX == tradeCenterChunkX+1 && chunkZ == tradeCenterChunkZ) {
            return false;
        }else if (chunkX == tradeCenterChunkX-1 && chunkZ == tradeCenterChunkZ) {
            return false;
        }else if (chunkX == tradeCenterChunkX && chunkZ == tradeCenterChunkZ+1) {
            return false;
        }else if (chunkX == tradeCenterChunkX && chunkZ == tradeCenterChunkZ-1) {
            return false;
        }

        return true;
    }
}
