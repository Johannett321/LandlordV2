package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

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
        advLocation.getWorld().setTime(13000);
        advLocation.getWorld().setStorm(true);
        advLocation.getWorld().setThundering(false);
        advLocation.getWorld().setWeatherDuration((int) Tools.secToTicks(excursionMinutes*60));
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
    protected Location[] getChestLocation() {
        World world = getEventSpawnLocation().getWorld();
        return new Location[]{
                new Location(world, 215, 157, -1424),
                new Location(world, 214, 157, -1424),
                new Location(world, 211, 161, -1427),
                new Location(world, 197, 156, -1448),
                new Location(world, 194, 159, -1444),
                new Location(world, 190, 141, -1466),
                new Location(world, 183, 147, -1490),
                new Location(world, 153, 143, -1498),
                new Location(world, 154, 147, -1496),
                new Location(world, 138, 160, -1522),
                new Location(world, 130, 159, -1517),
                new Location(world, 136, 163, -1528),
                new Location(world, 133, 166, -1518),
                new Location(world, 136, 177, -1518),
                new Location(world, 133, 177, -1518)
        };
    }

    @Override
    protected void showWelcomeMessage() {
        God.speak(LangDict.getString("events.icyHills.welcomeStart") + excursionMinutes + LangDict.getString("events.icyHills.welcomeEnd"));
    }
}
