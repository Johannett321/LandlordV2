package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        God.speak(LangDict.getString("solvheimWelcomeStart") + excursionMinutes + LangDict.getString("solvheimWelcomeEnd"));
    }

    @Override
    protected int getExcursionMinutes() {
        return excursionMinutes;
    }

    @Override
    protected String getWelcomeTitle() {
        return LangDict.getString(LangDict.WELCOME_TITLE) + ChatColor.BLUE +  "Solvheim " + LangDict.getString("village");
    }

    @Override
    protected String getWelcomeSubtitle() {
        return null;
    }
}
