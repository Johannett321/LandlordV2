package com.johansvartdal.landlord.events;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TestEvent extends LandlordEvent {

    private Main plugin;

    public TestEvent(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startEvent() {
        Tools.broadcastMessage("Eventet starter straks!");

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                // save all previous locations
                saveAllPrevLocs();

                // teleport all players
                teleportAllPlayers();

                // check if player's has succeeded every X seconds
                startListeningForExitOnCords(plugin,-260, -256,112,115, -99, -96);
            }
        }, Tools.secToTicks(10));
    }

    public void teleportAllPlayers() {
        // XYZ: -275 / 113 / -91

        // teleport all players
        Location startLoc = new Location(Bukkit.getWorld("lladv"), -275, 113, -91);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(startLoc);
        }
    }

    @Override
    public void eventEnded() {
        super.eventEnded();
        teleportAllPlayersBack();
        God.speak("Amazing! You did it!");
    }
}
