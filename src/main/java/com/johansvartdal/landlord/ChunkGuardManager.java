package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class ChunkGuardManager {

    private static ArrayList<Chunk> guardedChunks = new ArrayList<>();

    private Main plugin;

    public ChunkGuardManager(Main plugin) {
        this.plugin = plugin;
        doLoop();
    }

    private void doLoop () {
        chunkGuardLoopIteration();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            doLoop();
        }, Tools.secToTicks(60*60)); // 1h
    }

    private void chunkGuardLoopIteration() {
        if (Properties.DEBUG_LOGGING) {
            System.out.println("Chunkguard running iteration");
        }

        unloadAllGuardedChunks();
        reloadGuardedChunksList();
        protectGuardedChunks();
    }

    /**
     * Sets all chunks to force loaded: false
     */
    private void unloadAllGuardedChunks() {
        for (Chunk chunk : guardedChunks) {
            if (Properties.DEBUG_LOGGING) {
                System.out.println("Unloading chunk: " + chunk.getX() + ":" + chunk.getZ());
            }
            chunk.setForceLoaded(false);
            chunk.removePluginChunkTicket(plugin);
        }
    }

    /**
     * Reloads the list of chunks that is guarded based on what users can afford
     */
    private void reloadGuardedChunksList() {
        guardedChunks.clear();

        ArrayList<PlayerData> playerDatas = Main.playerDataManager.getPlayerDataList();
        for (PlayerData playerData : playerDatas) {
            Chunk[] chunks = chunkPosToChunks(playerData.getGuardedChunks());

            // make sure player can afford
            if (!playerCanAfford(playerData, getChunkProtectionPrice(chunks.length))) {
                continue;
            }

            // actually mark protected
            withdrawPlayer(playerData, getChunkProtectionPrice(chunks.length));
            guardedChunks.addAll(Arrays.stream(chunks).toList());
        }
    }

    /**
     * Loops through guarded chunks and sets them to force loaded
     */
    private void protectGuardedChunks() {
        for (Chunk chunk : guardedChunks) {
            if (Properties.DEBUG_LOGGING) {
                System.out.println("Setting chunk as force loaded: " + chunk.getX() + ":" + chunk.getZ());
            }
            chunk.setForceLoaded(true);
            chunk.addPluginChunkTicket(plugin);
        }
    }

    private Chunk[] chunkPosToChunks(ArrayList<int[]> chunkPos) {
        Chunk[] chunks = new Chunk[chunkPos.size()];
        for (int i = 0; i < chunkPos.size(); i++) {
            int[] singleChunkPos = chunkPos.get(i);
            chunks[i] = chunkPosToChunk(singleChunkPos);
        }

        return chunks;
    }

    private Chunk chunkPosToChunk(int[] chunkPos) {
        return Bukkit.getWorlds().get(0).getChunkAt(chunkPos[0], chunkPos[1]);
    }

    public int getChunkProtectionPrice(int numberOfChunks) {
        return numberOfChunks * 2000;
    }

    /**
     * Checks if a chunk is guarded
     * @param chunk the chunk
     * @return true if protected
     */
    public boolean isChunkProtected(Chunk chunk) {
        return guardedChunks.contains(chunk);
    }

    /**
     * Registers the chunk as protected, and protects it immediately if the player can afford it.
     * @param player
     * @param chunk
     */
    public void startProtectingChunk(Player player, Chunk chunk) {
        PlayerData playerData = Main.playerDataManager.getPlayerData(player);
        getNumOfChunksProtectedForPlayer(player);
        playerData.chunkGuardWatchChunk(chunk);


        // set to force loaded if player can afford
        if (playerCanAfford(playerData, getChunkProtectionPrice(1))) {
            withdrawPlayer(playerData, getChunkProtectionPrice(1));
            guardedChunks.add(chunk);
            chunk.setForceLoaded(true);
            chunk.addPluginChunkTicket(plugin);
        }
    }

    public int getNumOfChunksProtectedForPlayer(Player player) {
        return Main.playerDataManager.getPlayerData(player).getGuardedChunks().size();
    }

    public boolean playerCanAfford(PlayerData playerData, int amount) {
        return playerData.getChunkGuardCoins() > amount;
    }

    public void withdrawPlayer(PlayerData playerData, int amount) {
        playerData.withdrawChunkGuardCoins(amount);
    }

    public void depositPlayer(PlayerData playerData, int amount) {
        if (amount < 0) {
            return;
        }

        playerData.depositChunkGuardCoins(amount);

        Chunk[] chunks = chunkPosToChunks(playerData.getGuardedChunks());

        // make sure player can afford
        if (!playerCanAfford(playerData, getChunkProtectionPrice(chunks.length))) {
            return;
        }

        // actually mark protected
        withdrawPlayer(playerData, getChunkProtectionPrice(chunks.length));
        guardedChunks.addAll(Arrays.stream(chunks).toList());

        // actually protect the guarded chunks!
        protectGuardedChunks();
    }

    public int getCurrentBalanceForPlayer(Player player) {
        return Main.playerDataManager.getPlayerData(player).getChunkGuardCoins();
    }

    public void stopWatchingChunk(PlayerData playerData, Chunk chunk) {
        playerData.chunkGuardStopWatchingChunk(chunk);
        chunk.setForceLoaded(false);
        chunk.removePluginChunkTicket(plugin);
        guardedChunks.removeIf(chunk1 -> chunk1.getX() == chunk.getX() && chunk1.getZ() == chunk.getZ());
    }
}
