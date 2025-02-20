package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class MesaHillsideAdventure extends AdventureEvent{

    public MesaHillsideAdventure(Main plugin) {
        super(plugin);
    }

    @Override
    protected Location getEventSpawnLocation() {
        return null;
    }

    @Override
    protected void showWelcomeMessage() {
        return;
    }

    @Override
    protected int getExcursionMinutes() {
        return -1;
    }

    @Override
    protected String getWelcomeTitle() {
        return null;
    }

    @Override
    protected String getWelcomeSubtitle() {
        return null;
    }

    @Override
    protected Location[] getChestLocation() {
        return null;
    }
}
