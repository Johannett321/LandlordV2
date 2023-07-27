package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LoungeEvent extends PlayerEvent {

    Location loungeLocation;
    Location jukeboxLocation;
    Block jukeboxBlock;

    public LoungeEvent(Main plugin, Player player) {
        super(plugin, player);
        // define jukebox
        jukeboxLocation = new Location(Bukkit.getWorld("lladv"), 101, 69, -875);
        jukeboxBlock = jukeboxLocation.getBlock();

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

        // give player food and drink
        ItemStack snacks = new ItemStack(Material.COOKIE);
        snacks.setAmount(3);
        ItemStack drinks = new ItemStack(Material.POTION);
        drinks.setAmount(2);
        Tools.givePlayerItemOrDrop(player, snacks, true);
        Tools.givePlayerItemOrDrop(player, drinks, true);
    }

    private void playLoungeMusic() {
        BlockState blockState = jukeboxBlock.getState();
        if (!(blockState instanceof Jukebox)) {
            createJukeboxAndPlaySong(Material.MUSIC_DISC_STAL);
            return;
        }

        Jukebox jukebox = (Jukebox) jukeboxBlock.getState();
        if (!jukebox.isPlaying()) {
            createJukeboxAndPlaySong(Material.MUSIC_DISC_STAL);
        }
    }

    private void resetJukebox() {
        jukeboxBlock.setType(Material.AIR);
    }

    private void createJukeboxAndPlaySong(Material disc) {
        // Place a new Jukebox
        jukeboxBlock.setType(Material.JUKEBOX);

        // Insert the record
        Jukebox jukebox = (Jukebox) jukeboxBlock.getState();
        ItemStack record = new ItemStack(disc);
        jukebox.setRecord(record);
        jukebox.update();
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

        resetJukebox();

        player.teleport(locationBeforeEvent);

        // update status & tell player
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.home"));
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
