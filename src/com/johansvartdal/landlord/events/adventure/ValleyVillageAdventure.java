package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ValleyVillageAdventure extends AdventureEvent{

    private final Location advLocation = new Location(Bukkit.getWorld("world"), 528, 68 ,-893);
    private final int excursionMinutes = 10;

    public ValleyVillageAdventure(Main plugin) {
        super(plugin);
        advLocation.setYaw(-121);
        advLocation.setPitch(4);
    }

    @Override
    protected Location getEventSpawnLocation() {
        return advLocation;
    }

    @Override
    protected void showWelcomeMessage() {
        God.speak("Greetings and welcome to Solvheim village! There are some hidden treasures scattered throughout the village. " +
                "Find them before your " + excursionMinutes + " minute excursion ends. Perhaps you'll uncover a valuable book of mending?");
    }

    @Override
    protected int getExcursionMinutes() {
        return excursionMinutes;
    }
}
