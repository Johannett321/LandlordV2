package com.johansvartdal.landlord.events.arenafight;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.events.LandlordEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class ArenaFightEvent extends LandlordEvent {

    private final World lladvWorld;
    private Location arenaSpawnLoc;
    private Location fireworkLocation;
    ArrayList<Wave> remainingWaves = new ArrayList<>(Arrays.asList(getWaves()));

    // TODO: Flere ArenaFight maps

    public ArenaFightEvent(Main plugin) {
        super(plugin);
        lladvWorld = Bukkit.getWorld("lladv");

        // set spawn location
        arenaSpawnLoc = new Location(lladvWorld, 268.5, 64, -167.5);
        arenaSpawnLoc.setYaw(132);
        arenaSpawnLoc.setPitch(30);

        // set firework location
        fireworkLocation = new Location(lladvWorld, 257, 73, -142);
    }

    @Override
    public void startEvent() {
        // keep night
        keepNight();

        // teleport all players to event
        saveAllPrevLocs();
        teleportAllPlayersToEventLocation();
        storePlayersPreviousSpawnLocations();
        updateSpawnLocationOfPlayers(arenaSpawnLoc);
        updateAllPlayerStatuses();

        // start waves
        runWaveLoop();
    }

    HashMap<UUID, Location> previousSpawnLocations = new HashMap<>();

    private void storePlayersPreviousSpawnLocations() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            previousSpawnLocations.put(player.getUniqueId(), player.getRespawnLocation());
        }
    }

    private void updateAllPlayerStatuses() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.arena"));
        }
    }

    @Override
    public void endEvent(Boolean cancelled) {
        // teleport all players back
        teleportAllPlayersBack();

        super.endEvent(cancelled);
    }

    private void teleportAllPlayersToEventLocation() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(arenaSpawnLoc);
        }

        super.lockPlayersAtLocation(arenaSpawnLoc, 200);
    }

    private void updateSpawnLocationOfPlayers(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setRespawnLocation(location, true);
        }
    }

    private void resetPlayerRespawnLocationsBackToPrevious() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location previousSpawnLocation = previousSpawnLocations.get(player.getUniqueId());
            player.setRespawnLocation(previousSpawnLocation);
        }
    }

    private void keepNight() {
        if (eventHasEnded) {
            return;
        }
        lladvWorld.setTime(14000);
        lladvWorld.setStorm(false);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                keepNight();
            }
        }, Tools.secToTicks(10));
    }

    private void runWaveLoop() {
        // determine if all waves completed
        if (remainingWaves.size() == 0) {
            allWavesCompleted();
            return;
        }

        // inform players
        if (remainingWaves.size() < getWaves().length) {
            God.speak(LangDict.getString("events.arenaFight.nextWave30Sec"));
        }else {
            God.speak(LangDict.getString("events.arenaFight.firstWave30Sec"));
        }

        // add loop repeater
        remainingWaves.get(0).doThisAfterEvent(() -> {
            remainingWaves.remove(remainingWaves.get(0));
            runWaveLoop();
        });

        // start wave
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            remainingWaves.get(0).startWave(plugin);
        }, Tools.secToTicks(30));
    }

    private void allWavesCompleted() {
        // celebrate players
        SpecialEffects.blastFireworks(fireworkLocation, 5);
        God.speak(LangDict.getString("events.arenaFight.wellDoneArena"));

        // end event
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            endEvent(false);
        }, Tools.secToTicks(15));
    }

    public abstract Wave[] getWaves();

    @Override
    public String getEventType() {
        return "ArenaFight";
    }

    @Override
    public void resumeEvent() {
        // kill all mobs
        Tools.killAllMobsInWorld(Bukkit.getWorld("lladv"));

        resetPlayerRespawnLocationsBackToPrevious();

        Tools.broadcastMessage(LangDict.getString(LangDict.EVENT_CANCELLED_SERVER_RESTART), ChatColor.RED);
        endEvent(true);
    }

    @Override
    public void prepareEvent() {
        super.prepareEvent();
        God.speak(LangDict.getString("info.arenaFightAnnouncementPrefix") + getPreparationTimeSeconds() + Tools.getTextTimeSeconds(getPreparationTimeSeconds()));
    }

    @Override
    protected String getPreparationCountdownMessagePrefix() {
        return LangDict.getString("info.arenaFightCountdownPrefix");
    }

    @Override
    protected int getPreparationTimeSeconds() {
        return 60*5;
    }
}
