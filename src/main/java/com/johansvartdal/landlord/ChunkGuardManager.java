package com.johansvartdal.landlord;

import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.CropState;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ChunkGuardManager{

    private static ArrayList<Chunk> guardedChunks = new ArrayList<>();
    private final Random random = new Random();

    private Main plugin;

    public ChunkGuardManager(Main plugin) {
        this.plugin = plugin;
        doLoop();
    }

    /**
     * Loops a chunkGuard iteration
     */
    private void doLoop () {
        int iterationTime = 60*20;
        if (Properties.DEV_CHEAT_MODE) {
            iterationTime = 30;
        }

        chunkGuardLoopIteration();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            doLoop();
        }, Tools.secToTicks(iterationTime)); // 20 minutes
    }

    /**
     * Single chunk loop iteration
     */
    private void chunkGuardLoopIteration() {
        if (Properties.DEBUG_LOGGING) {
            System.out.println("Chunkguard running iteration");
        }

        // make sure chunkGuard is unlocked before looping
        if (LevelManager.featureUnlocked("chunkguard")) {
            reloadGuardedChunksList();
            loopOverChunks();
        }
    }

    /**
     * Loops over all chunks
     */
    private void loopOverChunks() {
        if (Properties.DEBUG_LOGGING) System.out.println("Currently protected chunks: " + guardedChunks.size());
        for (Chunk chunk: guardedChunks) {
            if (Properties.DEBUG_LOGGING) System.out.println("Protecting chunk at: " + chunk.getX() + ":" + chunk.getZ());

            loopOverBlocksInChunk(chunk);
        }
    }

    /**
     * Loops over all blocks within the chunk
     * @param chunk
     */
    private void loopOverBlocksInChunk(Chunk chunk) {
        int chunkX = chunk.getX()*16;
        int chunkZ = chunk.getZ()*16;

        for (int y = -64; y <= 318; y++) {
            for (int x = chunkX; x < chunkX + 16; x++) {
                for (int z = chunkZ; z < chunkZ + 16; z++) {
                    attemptGrowBlock(chunk.getWorld().getBlockAt(x, y, z));
                }
            }
        }
    }

    /**
     * Attempts to grow the given block
     * @param block
     */
    private void attemptGrowBlock(Block block) {
        // Can the block grow?
        if (!(block.getBlockData() instanceof Ageable)) {
            return;
        }

        // make sure not already fully grown
        Ageable blockData = (Ageable) block.getBlockData();
        if (blockData.getAge() == blockData.getMaximumAge()) {
            return;
        }

        // actually grow it
        if (random.nextFloat() < 0.8) {
            int currentAge = blockData.getAge();
            blockData.setAge(currentAge + 1);
            block.setBlockData(blockData);
            System.out.println("Grew block at: " + block.getX() + ":" + block.getY() + ":" + block.getZ() + ", to age: " + blockData.getAge());
        }else {
            System.out.println("Did not grow block at position: " + block.getX() + ":" + block.getY() + ":" + block.getZ() + ", with age: " + blockData.getAge());
        }
    }

    /**
     * Reloads the list of chunks that is guarded based on what users can afford
     */
    private void reloadGuardedChunksList() {
        guardedChunks.clear();
        if (Properties.DEBUG_LOGGING) System.out.println("Checking which chunks to protect");

        ArrayList<PlayerData> playerDatas = Main.playerDataManager.getPlayerDataList();
        for (PlayerData playerData : playerDatas) {
            Chunk[] chunks = chunkPosToChunks(playerData.getGuardedChunks());

            // make sure player can afford
            if (!playerCanAfford(playerData, getChunkProtectionPrice(chunks.length))) {
                continue;
            }

            // actually mark protected
            if (!Properties.DEV_CHEAT_MODE) {
                withdrawPlayer(playerData, getChunkProtectionPrice(chunks.length));
            }
            guardedChunks.addAll(Arrays.stream(chunks).toList());

            if (Properties.DEBUG_LOGGING) System.out.println("Player: " + playerData.getUsername() + " will have: " + chunks.length + " chunks protected!");
        }
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
    }

    public int getCurrentBalanceForPlayer(Player player) {
        return Main.playerDataManager.getPlayerData(player).getChunkGuardCoins();
    }

    public void stopWatchingChunk(PlayerData playerData, Chunk chunk) {
        playerData.chunkGuardStopWatchingChunk(chunk);
        guardedChunks.removeIf(chunk1 -> chunk1.getX() == chunk.getX() && chunk1.getZ() == chunk.getZ());
    }
}
