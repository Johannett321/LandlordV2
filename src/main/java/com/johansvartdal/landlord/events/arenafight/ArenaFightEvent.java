package com.johansvartdal.landlord.events.arenafight;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

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
        updateAllPlayerStatuses();

        // start waves
        runWaveLoop();
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

        //TODO: Sett respawn location til players igjen til HOME location. Playersa har jo sovet i ArenaFighten.

        Tools.broadcastMessage(LangDict.getString(LangDict.EVENT_CANCELLED_SERVER_RESTART), ChatColor.RED);
        endEvent(true);
    }
}
