package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class AdventureEvent extends LandlordEvent {

    public AdventureEvent(Main plugin) {
        super(plugin);
    }

    @Override
    public String getEventType() {
        return "Adventure";
    }

    @Override
    public void startEvent() {
        God.speak("Get ready, an excursion will begin in 5 minutes!");
        scheduleExcursionStart();
    }

    private void scheduleExcursionStart() {
        Tools.performTaskAfterCountdown(this::startExcursion, "The excursion starts in",60*5);
    }

    @Override
    public void endEvent(Boolean cancelled) {
        super.endEvent(cancelled);
        teleportAllPlayersBack();
    }

    public void startExcursion() {
        saveAllPrevLocs();
        teleportAllPlayersToEvent();
        showWelcomeMessage();
        scheduleEndEvent(getExcursionMinutes());
    }

    protected void scheduleEndEvent(int inMinutes) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Tools.performTaskAfterCountdown(() -> {
                God.speak("Unfortunately, your excursion has come to an end. Welcome back home");
                endEvent(false);
            }, "Excursion ending in", 60);
        }, Tools.secToTicks(60*inMinutes-60));
    }

    @Override
    public void resumeEvent() {
        Tools.broadcastMessage("The event was cancelled due to a server restart", ChatColor.RED);
        endEvent(true);
    }

    protected void teleportAllPlayersToEvent() {
        // teleport players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(getEventSpawnLocation());
        }

        // lock them there
        lockPlayersAtLocation(getEventSpawnLocation(), 300);
    }

    protected abstract Location getEventSpawnLocation();
    protected abstract void showWelcomeMessage();
    protected abstract int getExcursionMinutes();
}
