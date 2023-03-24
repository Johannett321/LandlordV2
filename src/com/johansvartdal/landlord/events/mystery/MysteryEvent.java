package com.johansvartdal.landlord.events.mystery;

import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
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
        Tools.broadcastMessage("The event was cancelled due to a server restart", ChatColor.RED);
        endEvent(true);
    }

    @Override
    public void startEvent() {
        teleportAllPlayersToEvent();
        showWelcomeMessage();
        setExitLocationListener(getExitLocationArea());
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
