package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class IcyHillsEvent extends AdventureEvent{

    //TODO update location, yaw and pitch
    private final Location advLocation = new Location(Bukkit.getWorld("lladv"), 217, 157 ,-1408);
    private final int excursionMinutes = 5;

    public IcyHillsEvent(Main plugin) {
        super(plugin);
        advLocation.setYaw(-172);
        advLocation.setPitch(51);
    }

    @Override
    protected Location getEventSpawnLocation() {
        return advLocation;
    }

    @Override
    protected int getExcursionMinutes() {
        return excursionMinutes;
    }

    @Override
    protected void showWelcomeMessage() {
        //TODO change name of place
        God.speak("Greetings and welcome to Frostgarde's mountains! There are some hidden treasures scattered throughout the mountain. " +
                "Find them before your " + excursionMinutes + " minute excursion ends. Perhaps you'll uncover a valuable book of mending?");
    }
}
