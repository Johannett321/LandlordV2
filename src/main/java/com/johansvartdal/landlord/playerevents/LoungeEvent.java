package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class LoungeEvent extends PlayerEvent {

    Location loungeLocation;
    Location jukeboxLocation;
    Jukebox jukebox;

    public LoungeEvent(Main plugin, Player player) {
        super(plugin, player);
        // define jukebox
        jukeboxLocation = new Location(Bukkit.getWorld("lladv"), 101, 69, -875);
        jukebox = (Jukebox) jukeboxLocation.getBlock().getState();

        // define spawn location
        loungeLocation = new Location(Bukkit.getWorld("lladv"), 107, 68, -871);
        loungeLocation.setYaw(63);
        loungeLocation.setPitch(0);
    }

    @Override
    public void start() {
        player.teleport(loungeLocation);

        // update status & tell player
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.inLounge"));
        Tools.tellPlayer(player, LangDict.getString("playerEvents.lounge.welcomeToLounge"));

        // play lounge music
        playLoungeMusic();
    }

    private void playLoungeMusic() {
        if (!jukebox.isPlaying()) {
            ItemStack record = new ItemStack(Material.MUSIC_DISC_STAL);
            jukebox.setRecord(record);
            jukebox.update();
        }
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

        // update status & tell player
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.home"));
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
