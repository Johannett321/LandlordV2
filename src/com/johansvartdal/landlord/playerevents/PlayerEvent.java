package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public abstract class PlayerEvent {

    protected Player player;
    protected Location locationBeforeEvent;
    protected Main plugin;
    protected BukkitTask autoEndEvent;
    protected long scheduledEndTime;

    public PlayerEvent(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.locationBeforeEvent = player.getLocation();
    }

    public abstract void start();
    public void endEvent() {
        if (autoEndEvent != null) {
            autoEndEvent.cancel();
        }
    }
    public abstract int getLengthSecondsBeforeWarn();
    public abstract boolean playerTPAwayAllowed();
    public abstract void onEndCalled();

    public Player getPlayer() {
        return player;
    }

    public void extend() {
        autoEndEvent.cancel();
        scheduleAutoEnd();
    }

    public void scheduleAutoEnd() {
        long current = System.currentTimeMillis();
        scheduledEndTime = current + (1000L *getLengthSecondsBeforeWarn());
        autoEndEvent = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                onEndCalled();
            }
        }, Tools.secToTicks(getLengthSecondsBeforeWarn()));
    }

    public String getTextTimeLeft() {
        long current = System.currentTimeMillis();
        long timeLeftSeconds = (scheduledEndTime-current)/1000;
        if (timeLeftSeconds > 60) {
            return timeLeftSeconds/60 + " minute(s)";
        }else {
            return timeLeftSeconds + " second(s)";
        }
    }
}
