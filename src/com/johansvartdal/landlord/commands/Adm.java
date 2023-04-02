package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.events.Preparations;
import com.johansvartdal.landlord.events.TestEvent;
import com.johansvartdal.landlord.events.adventure.IcyHillsEvent;
import com.johansvartdal.landlord.events.arenafight.ArenaFight1;
import com.johansvartdal.landlord.events.taxevents.ChooseTreasuryEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class Adm implements CommandExecutor {

    private Main plugin;

    public Adm(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("adm").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;
        if (!player.isOp() || !Properties.DEBUG_MODE) {
            Tools.tellPlayer(player, LangDict.getString(LangDict.CMD_NOT_NOW));
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
            Tools.printMenuOption(player, "/adm", "testrent");
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
        }else if (strings[0].equals("testrent")) {

        }
        return true;
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
