package com.johansvartdal.landlord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class AFKDetector {

    @Getter
    @Setter
    @NoArgsConstructor
    private class DetectedPlayerLocation {
        private UUID playerUUID;
        private double x;
        private double y;
        private double z;
        private double yaw;
        private double pitch;
        private Long lastUpdated = 0L;
        private boolean AFK;

        public DetectedPlayerLocation(Player player, Location location) {
            this.playerUUID = player.getUniqueId();
            updateLocation(location);
        }

        public boolean locationIsSimilar(Location location) {
            return axisIsSimilar(x, location.getX()) &&
                    axisIsSimilar(y, location.getY()) &&
                    axisIsSimilar(z, location.getZ()) &&
                    axisIsSimilar(yaw, location.getYaw()) &&
                    axisIsSimilar(pitch, location.getPitch());
        }

        public boolean axisIsSimilar(double prevAx, double newAx) {
            return prevAx - newAx < 1 && newAx - prevAx < 1;
        }

        public void updateLocation(Location location) {
            this.x = location.getX();
            this.y = location.getY();
            this.z = location.getZ();
            this.pitch = location.getPitch();
            this.yaw = location.getYaw();
            lastUpdated = System.currentTimeMillis();
        }
    }

    private final Main plugin;
    private ArrayList<DetectedPlayerLocation> detectedPlayerLocations = new ArrayList<>();

    public AFKDetector(Main plugin) {
        this.plugin = plugin;
        scheduleNewAFKTest();
    }

    private void scheduleNewAFKTest() {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                checkAfks();
                scheduleNewAFKTest();
            }
        }, Tools.secToTicks(30));
    }

    private void checkAfks() {
        // remove logged out players
        if (Properties.DEBUG_LOGGING) System.out.println("AFKChecker detected num of players online: " + detectedPlayerLocations.size());
        detectedPlayerLocations.removeIf(detectedPlayerLocation -> Bukkit.getPlayer(detectedPlayerLocation.playerUUID) == null);
        if (Properties.DEBUG_LOGGING) System.out.println("AFKChecker detected num of players online: " + detectedPlayerLocations.size());

        // loop through existing players and check if AFK
        for (DetectedPlayerLocation detectedPlayerLocation : detectedPlayerLocations) {
            Player player = Bukkit.getPlayer(detectedPlayerLocation.playerUUID);
            // make sure player is still online
            if (player == null || !player.isOnline()) {
                if (Properties.DEBUG_LOGGING) System.out.println("AFKChecker detected that player is no longer online: " + detectedPlayerLocation.playerUUID);
                return;
            }

            // update location if it's not the same
            if (!detectedPlayerLocation.locationIsSimilar(player.getLocation())) {
                detectedPlayerLocation.updateLocation(player.getLocation());
                if (Properties.DEBUG_LOGGING) System.out.println("AFKChecker detected that player has moved: " + detectedPlayerLocation.playerUUID);

                // was player AFK when updating position? If so, update status to home
                if (detectedPlayerLocation.isAFK()) {
                    if (Properties.DEBUG_LOGGING) System.out.println("AFKChecker detected that A PLAYER WAS AFK, BUT NOW MOVED: " + detectedPlayerLocation.playerUUID);
                    PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.home"));
                    detectedPlayerLocation.setAFK(false);
                }
            }

            // if location has been similar for three minutes, set AFK
            if (System.currentTimeMillis() - detectedPlayerLocation.getLastUpdated() > 1000*60*3) {
                if (Properties.DEBUG_LOGGING) System.out.println("AFKChecker detected that player is AFK: " + detectedPlayerLocation.playerUUID);
                PlayerDataManager.updatePlayerStatus(player, "AFK");
                detectedPlayerLocation.setAFK(true);
            }
        }

        // loop through all players on server and see if we should add more players to the list
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean found = false;
            for (DetectedPlayerLocation detectedPlayerLocation : detectedPlayerLocations) {
                if (detectedPlayerLocation.getPlayerUUID().equals(player.getUniqueId())) {
                    found = true;
                    break;
                }else {
                    if (Properties.DEBUG_LOGGING) System.out.println("AFKChecker detected player: " + player.getUniqueId() + ", which did not match: " + detectedPlayerLocation.playerUUID);
                }
            }

            if (!found) {
                if (Properties.DEBUG_LOGGING) System.out.println("AFKChecker detected that a player has joined!: " + player.getUniqueId());
                DetectedPlayerLocation detectedPlayerLocation = new DetectedPlayerLocation(player, player.getLocation());
                detectedPlayerLocations.add(detectedPlayerLocation);
            }
        }
    }
}
