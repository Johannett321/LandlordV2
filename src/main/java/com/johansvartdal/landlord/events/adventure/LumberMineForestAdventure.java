package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class LumberMineForestAdventure extends AdventureEvent {

    private final Location advLocation = new Location(Bukkit.getWorld("lladv"), -1106, 70, -2745);

    public LumberMineForestAdventure(Main plugin) {
        super(plugin);
        advLocation.setYaw(-180);
        advLocation.setPitch(0);
    }

    @Override
    public void startExcursion() {
        super.startExcursion();
        advLocation.getWorld().setTime(0);
        advLocation.getWorld().setStorm(true);
        advLocation.getWorld().setThundering(false);
        advLocation.getWorld().setWeatherDuration((int) Tools.secToTicks(getExcursionMinutes()*60));
    }

    @Override
    protected Location getEventSpawnLocation() {
        return advLocation;
    }

    @Override
    protected void showWelcomeMessage() {
        God.speak(LangDict.getString("events.tyrvangr.welcomeStart") + getExcursionMinutes() + LangDict.getString("events.tyrvangr.welcomeEnd"));
    }

    @Override
    protected int getExcursionMinutes() {
        return 7;
    }

    @Override
    protected String getWelcomeTitle() {
        return LangDict.getString(LangDict.WELCOME_TITLE) + ChatColor.BLUE +  "Týrvangr";
    }

    @Override
    protected String getWelcomeSubtitle() {
        return null;
    }

    @Override
    protected Location[] getChestLocation() {
        World world = getEventSpawnLocation().getWorld();
        return new Location[]{
                new Location(world, -1090, 79, -2848),
                new Location(world, -1148, 63, -2891),
                new Location(world, -1144, 65, -2902),
                new Location(world, -1143, 64, -2886),
                new Location(world, -1114, 76, -2889),
                new Location(world, -1094, 67, -2883),
                new Location(world, -1094, 67, -2879),
                new Location(world, -1073, 72, -2873),
                new Location(world, -1143, 49, -2883),
                new Location(world, -1117, 71, -2855),
                new Location(world, -1113, 69, -2841),
                new Location(world, -1106, 70, -2826),
                new Location(world, -1130, 71, -2834),
                new Location(world, -1116, 76, -2860),
                new Location(world, -1113, 73, -2842),
                new Location(world, -1111, 70, -2861)
        };
    }
}
