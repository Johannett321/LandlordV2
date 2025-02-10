package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.PlayerEventManager;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public abstract class PlayerEvent {

    protected Player player;
    protected Location locationBeforeEvent;
    protected Main plugin;
    protected BukkitTask eventTimerWithAction = null;
    protected long scheduledEndTime;

    public PlayerEvent(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.locationBeforeEvent = player.getLocation();
    }

    public abstract void start();
    public void endEvent() {
        if (eventTimerWithAction != null) {
            eventTimerWithAction.cancel();
        }

        PlayerEventManager.notifyEventEnd(this);
    }
    public abstract Integer getLengthOfEventInSeconds();
    public abstract boolean playerTPAwayAllowed();
    public abstract void onWarningEventShouldCancel();

    public Player getPlayer() {
        return player;
    }

    public void extend() {
        eventTimerWithAction.cancel();
        scheduleAutoEnd();
    }

    public void scheduleAutoEnd() {
        long current = System.currentTimeMillis();
        scheduledEndTime = current + (1000L * getLengthOfEventInSeconds());

        if (eventTimerWithAction != null) {
            eventTimerWithAction.cancel();
        }

        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin,
                this::onWarningEventShouldCancel,
                Tools.secToTicks(getLengthOfEventInSeconds()));
    }

    public String getTextTimeLeft() {
        long current = System.currentTimeMillis();

        if (scheduledEndTime <= current) {
            return LangDict.getString("events.preparations.about0Sec");
        }

        long timeLeftSeconds = (scheduledEndTime-current)/1000;
        timeLeftSeconds = timeLeftSeconds + 60; // adding 60 seconds, as there are 60 seconds warning before ending the event
        return Tools.getTextTimeSeconds((int) timeLeftSeconds);
    }

    public abstract int getExtensionPrice();

    public abstract String getTitle();
}
