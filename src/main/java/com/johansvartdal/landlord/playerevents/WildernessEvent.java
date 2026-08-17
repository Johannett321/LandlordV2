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
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.wildEndingIn") + 1 + LangDict.getString("generalSentenceParts.minutes"), ChatColor.YELLOW);
        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin, this::thirtySecLeft, Tools.secToTicks(30));
    }

    public void thirtySecLeft() {
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.wildEndingIn") + 30 + LangDict.getString("generalSentenceParts.seconds"), ChatColor.YELLOW);
        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin, this::tenSecLeft, Tools.secToTicks(20));
    }

    public void tenSecLeft() {
        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.wildEndingIn") + 10 + LangDict.getString("generalSentenceParts.seconds"), ChatColor.YELLOW);
        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin, () -> countdownTillEnd(5), Tools.secToTicks(5));
    }

    public void countdownTillEnd(int num) {
        if (num <= 0) {
            endEvent();
            return;
        }

        Tools.tellPlayer(player, LangDict.getString("playerEvents.wilderness.wildEndingIn") + num + LangDict.getString("generalSentenceParts.seconds"), ChatColor.YELLOW);
        eventTimerWithAction = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            countdownTillEnd(num-1);
        }, Tools.secToTicks(1));
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
