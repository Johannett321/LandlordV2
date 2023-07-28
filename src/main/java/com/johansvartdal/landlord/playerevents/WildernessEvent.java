package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WildernessEvent extends PlayerEvent{

    public WildernessEvent(Main plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void start() {
        Location location = LevelManager.getWildernessLocation();
        player.teleport(location);

        // update status
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.inWild"));

        scheduleAutoEnd();
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
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.wildEnding10Sec"), ChatColor.YELLOW);

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
        return LevelManager.getWildernessPrice();
    }

    @Override
    public String getTitle() {
        return LangDict.getString("playerEvents.wilderness.title");
    }
}
