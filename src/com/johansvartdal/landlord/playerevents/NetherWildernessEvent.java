package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Random;

public class NetherWildernessEvent extends PlayerEvent{

    public NetherWildernessEvent(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void start() {
        Location location = getPossibleSpawnLocation();
        player.teleport(location);

        scheduleAutoEnd();
    }

    private Location getPossibleSpawnLocation() {
        double x = 0.5;
        double z = 0.5;

        Location location0 = new Location(Bukkit.getWorld("world_nether"), x, 80, z);
        Location location1 = new Location(Bukkit.getWorld("world_nether"), x, 81, z);
        Location location2 = new Location(Bukkit.getWorld("world_nether"), x, 82, z);
        Location location3 = new Location(Bukkit.getWorld("world_nether"), x, 83, z);

        // regen if in lava
        if (location0.getBlock().getType() == Material.LAVA ||
                location1.getBlock().getType() == Material.LAVA ||
                location2.getBlock().getType() == Material.LAVA ||
                location3.getBlock().getType() == Material.LAVA) {
            return getPossibleSpawnLocation();
        }

        location0.getBlock().setType(Material.OBSIDIAN);
        location1.getBlock().setType(Material.AIR);
        location2.getBlock().setType(Material.AIR);
        location3.getBlock().setType(Material.OBSIDIAN);

        return location1;
    }

    @Override
    public void endEvent() {
        super.endEvent();
        player.teleport(locationBeforeEvent);
    }

    @Override
    public int getLengthSecondsBeforeWarn() {
        return 20;
    }

    @Override
    public boolean playerTPAwayAllowed() {
        return true;
    }

    @Override
    public void onEndCalled() {
        Tools.tellPlayer(player, "Nether wilderness ending in 10 seconds", ChatColor.YELLOW);

        autoEndEvent = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                Tools.tellPlayer(player, "Welcome back home!");
                endEvent();
            }
        }, Tools.secToTicks(10));
    }
}
