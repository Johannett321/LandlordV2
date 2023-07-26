package com.johansvartdal.landlord.events.mystery;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Mystery1 extends MysteryEvent {

    private final Location spawnLocation = new Location(Bukkit.getWorld("lladv"), -283.5, 58, -160.5);

    public Mystery1(Main plugin) {
        super(plugin);
         spawnLocation.setYaw(-90);
         spawnLocation.setPitch(0);
    }

    @Override
    protected Location getEventSpawnLocation() {
        return spawnLocation;
    }

    @Override
    protected LocationArea getExitLocationArea() {
        return new LocationArea(-271,-269,11,14,-158,-156);
    }

    @Override
    protected void showWelcomeMessage() {
        God.speak(LangDict.getString("events.cakeWorld.welcomeStart"));
    }
}
