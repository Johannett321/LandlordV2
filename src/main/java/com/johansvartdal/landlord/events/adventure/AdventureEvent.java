package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.events.LandlordEvent;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Slf4j
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
        startExcursion();
        prepareChests();
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

    private void prepareChests() {
        for (Location location : getChestLocation()) {
            System.out.println("Filling adventure chest at location: " + location.getX() + ", " + location.getY() + ", " + location.getZ());
            Main.chestManager.fillAdventureChest(location);
        }
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
    protected abstract Location[] getChestLocation();

    @Override
    public void prepareEvent() {
        super.prepareEvent();
        God.speak(LangDict.getString("info.excursionAnnouncementPrefix") + Tools.getTextTimeSeconds(getPreparationTimeSeconds()));
    }

    @Override
    protected String getPreparationCountdownMessagePrefix() {
        return LangDict.getString("info.excursionCountdownPrefix");
    }

    @Override
    protected int getPreparationTimeSeconds() {
        if (Properties.DEV_CHEAT_MODE) {
            return 30;
        }
        return 60*5;
    }
}
