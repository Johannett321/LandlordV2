package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class SibirianCottageWinterAdventure extends AdventureEvent {

    private final Location advLocation = new Location(Bukkit.getWorld("lladv"), -2399, 77, 176);

    public SibirianCottageWinterAdventure(Main plugin) {
        super(plugin);
        advLocation.setYaw(90);
        advLocation.setPitch(0);
    }

    @Override
    public void startExcursion() {
        super.startExcursion();
        advLocation.getWorld().setTime(13000);
        advLocation.getWorld().setStorm(false);
        advLocation.getWorld().setThundering(false);
        advLocation.getWorld().setClearWeatherDuration((int) Tools.secToTicks(getExcursionMinutes()*60));
    }

    @Override
    protected Location getEventSpawnLocation() {
        return advLocation;
    }

    @Override
    protected void showWelcomeMessage() {
        God.speak(LangDict.getString("events.MorozvetskiyBereg.welcomeStart") + getExcursionMinutes() + LangDict.getString("events.MorozvetskiyBereg.welcomeEnd"));
    }

    @Override
    protected int getExcursionMinutes() {
        return 8;
    }

    @Override
    protected String getWelcomeTitle() {
        return LangDict.getString(LangDict.WELCOME_TITLE) + ChatColor.BLUE +  "Morozvetskiy Bereg";
    }

    @Override
    protected String getWelcomeSubtitle() {
        return null;
    }

    @Override
    protected Location[] getChestLocation() {
        World world = getEventSpawnLocation().getWorld();
        return new Location[]{
                new Location(world, -2433, 63, 123),
                new Location(world, -2431, 63, 119),
                new Location(world, -2432, 64, 114),
                new Location(world, -2396, 75, 182),
                new Location(world, -2395, 75, 183),
                new Location(world, -2394, 77, 183),
                new Location(world, -2315, 103, 109),
                new Location(world, -2316, 106, 99),
                new Location(world, -2326, 105, 116),
                new Location(world, -2322, 106, 104),
                new Location(world, -2318, 105, 102),
                new Location(world, -2306, 96, 109),
                new Location(world, -2309, 114, 104),
                new Location(world, -2324, 113, 113),
                new Location(world, -2308, 103, 104),
                new Location(world, -2310, 79, 202),
                new Location(world, -2224, 75, 159),
                new Location(world, -2251, 78, 71),
                new Location(world, -2296, 88, 80)
        };
    }
}
