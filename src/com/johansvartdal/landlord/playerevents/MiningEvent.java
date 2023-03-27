package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class MiningEvent extends PlayerEvent {

    BukkitTask heightChecker = null;

    public MiningEvent(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void start() {
        scheduleAutoEnd();
        Location miningStartLoc = getRandomLocationWithoutLava();
        player.teleport(miningStartLoc);

        scheduleHeightChecker();
    }

    @Override
    public int getLengthOfEventInSeconds() {
        if (Properties.DEBUG_MODE) {
            return 15;
        }else {
            return 60*45;
        }
    }

    @Override
    public boolean playerTPAwayAllowed() {
        return true;
    }

    @Override
    public void endEvent() {
        super.endEvent();
        if (heightChecker != null) {
            heightChecker.cancel();
        }

        player.teleport(locationBeforeEvent);
        Tools.tellPlayer(player, LangDict.getString(LangDict.WELCOME_HOME));
    }

    @Override
    public void onWarningEventShouldCancel() {
        warnOneMinLeft();
    }

    @Override
    public int getExtensionPrice() {
        return StaticValues.MINING_PRICE;
    }

    @Override
    public String getTitle() {
        return "mining";
    }

    public void warnOneMinLeft() {
        Tools.tellPlayer(player, LangDict.getString("wildEnding1Min"), ChatColor.YELLOW);
        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                warnTenSecLeft();
            }
        }, Tools.secToTicks(60));
    }

    public void warnTenSecLeft() {
        Tools.tellPlayer(player, LangDict.getString("wildEnding10Sec"), ChatColor.YELLOW);
        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                endEvent();
            }
        }, Tools.secToTicks(10));
    }

    private Location getRandomLocationWithoutLava() {
        Random random = new Random();
        int randomNum = random.nextInt(100000);
        randomNum += 100000;

        Location location = new Location(Bukkit.getWorld("world"), randomNum+5, 12, randomNum+5);
        for (int x = randomNum; x < randomNum+11; x++) {
            for (int y = 11; y < 15; y++) {
                for (int z = randomNum; z < randomNum+11; z++) {
                    Location clearBlock = new Location(location.getWorld(), x, y, z);

                    // make sure walls are not lava
                    if (x == randomNum || x == randomNum+10 || z == randomNum || z == randomNum+10) {
                        clearBlock.getBlock().setType(Material.STONE);
                        continue;
                    }

                    // make sure ground is not lava
                    if (y == 11 || y == 14) {
                        clearBlock.getBlock().setType(Material.STONE);
                    }else {
                        clearBlock.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        location.getBlock().setType(Material.TORCH);
        return location;
    }

    int strikes = 0;

    private void scheduleHeightChecker() {
        heightChecker = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (player.getLocation().getY() > 16) {
                    strikes += 1;

                    if (strikes > 2) {
                        Tools.tellPlayer(player, LangDict.getString("heightLimitTooMuch"), ChatColor.RED);
                        endEvent();
                        return;
                    }else {
                        Tools.tellPlayer(player, LangDict.getString("mineGetDown") + strikes + "/3)", ChatColor.RED);
                    }
                }

                // reschedule
                scheduleHeightChecker();
            }
        }, Tools.secToTicks(10));
    }
}
