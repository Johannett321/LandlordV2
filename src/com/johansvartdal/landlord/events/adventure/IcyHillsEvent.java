package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class IcyHillsEvent extends AdventureEvent{

    private final Location advLocation = new Location(Bukkit.getWorld("lladv"), 217, 157 ,-1408);
    private final int excursionMinutes = 5;

    public IcyHillsEvent(Main plugin) {
        super(plugin);
        advLocation.setYaw(150);
        advLocation.setPitch(-9);
    }

    @Override
    public void startExcursion() {
        super.startExcursion();
        advLocation.getWorld().setTime(0);
        advLocation.getWorld().setClearWeatherDuration((int) Tools.secToTicks(excursionMinutes*60));
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
    protected String getWelcomeTitle() {
        return LangDict.getString(LangDict.WELCOME_TITLE) + ChatColor.BLUE +  "Frostgarde";
    }

    @Override
    protected String getWelcomeSubtitle() {
        return null;
    }

    @Override
    protected void showWelcomeMessage() {
        God.speak(LangDict.getString("events.icyHills.welcomeStart") + excursionMinutes + LangDict.getString("events.icyHills.welcomeEnd"));
    }
}
