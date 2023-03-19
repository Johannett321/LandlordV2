package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public abstract class LandlordEvent implements LandlordEventInterface{

    private class PlayerPrevLoc {
        Player player;
        Location location;
        public PlayerPrevLoc(Player player, Location location) {
            this.player = player;
            this.location = location;
        }
    }

    private ArrayList<PlayerPrevLoc> previousLocations = new ArrayList<>();

    private OnLandlordEventEndListener onLandlordEventEndListener;
    private BukkitTask exitLocationChecker = null;

    private Integer startX;
    private Integer endX;
    private Integer startY;
    private Integer endY;
    private Integer startZ;
    private Integer endZ;

    public void saveAllPrevLocs() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerPrevLoc playerPrevLoc = new PlayerPrevLoc(player, player.getLocation());
            previousLocations.add(playerPrevLoc);
        }
    }

    public void setOnEventEndListener(OnLandlordEventEndListener onEventEndListener) {
        this.onLandlordEventEndListener = onEventEndListener;
    }

    public void setOnPrepareEndListener(OnLandlordEventEndListener onEventEndListener) {
        this.onLandlordEventEndListener = onEventEndListener;
    }

    public void eventEnded() {
        if (exitLocationChecker != null)  {
            exitLocationChecker.cancel();
        }
        onLandlordEventEndListener.onEnd();
        Main.properties.setGameState(Properties.GameState.NORMAL);
    }

    public void teleportAllPlayersBack() {
        for (PlayerPrevLoc playerPrevLoc : previousLocations) {
            playerPrevLoc.player.teleport(playerPrevLoc.location);
        }
    }

    protected void startListeningForExitOnCords(Main plugin, int startX, int endX, int startY, int endY, int startZ, int endZ) {
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.startZ = startZ;
        this.endZ = endZ;
        scheduleNextExitPositionCheck(plugin);
    }

    private void scheduleNextExitPositionCheck(Main plugin) {

        Boolean allPlayersWithin = true;
        for (Player player : Bukkit.getOnlinePlayers())  {
            Location pLoc = player.getLocation();
            System.out.println("Checking cords for " + player.getDisplayName() + ": " + pLoc.getX() + " / " + pLoc.getY() + " / " + pLoc.getZ());
            if (!(pLoc.getX() >= startX && pLoc.getX() <= endX && pLoc.getY() >= startY && pLoc.getY() <= endY && pLoc.getZ() >= startZ && pLoc.getZ() <= endZ)) {
                allPlayersWithin = false;
                break;
            }
        }

        if (allPlayersWithin) {
            this.eventEnded();
            return;
        }

        exitLocationChecker = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                scheduleNextExitPositionCheck(plugin);
            }
        }, Tools.secToTicks(3));
    }
}
