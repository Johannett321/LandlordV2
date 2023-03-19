package com.johansvartdal.landlord.events.arenafight;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class ArenaFightEvent extends LandlordEvent {

    protected final Main plugin;
    private final World lladvWorld;
    private Location arenaSpawnLoc;
    private Location fireworkLocation;
    ArrayList<Wave> remainingWaves = new ArrayList<>(Arrays.asList(getWaves()));

    // TODO: legge til en checker som sjekker om når sist remaingMobs ble oppdatert. Hvis det er lenge siden, skal alle mobs killes, noe som fører til at man går videre til neste wave.
    // TODO: Flere ArenaFight maps
    // TODO: Hvis en EventRunning og man er utenfor eventForceLockLocation + 1000 blokker radius, skal man bli teleport til eventForceLockLocation (dette gjør også at nye players kommer inn i eventet igjen også)
    // TODO: Dersom serveren restartes, må eventet begynne på nytt, og alle mobs fra forrige "try" drepes.


    public ArenaFightEvent(Main plugin) {
        this.plugin = plugin;
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

        // start waves
        runWaveLoop();
    }

    @Override
    public void eventEnded() {
        // teleport all players back
        teleportAllPlayersBack();

        super.eventEnded();
    }

    private void teleportAllPlayersToEventLocation() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(arenaSpawnLoc);
        }
    }

    private void keepNight() {
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
            God.speak("Good job! Keep it going! Next wave starting in 30 seconds");
        }else {
            God.speak("The first wave is starting in 30 seconds! Please sleep in a bed before the battle begins");
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
        God.speak("Well done! You have completed the ArenaFight!");

        // end event
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            eventEnded();
        }, Tools.secToTicks(15));
    }

    public abstract Wave[] getWaves();

}
