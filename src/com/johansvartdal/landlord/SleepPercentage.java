package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SleepPercentage {

    private final Main plugin;

    public SleepPercentage(Main plugin) {
        this.plugin = plugin;
        scheduleNewSleepTest();
    }

    private void scheduleNewSleepTest() {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                measureSleepingPeople();
                scheduleNewSleepTest();
            }
        }, Tools.secToTicks(5));
    }

    private void measureSleepingPeople() {
        World world = Bukkit.getWorld("world");
        final long time = world.getTime();

        if (!(time >= 12950 && time <= 23050)) {
            return;
        }

        int sleepersNeeded = Bukkit.getOnlinePlayers().size();
        if (sleepersNeeded > 1) {
            sleepersNeeded = sleepersNeeded/2;
        }
        int playersSleeping = 0;
        for(Player p : Bukkit.getOnlinePlayers()){
            if (p.isSleeping()) {
                playersSleeping = playersSleeping + 1;
            }
        }

        if (playersSleeping >= sleepersNeeded) {
            world.setTime(0);
            world.setStorm(false);
        }else if (playersSleeping > 0) {
            Tools.broadcastMessage(playersSleeping + "/" + sleepersNeeded + " player(s) are sleeping");
        }
    }
}
