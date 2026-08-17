package com.johansvartdal.landlord.events.mystery;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.events.LandlordEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class MysteryEvent extends LandlordEvent {

    @AllArgsConstructor
    @Getter
    protected class LocationArea {
        private int startX;
        private int endX;
        private int startY;
        private int endY;
        private int startZ;
        private int endZ;
    }

    public MysteryEvent(Main plugin) {
        super(plugin);
    }

    @Override
    public String getEventType() {
        return "Mystery";
    }

    @Override
    public void resumeEvent() {
        Tools.broadcastMessage(LangDict.getString(LangDict.EVENT_CANCELLED_SERVER_RESTART), ChatColor.RED);
        endEvent(true);
    }

    @Override
    public void startEvent() {
        teleportAllPlayersToEvent();
        updatePlayerStatuses();
        showWelcomeMessage();
        setExitLocationListener(getExitLocationArea());
    }

    private void updatePlayerStatuses() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Main.playerDataManager.playerExists(player)) {
                continue; // skip spectators
            }
            PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.mystery"));
        }
    }

    private void setExitLocationListener(LocationArea locationArea) {
        super.startListeningForExitOnCords(plugin,
                locationArea.getStartX(),
                locationArea.getEndX(),
                locationArea.getStartY(),
                locationArea.getEndY(),
                locationArea.getStartZ(),
                locationArea.getEndZ());
    }

    private void teleportAllPlayersToEvent() {
        Location spawnLocation = getEventSpawnLocation();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(spawnLocation);
        }

        lockPlayersAtLocation(spawnLocation, 300);
    }

    protected abstract Location getEventSpawnLocation();
    protected abstract LocationArea getExitLocationArea();
    protected abstract void showWelcomeMessage();
}
