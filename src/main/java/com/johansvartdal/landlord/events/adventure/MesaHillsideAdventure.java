package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class MesaHillsideAdventure extends AdventureEvent{

    private final Location advLocation = new Location(Bukkit.getWorld("lladv"), 10260, 79, -37);
    private final int excursionMinutes = 8;

    public MesaHillsideAdventure(Main plugin) {
        super(plugin);
        advLocation.setYaw(-59);
        advLocation.setPitch(0);
    }

    @Override
    public void startExcursion() {
        super.startExcursion();
        advLocation.getWorld().setTime(0);
        advLocation.getWorld().setStorm(false);
        advLocation.getWorld().setThundering(false);
        advLocation.getWorld().setClearWeatherDuration((int) Tools.secToTicks(excursionMinutes*60));
    }

    @Override
    protected Location getEventSpawnLocation() {
        return advLocation;
    }

    @Override
    protected void showWelcomeMessage() {
        God.speak(LangDict.getString("events.VaelTorrah.welcomeStart") + excursionMinutes + LangDict.getString("events.VaelTorrah.welcomeEnd"));
    }

    @Override
    protected int getExcursionMinutes() {
        return excursionMinutes;
    }

    @Override
    protected String getWelcomeTitle() {
        return LangDict.getString(LangDict.WELCOME_TITLE) + ChatColor.BLUE +  "Vael'Torrah";
    }

    @Override
    protected String getWelcomeSubtitle() {
        return null;
    }

    @Override
    protected Location[] getChestLocation() {
        World world = getEventSpawnLocation().getWorld();
        return new Location[]{
                new Location(world, 10338, 70, 27),
                new Location(world, 10338, 70, 32),
                new Location(world, 10340, 70, 32),
                new Location(world, 10339, 71, 44),
                new Location(world, 10339, 71, 57),
                new Location(world, 10340, 74, 25),
                new Location(world, 10341, 74, 39),
                new Location(world, 10342, 74, 41),
                new Location(world, 10343, 77, 27),
                new Location(world, 10350, 78, 41),
                new Location(world, 10349, 78, 47),
                new Location(world, 10349, 78, 52),
                new Location(world, 10340, 78, 53),
                new Location(world, 10340, 79, 43),
                new Location(world, 10341, 78, 42),
                new Location(world, 10344, 81, 17),
                new Location(world, 10344, 81, 21),
                new Location(world, 10340, 82, 50),
                new Location(world, 10347, 82, 16),
                new Location(world, 10346, 85, 18),
                new Location(world, 10345, 84, 24),
                new Location(world, 10349, 88, 21),
                new Location(world, 10346, 90, 28),
                new Location(world, 10343, 90, 35),
                new Location(world, 10343, 88, 52),
                new Location(world, 10343, 88, 53),
                new Location(world, 10336, 90, 61),
                new Location(world, 10336, 86, 61),
                new Location(world, 10337, 78, 64),
                new Location(world, 10341, 78, 62),
                new Location(world, 10336, 74, 61)
        };
    }
}
