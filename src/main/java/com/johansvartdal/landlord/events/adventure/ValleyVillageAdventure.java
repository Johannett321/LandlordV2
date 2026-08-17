package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class ValleyVillageAdventure extends AdventureEvent{

    private final Location advLocation = new Location(Bukkit.getWorld("lladv"), 528, 68 ,-893);
    private final int excursionMinutes = 10;

    public ValleyVillageAdventure(Main plugin) {
        super(plugin);
        advLocation.setYaw(-121);
        advLocation.setPitch(4);
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
        God.speak(LangDict.getString("events.solvheim.welcomeStart") + getExcursionMinutes() + LangDict.getString("events.solvheim.welcomeEnd"));
    }

    @Override
    protected int getExcursionMinutes() {
        return excursionMinutes;
    }

    @Override
    protected String getWelcomeTitle() {
        return LangDict.getString(LangDict.WELCOME_TITLE) + ChatColor.BLUE +  "Solvheim " + LangDict.getString("generalSentenceParts.village");
    }

    @Override
    protected String getWelcomeSubtitle() {
        return null;
    }

    @Override
    protected Location[] getChestLocation() {
        World world = getEventSpawnLocation().getWorld();
        return new Location[]{
                new Location(world, 522, 67, -894),
                new Location(world, 530, 68, -901),
                new Location(world, 554, 71, -901),
                new Location(world, 564, 71, -904),
                new Location(world, 568, 71, -906),
                new Location(world, 544, 63, -906),
                new Location(world, 544, 63, -907),
                new Location(world, 516, 67, -907),
                new Location(world, 501, 54, -924),
                new Location(world, 500, 54, -924),
                new Location(world, 528, 68, -945),
                new Location(world, 568, 69, -933),
                new Location(world, 610, 120, -925),
                new Location(world, 473, 80, -931),
                new Location(world, 490, 80, -924)
        };
    }
}
