package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class SibirianCottageWinterAdventure extends AdventureEvent {

    private final Location advLocation = new Location(Bukkit.getWorld("lladv"), -2399, 77, 176);

    public SibirianCottageWinterAdventure(Main plugin) {
        super(plugin);
        advLocation.setYaw(90);
        advLocation.setPitch(0);
    }

    @Override
    protected Location getEventSpawnLocation() {
        return advLocation;
    }

    @Override
    protected void showWelcomeMessage() {

    }

    @Override
    protected int getExcursionMinutes() {
        return 60;
    }

    @Override
    protected String getWelcomeTitle() {
        return LangDict.getString(LangDict.WELCOME_TITLE) + ChatColor.BLUE +  "Muromtsevo Coast";
    }

    @Override
    protected String getWelcomeSubtitle() {
        return null;
    }
}
