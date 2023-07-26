package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class LoungeEvent extends PlayerEvent {

    Location loungeLocation;

    public LoungeEvent(Main plugin, Player player) {
        super(plugin, player);
        loungeLocation = new Location(Bukkit.getWorld("lladv"), 200, 200, 200); // TODO: Get location of lounge
    }

    @Override
    public void start() {
        player.teleport(loungeLocation);

        Tools.tellPlayer(player, LangDict.getString("playerEvents.lounge.welcomeToLounge"));
    }

    @Override
    public Integer getLengthOfEventInSeconds() {
        return null;
    }

    @Override
    public boolean playerTPAwayAllowed() {
        return true;
    }

    @Override
    public void endEvent() {
        super.endEvent();

        player.teleport(locationBeforeEvent);
        Tools.tellPlayer(player, LangDict.getString(LangDict.WELCOME_HOME));
    }

    @Override
    public void onWarningEventShouldCancel() {
        endEvent();
    }

    @Override
    public int getExtensionPrice() {
        return 0;
    }

    @Override
    public String getTitle() {
        return LangDict.getString("playerEvents.lounge.title");
    }
}
