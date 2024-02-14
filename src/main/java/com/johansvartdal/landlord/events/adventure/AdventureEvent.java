package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.*;
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
        God.speak(LangDict.getString("events.excursion.getReadyExcursion"));
        scheduleExcursionStart();
    }

    private void scheduleExcursionStart() {
        int startEventInSeconds = 60*5;
        if (Properties.DEV_CHEAT_MODE) {
            startEventInSeconds = 35;
        }
        Tools.performTaskAfterCountdown(this::startExcursion, LangDict.getString("events.excursion.excursionIn"), startEventInSeconds);
    }

    @Override
    public void endEvent(Boolean cancelled) {
        super.endEvent(cancelled);
        teleportAllPlayersBack();
    }

    public void startExcursion() {
        saveAllPrevLocs();
        teleportAllPlayersToEvent();
        updateAllPlayerStatuses();
        showWelcomeMessage();
        showTitle();
        scheduleEndEvent(getExcursionMinutes());
    }

    private void updateAllPlayerStatuses() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.excursion"));
        }
    }

    private void showTitle() {
        Tools.broadcastTitle(getWelcomeTitle(), getWelcomeSubtitle());
    }

    protected void scheduleEndEvent(int inMinutes) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Tools.performTaskAfterCountdown(() -> {
                God.speak(LangDict.getString("events.excursion.excursionEnd"));
                endEvent(false);
            }, LangDict.getString("events.excursion.excursionEndIn"), 60);
        }, Tools.secToTicks(60*inMinutes-60));
    }

    @Override
    public void resumeEvent() {
        Tools.broadcastMessage(LangDict.getString(LangDict.EVENT_CANCELLED_SERVER_RESTART), ChatColor.RED);
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
    protected abstract String getWelcomeTitle();
    protected abstract String getWelcomeSubtitle();
}
