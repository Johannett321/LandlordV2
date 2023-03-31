package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public abstract class LandlordEvent implements LandlordEventInterface {

    private class PlayerPrevLoc {
        Player player;
        Location location;
        public PlayerPrevLoc(Player player, Location location) {
            this.player = player;
            this.location = location;
        }
    }

    private final ArrayList<PlayerPrevLoc> previousLocations = new ArrayList<>();

    private OnLandlordEventEndListener onLandlordEventEndListener;
    private BukkitTask exitLocationChecker = null;

    private Integer startX;
    private Integer endX;
    private Integer startY;
    private Integer endY;
    private Integer startZ;
    private Integer endZ;
    protected Boolean eventHasEnded = false;
    public final Main plugin;

    public LandlordEvent(Main plugin) {
        this.plugin = plugin;
    }

    public void saveAllPrevLocs() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerPrevLoc playerPrevLoc = new PlayerPrevLoc(player, player.getLocation());
            previousLocations.add(playerPrevLoc);
        }
    }

    public void setOnEventEndListener(OnLandlordEventEndListener onEventEndListener) {
        this.onLandlordEventEndListener = onEventEndListener;
    }

    public void endEvent(Boolean cancelled) {
        eventHasEnded = true;
        if (exitLocationChecker != null)  {
            exitLocationChecker.cancel();
        }

        // run onEndListener
        if (onLandlordEventEndListener != null) {
            onLandlordEventEndListener.onEnd();
        }

        // update game type
        Main.properties.setGameState(Properties.GameState.NORMAL);

        Tools.deleteFile("runningEventType.txt");
    }

    public void teleportAllPlayersBack() {
        // if no previous locations were saved
        if (previousLocations.size() == 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(Main.playerDataManager.getPlayerData(player).getHomeLocation());
            }
            return;
        }

        // teleport players back
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
            this.endEvent(false);
            return;
        }

        exitLocationChecker = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                scheduleNextExitPositionCheck(plugin);
            }
        }, Tools.secToTicks(3));
    }

    protected void lockPlayersAtLocation(Location location, int radius) {
        if (eventHasEnded) {
            return;
        }

        double locX = location.getX();
        double locZ = location.getZ();

        for (Player player : Bukkit.getOnlinePlayers()) {
            double playerX = player.getLocation().getX();
            double playerZ = player.getLocation().getZ();

            if (playerX > locX - radius && playerX < locX + radius && playerZ > locZ - radius && playerZ < locZ + radius && location.getWorld().equals(player.getWorld())) {
                continue;
            }

            // player is not within radius
            player.teleport(location);
            Tools.tellPlayer(player, LangDict.getString("teleportedBackToEvent"));
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> lockPlayersAtLocation(location, radius), Tools.secToTicks(3));
    }
}
