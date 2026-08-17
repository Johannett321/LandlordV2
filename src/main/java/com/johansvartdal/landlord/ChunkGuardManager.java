package com.johansvartdal.landlord;

import com.johansvartdal.landlord.levels.LevelManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChunkGuardManager{

    @Getter
    @AllArgsConstructor
    class GuardedChunk {
        private Chunk chunk;
        private PlayerData playerData;
    }

    @Getter
    @AllArgsConstructor
    class CachedBlock {
        Block block;
        GuardedChunk guardedChunk;
    }

    @Getter
    @AllArgsConstructor
    class UpwardsGrowingPlant {
        private Material[] growsAs;
        private Material material;
        private Material growsIn;
        private int maxHeight;
        private boolean longGrower;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class SideGrowingPlant {
        private Material seed;
        private Material stem;
        private Material result;
        private Material attached;
    }

    private final UpwardsGrowingPlant[] upGrowingPlants = new UpwardsGrowingPlant[]{
            new UpwardsGrowingPlant(new Material[]{Material.SUGAR_CANE}, Material.SUGAR_CANE, Material.AIR, 3, false),
            new UpwardsGrowingPlant(new Material[]{Material.CACTUS},Material.CACTUS, Material.AIR, 3, false),
            new UpwardsGrowingPlant(new Material[]{Material.KELP, Material.KELP_PLANT}, Material.KELP, Material.WATER, 17, true),
            new UpwardsGrowingPlant(new Material[]{Material.BAMBOO_SAPLING, Material.BAMBOO}, Material.BAMBOO, Material.AIR, 19, true)
    };

    private final SideGrowingPlant[] sideGrowingPlants = new SideGrowingPlant[]{
            new SideGrowingPlant(Material.MELON_SEEDS, Material.MELON_STEM, Material.MELON, Material.ATTACHED_MELON_STEM),
            new SideGrowingPlant(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM, Material.PUMPKIN, Material.ATTACHED_PUMPKIN_STEM)
    };

    private final ArrayList<CachedBlock> cachedPotentiallyGrowable = new ArrayList<>();
    private static ArrayList<GuardedChunk> guardedChunks = new ArrayList<>();
    private final Random random = new Random();
    private Long lastCacheTime = 0L;

    private Main plugin;

    public ChunkGuardManager(Main plugin) {
        this.plugin = plugin;
        doLoop();
    }

    /**
     * Loops a chunkGuard iteration
     */
    private void doLoop() {
        int iterationTime = 60*3; // three minutes
        if (Properties.DEV_CHEAT_MODE) {
            iterationTime = 5;
        }

        // check if we should update cache
        if (System.currentTimeMillis() - lastCacheTime > 1000*60*60 || Properties.DEV_CHEAT_MODE) {
            runCacheIteration();
        }

        // run grow iteration
        runGrowIteration();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            doLoop();
        }, Tools.secToTicks(iterationTime)); // 20 minutes
    }

    /**
     * Loops over all protected chunks, and checks if they should be cached. Caches if they should
     */
    private void runCacheIteration() {
        // make sure chunkGuard is unlocked before looping
        if (!LevelManager.featureUnlocked("chunkguard")) {
            return;
        }

        long growStartTime;
        if (Properties.DEBUG_LOGGING) {
            growStartTime = System.currentTimeMillis();
            System.out.println("ChunkGuard starting cache mission at: " + growStartTime);
        }

        reloadGuardedChunksList();
        cachedPotentiallyGrowable.clear();
        cacheMissionLoopOverChunks();

        lastCacheTime = System.currentTimeMillis();

        if (Properties.DEBUG_LOGGING) System.out.println("ChunkGuard cache mission completed at: " + System.currentTimeMillis() + ". Took: " + (System.currentTimeMillis() - growStartTime) + " milli(s)! Guarded chunks: " + guardedChunks.size());
    }

    /**
     * Single chunk loop iteration
     */
    private void runGrowIteration() {
        // make sure chunkGuard is unlocked before looping
        if (!LevelManager.featureUnlocked("chunkguard")) {
            return;
        }

        Long growStartTime;
        if (Properties.DEBUG_LOGGING) {
            growStartTime = System.currentTimeMillis();
            System.out.println("ChunkGuard starting grow mission at: " + growStartTime);
        }

        // actually do the cache loop
        loopOverCachedBlocks();

        if (Properties.DEBUG_LOGGING) System.out.println("ChunkGuard grow mission completed at: " + System.currentTimeMillis() + ". Took: " + (System.currentTimeMillis() - growStartTime) + " milli(s)!");
    }

    /**
     * Loops over all chunks
     */
    private void cacheMissionLoopOverChunks() {
        for (GuardedChunk guardedChunk: guardedChunks) {
            cacheMissionLoopOverChunkBlocks(guardedChunk);
        }

        // sort by Y and reverse list to get descending order
        cachedPotentiallyGrowable.sort(Comparator.comparing(cachedBlock -> cachedBlock.getBlock().getY()));
        Collections.reverse(cachedPotentiallyGrowable);
    }

    /**
     * Loops over all blocks within the chunk
     * @param guardedChunk
     */
    private void cacheMissionLoopOverChunkBlocks(GuardedChunk guardedChunk) {
        int chunkX = guardedChunk.getChunk().getX()*16;
        int chunkZ = guardedChunk.getChunk().getZ()*16;

        Chunk chunk = guardedChunk.getChunk();
        World world = chunk.getWorld();

        // find tallest block in chunk
        int tallestBlockInChunk = -64;
        for (int x = chunkX; x < chunkX + 16; x++) {
            for (int z = chunkZ; z < chunkZ + 16; z++) {
                for (int y = 318; y >= tallestBlockInChunk; y--) {
                    if (!world.getBlockAt(x,y,z).getType().equals(Material.AIR)) {
                        if (y > tallestBlockInChunk) {
                            tallestBlockInChunk = y;
                        }
                    }
                }
            }
        }

        // loop through all blocks up til tallest block in chunk
        for (int y = tallestBlockInChunk; y >= -64; y--) {
            for (int x = chunkX; x < chunkX + 16; x++) {
                for (int z = chunkZ; z < chunkZ + 16; z++) {
                    cacheIfGrowableOrCloseTo(guardedChunk, chunk.getWorld().getBlockAt(x, y, z));
                }
            }
        }
    }

    // check if block should be cached, and caches it
    private void cacheIfGrowableOrCloseTo(GuardedChunk guardedChunk, Block block) {
        Material material = block.getType();

        // check if upgrowing
        if (Arrays.stream(upGrowingPlants).anyMatch(upwardsGrowingPlant -> Arrays.stream(upwardsGrowingPlant.growsAs).toList().contains(material))) {
            if (cachedPotentiallyGrowable.stream().noneMatch(cachedBlock -> cachedBlock.getBlock().equals(block))) {
                cachedPotentiallyGrowable.add(new CachedBlock(block, guardedChunk));
            }

            for (int i = 1; i <= 7; i++) {
                if (block.getLocation().getY() + i >= 317) {
                    break;
                }

                Block nextBlock = block.getLocation().add(0,i,0).getBlock();

                if (cachedPotentiallyGrowable.stream().noneMatch(cachedBlock -> cachedBlock.getBlock().equals(nextBlock))) {
                    cachedPotentiallyGrowable.add(new CachedBlock(nextBlock, guardedChunk));
                }
            }
            return;
        }

        // check if side growing
        if (block.getBlockData() instanceof Ageable) {
            if (cachedPotentiallyGrowable.stream().noneMatch(cachedBlock -> cachedBlock.getBlock().equals(block))) {
                cachedPotentiallyGrowable.add(new CachedBlock(block, guardedChunk));
            }
        }
    }

    /**
     * Loops over the cached blocks, and attempts to grow them
     */
    private void loopOverCachedBlocks() {
        for (CachedBlock cachedBlock: cachedPotentiallyGrowable){
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(cachedBlock.getGuardedChunk().getPlayerData().getUsername());

            if (offlinePlayer.isOnline() && cachedBlock.getBlock().getLocation().distance(offlinePlayer.getPlayer().getLocation()) < 128) {
                continue;
            }

            attemptGrow(cachedBlock.getBlock());
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
            if (!Properties.DEV_CHEAT_MODE) {
                withdrawPlayer(playerData, getChunkProtectionPrice(chunks.length));
            }

            Arrays.stream(chunks).toList().forEach(chunk -> {
                guardedChunks.add(new GuardedChunk(chunk, playerData));
            });

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
            GuardedChunk guardedChunk = new GuardedChunk(chunk, playerData);
            guardedChunks.add(guardedChunk);
            cacheMissionLoopOverChunkBlocks(guardedChunk);
        }
    }









    /**
     * Attempts to grow the given block
     * @param block The block to grow
     */
    public void attemptGrow(Block block) {
        attemptGrowSelfStationary(block);
        attemptGrowUpwards(block);
        attemptGrowSideWays(block);
    }

    /**
     * Attempts to grow the given block stationary (in the same position)
     * @param block
     */
    private void attemptGrowSelfStationary(Block block) {
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
        if (random.nextFloat() < 0.525) {
            int currentAge = blockData.getAge();
            blockData.setAge(currentAge + 1);
            block.setBlockData(blockData);
            System.out.println("Grew block at: " + block.getX() + ":" + block.getY() + ":" + block.getZ() + ", to age: " + blockData.getAge());
        }
    }

    private void attemptGrowUpwards(Block block) {
        for (UpwardsGrowingPlant upGrowingPlant: upGrowingPlants) {
            // try to map the block to one of the objects
            if (!Arrays.stream(upGrowingPlant.growsAs).toList().contains(block.getType())) {
                continue;
            }

            if (upGrowingPlant.longGrower) {
                attemptLongGrow(upGrowingPlant, block);
            }else {
                attemptShortGrow(upGrowingPlant, block);
            }
        }
    }

    /**
     * Attempts to grow short plants, like cactus or sugar cane
     * @param upGrowingPlant the plant
     * @param block the block of the plant
     */
    private void attemptShortGrow(UpwardsGrowingPlant upGrowingPlant, Block block) {
        Block blockTwiceBelow = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() - 2, block.getZ());
        Block blockAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() + 1, block.getZ());

        // make sure it does not grow too tall
        if (blockTwiceBelow.getType().equals(upGrowingPlant.material)) {
            return;
        }

        // make sure we have air above
        if (!blockAbove.getType().equals(Material.AIR)) {
            return;
        }

        // generate some randomness
        if (random.nextFloat() > 0.525) {
            return;
        }

        // grow
        blockAbove.setType(upGrowingPlant.material);

        /*--- Special cactus instructions ---*/
        // make sur we are working with cactus
        if (upGrowingPlant.material != Material.CACTUS) {
            return;
        }

        Block blockSouthAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() + 1, block.getZ() + 1);
        Block blockNorthAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() + 1, block.getZ() - 1);
        Block blockEastAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ());
        Block blockWestAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ());

        // pop cactus if there are blocks too close
        if (!blockSouthAbove.getType().equals(Material.AIR) || !blockNorthAbove.getType().equals(Material.AIR) || !blockEastAbove.getType().equals(Material.AIR) || !blockWestAbove.getType().equals(Material.AIR)) {
            blockAbove.setType(Material.AIR);

            ItemStack itemStack = new ItemStack(Material.CACTUS);
            itemStack.setAmount(1);
            itemStack.setType(Material.CACTUS);

            blockAbove.getWorld().dropItem(blockAbove.getLocation().add(0.5,0,0.5), itemStack);
        }
    }

    /**
     * Attempts to grow a long growing plant like bamboo or kelp.
     * @param upGrowingPlant The plant
     * @param block The block of the plant
     */
    private void attemptLongGrow(UpwardsGrowingPlant upGrowingPlant, Block block) {
        Block blockAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() + 1, block.getZ());
        Block sixBlocksBelow = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() - 6, block.getZ());

        // generate some randomness
        if (random.nextFloat() > 0.325) {
            return;
        }

        // if block is bamboo sapling, replace with proper bamboo
        if (block.getType().equals(Material.BAMBOO_SAPLING)) {
            block.setType(Material.BAMBOO);
        }

        // make sure we are working with an ageable
        if (!(block.getBlockData() instanceof Ageable blockData)) {
            if (Properties.DEBUG_LOGGING) System.out.println("Did not grow block due to it not being ageable: " + block.getX() + ":" + block.getY() + ":" + block.getZ());
            return;
        }

        // make sure the plant is not too old
        if (blockData.getAge() >= (block.getType().equals(Material.BAMBOO) ? 2 : blockData.getMaximumAge())) {
            if (Properties.DEBUG_LOGGING) System.out.println("Did not grow block due to age: " + blockData.getAge() + ", " + block.getX() + ":" + block.getY() + ":" + block.getZ());
            return;
        }

        // make sure bamboo plant does not get too tall (if bamboo)
        if (upGrowingPlant.material.equals(Material.BAMBOO) && sixBlocksBelow.getType().equals(Material.BAMBOO)) {
            if (Properties.DEBUG_LOGGING) System.out.println("Did not grow bamboo any taller. Already to tall: " + block.getX() + ":" + block.getY() + ":" + block.getZ());
            return;
        }

        // make sure the block above is something the plant can grow in
        if (!blockAbove.getType().equals(upGrowingPlant.growsIn)) {
            if (Properties.DEBUG_LOGGING) System.out.println("Cannot grow block, because " + block.getType().name() + " only grows in " + upGrowingPlant.growsIn + " while the block above is " + blockAbove.getType().name());
            return;
        }

        // replacing sapling
        if (block.getType() != upGrowingPlant.getMaterial()) {
            block.setType(upGrowingPlant.material);
        }

        // setting material
        blockAbove.setType(upGrowingPlant.material);

        // setting age as long as we are not working with a bamboo
        if (!upGrowingPlant.material.equals(Material.BAMBOO)) {
            Ageable blockAboveData = (Ageable) blockAbove.getBlockData();
            blockAboveData.setAge(blockData.getAge() + 1);
            blockAbove.setBlockData(blockAboveData);
        }
        if (Properties.DEBUG_LOGGING) System.out.println("Growing block: " + block.getX() + ":" + block.getY() + ":" + block.getZ());
    }

    /**
     * Attempts to grow a plant sideways
     * @param block
     */
    private void attemptGrowSideWays(Block block) {
        // make sure it is an ageable block
        if (!(block.getBlockData() instanceof Ageable)) {
            return;
        }

        // generate some randomness
        if (random.nextFloat() > 0.125) {
            return;
        }

        // loop though all sideGrowingPlants untill we find the one for this block
        for (SideGrowingPlant plant: sideGrowingPlants) {

            // check if the sideGrowingPlant matches this block
            if (!block.getType().equals(plant.getStem())) {
                continue;
            }

            BlockData blockData = block.getBlockData();
            Ageable ageableBlockData = (Ageable) blockData;

            // make sure plant is adult before attempting side growth.
            if (ageableBlockData.getAge() != ageableBlockData.getMaximumAge()) {
                if (Properties.DEBUG_LOGGING) System.out.println("Cannot grow at " + block.getX() + ":" + block.getY() + ":" + block.getZ() + " due to it being too young");
                return;
            }

            // get blocks from all directions
            Block blockSouth = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY(), block.getZ() + 1);
            Block blockNorth = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
            Block blockEast = Bukkit.getWorlds().get(0).getBlockAt(block.getX() + 1, block.getY(), block.getZ());
            Block blockWest = Bukkit.getWorlds().get(0).getBlockAt(block.getX() - 1, block.getY(), block.getZ());

            // make sure there are no blocks nearby with the fruit we are attempting to produce
            if (blockSouth.getType().equals(plant.result) || blockNorth.getType().equals(plant.result) || blockEast.getType().equals(plant.result) || blockWest.getType().equals(plant.result)) {
                if (Properties.DEBUG_LOGGING) System.out.println("Cannot grow at " + block.getX() + ":" + block.getY() + ":" + block.getZ() + " due to it already having melon nearby");
                return;
            }

            //check all the blocks and map out which we actually can produce a fruit on
            Block[] blocksToCheck = new Block[]{blockSouth, blockNorth, blockEast, blockWest};
            ArrayList<Block> availableBlocks = new ArrayList<>();
            for (Block checkingBlock : blocksToCheck) {
                // make sure it's empty space where we want to produce fruit
                if (!checkingBlock.getType().equals(Material.AIR)) {
                    if (Properties.DEBUG_LOGGING) System.out.println("Cannot grow at " + checkingBlock.getX() + ":" + checkingBlock.getY() + ":" + checkingBlock.getZ() + " due to not being AIR");
                    continue;
                }

                // make sure the block below the empty space is dirt, grass or farmland.
                Block blockBelowCheckingBlock = checkingBlock.getWorld().getBlockAt(checkingBlock.getX(), checkingBlock.getY() - 1, checkingBlock.getZ());
                if (blockBelowCheckingBlock.getType() != Material.DIRT && blockBelowCheckingBlock.getType() != Material.GRASS_BLOCK && blockBelowCheckingBlock.getType() != Material.FARMLAND) {
                    if (Properties.DEBUG_LOGGING) System.out.println("Cannot grow at " + checkingBlock.getX() + ":" + checkingBlock.getY() + ":" + checkingBlock.getZ() + "(" + block.getType().name() + ")" + " due to wrong surface underneath");
                    continue;
                }

                // add it as available space
                availableBlocks.add(checkingBlock);
            }

            // return if there are no places we can produce this fruit.
            if (availableBlocks.isEmpty()) {
                return;
            }

            // grow the plant at a random block
            int randomNumber = random.nextInt(availableBlocks.size());
            Block resultingBlock = availableBlocks.get(randomNumber);
            resultingBlock.setType(plant.result);

            if (Properties.DEBUG_LOGGING) System.out.println("Growing at " + resultingBlock.getX() + ":" + resultingBlock.getY() + ":" + resultingBlock.getZ());

            // enable the attached stem
            block.setType(plant.attached);

            // point the attached stem towards the fruit
            Directional directional = (Directional) block.getBlockData();
            if (resultingBlock.getZ() > block.getZ()) {
                directional.setFacing(BlockFace.SOUTH);
                if (Properties.DEBUG_LOGGING) System.out.println("Setting block direction to SOUTH");
            }else if (resultingBlock.getZ() < block.getZ()) {
                directional.setFacing(BlockFace.NORTH);
                if (Properties.DEBUG_LOGGING) System.out.println("Setting block direction to NORTH");
            }else if (resultingBlock.getX() > block.getX()) {
                directional.setFacing(BlockFace.EAST);
                if (Properties.DEBUG_LOGGING) System.out.println("Setting block direction to EAST");
            }else if (resultingBlock.getX() < block.getX()) {
                directional.setFacing(BlockFace.WEST);
                if (Properties.DEBUG_LOGGING) System.out.println("Setting block direction to WEST");
            }

            // update block data with direction
            block.setBlockData(directional);
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
     * Checks if a chunk is current guarded, meaning things will grow
     * @param chunk the chunk
     * @return true if protected
     */
    public boolean isChunkCurrentlyProtected(Chunk chunk) {
        return guardedChunks.stream().anyMatch(guardedChunk -> guardedChunk.getChunk().equals(chunk));
    }

    /**
     * Checks if a chunk is guarded
     * @param chunk the chunk
     * @return true if protected
     */
    public boolean isChunkProtectedByPlayer(Player player, Chunk chunk) {
        ArrayList<int[]> playersGuardedChunks = Main.playerDataManager.getPlayerData(player).getGuardedChunks();
        if (Properties.DEBUG_LOGGING) {
            System.out.println("--- Currently protected chunks by " + player.getDisplayName() + ": ---");
            playersGuardedChunks.forEach(protectedChunk -> {
                System.out.println(protectedChunk[0] + ":" + protectedChunk[1]);
            });
        }
        return playersGuardedChunks.stream().anyMatch(protectedChunk -> chunk.getX() == protectedChunk[0] && chunk.getZ() == protectedChunk[1]);
    }

    public int getNumOfChunksProtectedForPlayer(Player player) {
        return Main.playerDataManager.getPlayerData(player).getGuardedChunks().size();
    }

    public boolean playerCanAfford(PlayerData playerData, int amount) {
        return playerData.getChunkGuardCoins() >= amount;
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

        // attempt protect chunks
        for (Chunk chunk : chunks) {
            // make sure chunk is not already protected
            if (guardedChunks.stream().anyMatch(guardedChunk -> guardedChunk.getChunk().getX() == chunk.getX() && guardedChunk.getChunk().getZ() == chunk.getZ())) {
                continue;
            }

            // make sure player can afford
            if (!playerCanAfford(playerData, getChunkProtectionPrice(1))) {
                return;
            }

            // protect it
            withdrawPlayer(playerData, getChunkProtectionPrice(1));
            GuardedChunk guardedChunk = new GuardedChunk(chunk, playerData);
            guardedChunks.add(guardedChunk);
            cacheMissionLoopOverChunkBlocks(guardedChunk);
        }
    }

    public int getCurrentBalanceForPlayer(Player player) {
        return Main.playerDataManager.getPlayerData(player).getChunkGuardCoins();
    }

    public void stopWatchingChunk(PlayerData playerData, Chunk chunk) {
        playerData.chunkGuardStopWatchingChunk(chunk);
        guardedChunks.removeIf(guardedChunk -> guardedChunk.getChunk().getX() == chunk.getX() && guardedChunk.getChunk().getZ() == chunk.getZ());
    }
}
