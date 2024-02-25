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

        // get the location
        Location miningStartLoc = getRandomLocationWithoutLava();

        // update status & tell player
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.mining"));

        // teleport player there
        player.teleport(miningStartLoc);

        // height checker
        scheduleHeightChecker();
    }

    @Override
    public Integer getLengthOfEventInSeconds() {
        if (Properties.DEV_CHEAT_MODE) {
            return 15; // 15 seconds
        }else {
            return 60*45; // 45 minutes
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

        // update status & tell player
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.home"));
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
        return LangDict.getString("playerEvents.mining.title");
    }

    public void warnOneMinLeft() {
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.wildEnding1Min"), ChatColor.YELLOW);
        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                warnTenSecLeft();
            }
        }, Tools.secToTicks(60));
    }

    public void warnTenSecLeft() {
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.wildEnding10Sec"), ChatColor.YELLOW);
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

        Location location = new Location(Bukkit.getWorld("world"), randomNum+5, -53, randomNum+5);
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
                    if (y == -54 || y == -51) {
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
        heightChecker = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.getLocation().getY() > 16) {
                strikes += 1;

                if (strikes > 2) {
                    Tools.tellPlayer(player, LangDict.getString("playerEvents.mining.heightLimitTooMuch"), ChatColor.RED);
                    endEvent();
                    return;
                }else {
                    Tools.tellPlayer(player, LangDict.getString("playerEvents.mining.mineGetDown") + strikes + "/3)", ChatColor.RED);
                }
            }

            // reschedule
            scheduleHeightChecker();
        }, Tools.secToTicks(10));
    }
}
