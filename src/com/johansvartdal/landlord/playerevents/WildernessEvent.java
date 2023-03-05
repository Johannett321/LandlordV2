package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WildernessEvent extends PlayerEvent{

    public WildernessEvent(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void start() {
        Location location = Main.levelManager.getWildernessLocation();
        player.teleport(location);

        scheduleAutoEnd();
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
    public void scheduleEnd() {
        Tools.tellPlayer(player, "Wilderness ending in 10 seconds", ChatColor.YELLOW);

        autoEndEvent = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                Tools.tellPlayer(player, "Welcome back home!");
                endEvent();
            }
        }, Tools.secToTicks(10));
    }
}
