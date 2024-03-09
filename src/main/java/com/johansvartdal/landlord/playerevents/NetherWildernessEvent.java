package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Random;

public class NetherWildernessEvent extends PlayerEvent{

    public NetherWildernessEvent(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void start() {
        Location location = getPossibleSpawnLocation();
        player.teleport(location);

        // update status
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.inNether"));

        scheduleAutoEnd();
    }

    private Location getPossibleSpawnLocation() {
        Random random = new Random();
        int xz = random.nextInt(100000);

        Location location0 = new Location(Bukkit.getWorld("world_nether"), xz, 80, xz);
        Location location1 = new Location(Bukkit.getWorld("world_nether"), xz, 81, xz);
        Location location2 = new Location(Bukkit.getWorld("world_nether"), xz, 82, xz);
        Location location3 = new Location(Bukkit.getWorld("world_nether"), xz, 83, xz);

        // regen if in lava
        if (location0.getBlock().getType() == Material.LAVA ||
                location1.getBlock().getType() == Material.LAVA ||
                location2.getBlock().getType() == Material.LAVA ||
                location3.getBlock().getType() == Material.LAVA) {
            return getPossibleSpawnLocation();
        }

        location0.getBlock().setType(Material.OBSIDIAN);
        location1.getBlock().setType(Material.AIR);
        location2.getBlock().setType(Material.AIR);
        location3.getBlock().setType(Material.OBSIDIAN);

        return location1;
    }

    @Override
    public void endEvent() {
        super.endEvent();
        player.teleport(locationBeforeEvent);

        // update status
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.home"));
    }

    @Override
    public Integer getLengthOfEventInSeconds() {
        return 60*7;
    }

    @Override
    public boolean playerTPAwayAllowed() {
        return true;
    }

    @Override
    public void onWarningEventShouldCancel() {
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.wildEndingIn") + 10 + LangDict.getString("generalSentenceParts.seconds"), ChatColor.YELLOW);

        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                Tools.tellPlayer(player, LangDict.getString(LangDict.WELCOME_HOME));
                endEvent();
            }
        }, Tools.secToTicks(10));
    }

    @Override
    public int getExtensionPrice() {
        return LevelManager.getNetherWildernessPrice();
    }

    @Override
    public String getTitle() {
        return LangDict.getString("playerEvents.wilderness.wildernessNether");
    }
}
