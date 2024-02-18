package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.chatentities.InfoChat;
import com.johansvartdal.landlord.chatentities.RouletteChat;
import com.johansvartdal.landlord.events.arenafight.ArenaFight1;
import com.johansvartdal.landlord.events.taxevents.ChooseTreasuryEvent;
import com.johansvartdal.landlord.levels.S1Level3;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Random;

import static com.johansvartdal.landlord.Tools.debugLog;

public class Adm implements CommandExecutor {

    private Main plugin;

    public Adm(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("adm").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.isOp() || !Properties.DEV_CHEAT_MODE) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW));
            return true;
        }

        if (strings.length == 0) {
            Tools.printMenuHeader(player, "COMMANDS");
            Tools.printMenuOption(player, "/adm", "clear");
            Tools.printMenuOption(player, "/adm", "lockday");
            Tools.printMenuOption(player, "/adm", "lladv");
            Tools.printMenuOption(player, "/adm", "motherload");
            Tools.printMenuOption(player, "/adm", "forceup");
            Tools.printMenuOption(player, "/adm", "testevent");
            Tools.printMenuOption(player, "/adm", "testarena");
            Tools.printMenuOption(player, "/adm", "testeffect");
            Tools.printMenuOption(player, "/adm", "countdown");
            Tools.printMenuOption(player, "/adm", "haste");
            Tools.printMenuOption(player, "/adm", "testtreasury");
            Tools.printMenuOption(player, "/adm", "forcelvl1");
            Tools.printMenuOption(player, "/adm", "testjail");
            Tools.printMenuOption(player, "/adm", "copybook");
            Tools.printMenuOption(player, "/adm", "testlangfallback");
            Tools.printMenuOption(player, "/adm", "roulettechat");
            Tools.printMenuOption(player, "/adm", "age");
            return true;
        }

        if (strings[0].equals("clear")) {
            player.getWorld().setTime(0);
            player.getWorld().setClearWeatherDuration((int) Tools.secToTicks(60*60));
        }else if (strings[0].equals("lladv")) {
            player.teleport(new Location(Bukkit.getWorld("lladv"), 194, 81, -112));
            Tools.tellPlayer(player, "Welcome to lladv");
            player.setGameMode(GameMode.CREATIVE);
        }else if (strings[0].equals("motherload")) {
            Bank.depositPlayerWithoutTax(player, 20000);
            Tools.tellPlayer(player, "Money reloaded!", ChatColor.GREEN);
        }else if (strings[0].equals("forceup")) {
            Tools.tellPlayer(player, "Forcing upgrade!", ChatColor.YELLOW);
            LevelManager.forceProceedToNextLevel();
        }else if (strings[0].equals("testevent")) {
            LandlordEventManager.startEvent(new ChooseTreasuryEvent(plugin));
        }else if (strings[0].equals("testarena")) {
            ArenaFight1 arenaFight1 = new ArenaFight1(plugin);
            LandlordEventManager.startEvent(arenaFight1);
        }else if (strings[0].equals("testeffect")) {
            testEffectUnlockChunk(player);
        }else if (strings[0].equals("countdown")) {
            Tools.performTaskAfterCountdown(() -> {
                Tools.tellPlayer(player, "Time's up!");
            }, "time left: ", 60);
        }else if (strings[0].equals("haste")) {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.FAST_DIGGING, (int) Tools.secToTicks(10), 1);
            player.addPotionEffect(potionEffect);
        }else if (strings[0].equals("testtreasury")) {
            LandlordEventManager.startEvent(new ChooseTreasuryEvent(plugin));
        }else if (strings[0].equals("lockday")) {
            dayLocked = !dayLocked;
            if (dayLocked) {
                Tools.broadcastMessage("Day is now locked!", ChatColor.GREEN);
                lockDay();
            }else {
                Tools.broadcastMessage("Day is no longer locked!", ChatColor.GREEN);
                if (dayLocker != null) {
                    dayLocker.cancel();
                }
            }
        }else if (strings[0].equals("forcelvl1")) {
            LevelManager.startLevel1();
        }else if (strings[0].equals("testjail")) {
            JailManager.sendToJail(plugin, player, LangDict.getString("playerEvents.jail.jailReasonTax"), LangDict.getString("playerEvents.jail.jailOutTax"), 60);
        }else if (strings[0].equals("copybook")) {
            copyBook(player);
        }else if (strings[0].equals("testlangfallback")) {
            Tools.tellPlayer(player, LangDict.getString("commandResponses.successMessages.onlyEnglish"));
            S1Level3 level3 = new S1Level3(plugin);
            Tools.givePlayerItemOrDrop(player, level3.getBook().produceAndGetBook(), true);
        }else if (strings[0].equals("roulettechat")) {
            Tools.tellPlayer(new RouletteChat(), player, "This is a test rouletteMessage");
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                Tools.tellPlayer(new RouletteChat(), player, "And this is a colored one", ChatColor.GREEN);
            }, Tools.secToTicks(3));
        }else if (strings[0].equalsIgnoreCase("age")) {
            attemptGrowSelfStationary(player);
            attemptGrowUpwards(player);
            attemptGrowSideWays(player);
        }
        return true;
    }

    private void attemptGrowSelfStationary(Player player) {
        Block block = player.getLocation().getBlock();
        System.out.println("Aging block: " + block.getX() + ":" + block.getY() + ":" + block.getZ());

        if (!(block.getBlockData() instanceof Ageable)) {
            Tools.tellPlayer(new ErrorChat(), player, "Block is not instance of Ageable: " + block.getX() + ":" + block.getY() + ":" + block.getZ());
            return;
        }

        Ageable blockData = (Ageable) block.getBlockData();
        if (blockData.getAge() >= blockData.getMaximumAge()) {
            Tools.tellPlayer(new InfoChat(), player, "Block already at max age: " + blockData.getAge());
            return;
        }
        blockData.setAge(blockData.getAge() + 1);
        block.setBlockData(blockData);

        Tools.tellPlayer(new InfoChat(), player, "Grew crop to " + blockData.getAge());
    }

    @Getter
    @AllArgsConstructor
    class UpwardsGrowingPlant {
        Material material;
        int heightRangeMin;
        int heightRangeMax;
    }

    private void attemptGrowUpwards(Player player) {
        UpwardsGrowingPlant[] upGrowingPlants = new UpwardsGrowingPlant[]{
                new UpwardsGrowingPlant(Material.SUGAR_CANE, 3, 3),
                new UpwardsGrowingPlant(Material.CACTUS, 3, 3),
                new UpwardsGrowingPlant(Material.KELP_PLANT, 2, 26),
                new UpwardsGrowingPlant(Material.BAMBOO, 3, 3)
        };
        Block block = player.getLocation().getBlock();

        for (UpwardsGrowingPlant upGrowingPlant: upGrowingPlants) {
            if (!block.getType().equals(upGrowingPlant.material)) {
                continue;
            }

            Block blockTwiceBelow = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() - 2, block.getZ());
            Block blockAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() + 1, block.getZ());

            if (!blockTwiceBelow.getType().equals(upGrowingPlant.material) && blockAbove.getType().equals(Material.AIR)) {
                blockAbove.setType(upGrowingPlant.material);
            }

            if (upGrowingPlant.material == Material.CACTUS) {
                Block blockSouthAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() + 1, block.getZ() + 1);
                Block blockNorthAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY() + 1, block.getZ() - 1);
                Block blockEastAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX() + 1, block.getY() + 1, block.getZ());
                Block blockWestAbove = Bukkit.getWorlds().get(0).getBlockAt(block.getX() - 1, block.getY() + 1, block.getZ());

                if (!blockSouthAbove.getType().equals(Material.AIR) || !blockNorthAbove.getType().equals(Material.AIR) || !blockEastAbove.getType().equals(Material.AIR) || !blockWestAbove.getType().equals(Material.AIR)) {
                    blockAbove.setType(Material.AIR);

                    ItemStack itemStack = new ItemStack(Material.CACTUS);
                    itemStack.setAmount(1);
                    itemStack.setType(Material.CACTUS);

                    blockAbove.getWorld().dropItem(blockAbove.getLocation().add(0.5,0,0.5), itemStack);
                }
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class SideGrowingPlant {
        Material seed;
        Material stem;
        Material result;
        Material attached;
    }

    private void attemptGrowSideWays(Player player) {
        SideGrowingPlant[] sideGrowingPlant = new SideGrowingPlant[]{
                new SideGrowingPlant(Material.MELON_SEEDS, Material.MELON_STEM, Material.MELON, Material.ATTACHED_MELON_STEM),
                new SideGrowingPlant(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM, Material.PUMPKIN, Material.ATTACHED_PUMPKIN_STEM)
        };

        Block block = player.getLocation().getBlock();

        if (!(block.getBlockData() instanceof Ageable)) {
            return;
        }

        for (SideGrowingPlant plant: sideGrowingPlant) {
            if (!block.getType().equals(plant.getStem())) {
                continue;
            }

            BlockData blockData = block.getBlockData();
            Ageable ageableBlockData = (Ageable) blockData;

            if (ageableBlockData.getAge() != ageableBlockData.getMaximumAge()) {
                Tools.tellPlayer(new ErrorChat(), player, "Cannot grow at " + block.getX() + ":" + block.getY() + ":" + block.getZ() + " due to it being too young");
                return;
            }

            Block blockSouth = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY(), block.getZ() + 1);
            Block blockNorth = Bukkit.getWorlds().get(0).getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
            Block blockEast = Bukkit.getWorlds().get(0).getBlockAt(block.getX() + 1, block.getY(), block.getZ());
            Block blockWest = Bukkit.getWorlds().get(0).getBlockAt(block.getX() - 1, block.getY(), block.getZ());

            if (blockSouth.getType().equals(plant.result) || blockNorth.getType().equals(plant.result) || blockEast.getType().equals(plant.result) || blockWest.getType().equals(plant.result)) {
                Tools.tellPlayer(new ErrorChat(), player, "Cannot grow at " + block.getX() + ":" + block.getY() + ":" + block.getZ() + " due to it already having melon nearby");
                return;
            }

            Block[] blocksToCheck = new Block[]{blockSouth, blockNorth, blockEast, blockWest};
            ArrayList<Block> availableBlocks = new ArrayList<>();
            for (Block checkingBlock : blocksToCheck) {
                if (!checkingBlock.getType().equals(Material.AIR)) {
                    Tools.tellPlayer(new ErrorChat(), player, "Cannot grow at " + checkingBlock.getX() + ":" + checkingBlock.getY() + ":" + checkingBlock.getZ() + " due to not being AIR");
                    continue;
                }

                Block blockBelowCheckingBlock = checkingBlock.getWorld().getBlockAt(checkingBlock.getX(), checkingBlock.getY() - 1, checkingBlock.getZ());
                if (blockBelowCheckingBlock.getType() != Material.DIRT && blockBelowCheckingBlock.getType() != Material.GRASS_BLOCK && blockBelowCheckingBlock.getType() != Material.FARMLAND) {
                    Tools.tellPlayer(new ErrorChat(), player, "Cannot grow at " + checkingBlock.getX() + ":" + checkingBlock.getY() + ":" + checkingBlock.getZ() + " due to wrong surface underneath");
                    continue;
                }

                availableBlocks.add(checkingBlock);
            }

            if (availableBlocks.isEmpty()) {
                return;
            }

            Random random = new Random();
            int randomNumber = random.nextInt(availableBlocks.size());
            Block resultingBlock = availableBlocks.get(randomNumber);
            resultingBlock.setType(plant.result);
            Tools.tellPlayer(new InfoChat(), player, "Growing at " + resultingBlock.getX() + ":" + resultingBlock.getY() + ":" + resultingBlock.getZ());

            block.setType(plant.attached);

            Directional directional = (Directional) block.getBlockData();
            if (resultingBlock.getZ() > block.getZ()) {
                directional.setFacing(BlockFace.SOUTH);
                Tools.tellPlayer(new InfoChat(), player, "Setting block direction to SOUTH");
            }else if (resultingBlock.getZ() < block.getZ()) {
                directional.setFacing(BlockFace.NORTH);
                Tools.tellPlayer(new InfoChat(), player, "Setting block direction to NORTH");
            }else if (resultingBlock.getX() > block.getX()) {
                directional.setFacing(BlockFace.EAST);
                Tools.tellPlayer(new InfoChat(), player, "Setting block direction to EAST");
            }else if (resultingBlock.getX() < block.getX()) {
                directional.setFacing(BlockFace.WEST);
                Tools.tellPlayer(new InfoChat(), player, "Setting block direction to WEST");
            }
            block.setBlockData(directional);
        }
    }

    private void copyBook(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (!itemInMainHand.getType().equals(Material.WRITABLE_BOOK)) {
            Tools.tellPlayer(new ErrorChat(), player, "This is not a book");
            return;
        }

        BookMeta bookMeta = (BookMeta) itemInMainHand.getItemMeta();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < bookMeta.getPageCount()+1; i++) {
            if (i > 1) {
                stringBuilder.append("\n\n\n\n\n\n\n");
            }
            debugLog("Attempting to read page: " + i);
            stringBuilder.append(bookMeta.getPage(i));
        }

        Tools.write("Books/Book" + System.currentTimeMillis() + ".txt", stringBuilder.toString());
        Tools.tellPlayer(player, "SUCCESS! Book has been saved as txt in plugin dir!", ChatColor.GREEN);
    }

    boolean dayLocked = false;
    BukkitTask dayLocker = null;

    private void lockDay() {
        if (dayLocked) {
            Bukkit.getWorld("lladv").setTime(3000);
            Bukkit.getWorld("world").setTime(3000);

            dayLocker = Bukkit.getScheduler().runTaskLater(plugin, this::lockDay, Tools.secToTicks(60*2));
        }
    }

    private void testEffectUnlockChunk(Player player) {
        // levitation effect
        PotionEffect levitationFast = new PotionEffect(PotionEffectType.LEVITATION, (int) Tools.secToTicks(20), 3);
        player.addPotionEffect(levitationFast);

        // 6 sec
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // levitation
            player.removePotionEffect(PotionEffectType.LEVITATION);
            PotionEffect levitationSlow = new PotionEffect(PotionEffectType.LEVITATION, (int) Tools.secToTicks(9), 1);
            player.addPotionEffect(levitationSlow);

            // 3 sec
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // get chunk
                String direction = getPlayerFacingDirection(player);
                Chunk chunkAtDirection = getChunkAtDirection(player, direction);

                // play anim and sound
                SpecialEffects.playChunkUnlockAnim(chunkAtDirection, (int) player.getLocation().getY());

                // play sounds
                player.playSound(player, Sound.ITEM_TOTEM_USE, 1, 0);
                Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
                    }
                }, Tools.secToTicks(1));
            }, Tools.secToTicks(2));

            // 8 sec -> slow falling
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.removePotionEffect(PotionEffectType.LEVITATION);
                PotionEffect slowFalling = new PotionEffect(PotionEffectType.SLOW_FALLING, (int) Tools.secToTicks(6), 3);
                player.addPotionEffect(slowFalling);
            }, Tools.secToTicks(3));

        }, Tools.secToTicks(5));
    }

    public String getPlayerFacingDirection(Player player) {
        int yaw = (int) player.getLocation().getYaw();

        // normalize the angle to be between 0 and 359
        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360;
        }

        // determine the direction based on the angle
        if (yaw >= 45 && yaw < 135) {
            return "west"; // west
        } else if (yaw >= 135 && yaw < 225) {
            return "north";  // north
        } else if (yaw >= 225 && yaw < 315) {
            return "east";  // east
        } else {
            return "south";  // south
        }
    }

    public Chunk getChunkAtDirection(Player player, String direction) {
        Chunk currentChunk = player.getLocation().getChunk();
        int chunkX = currentChunk.getX();
        int chunkZ = currentChunk.getZ();
        switch (direction) {
            case "north":
                chunkZ -= 1;
                break;
            case "south":
                chunkZ += 1;
                break;
            case "west":
                chunkX -= 1;
                break;
            case "east":
                chunkX += 1;
                break;
        }
        return player.getWorld().getChunkAt(chunkX, chunkZ);
    }
}
