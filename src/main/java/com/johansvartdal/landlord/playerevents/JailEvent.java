package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static com.johansvartdal.landlord.Tools.debugLog;

public class JailEvent extends PlayerEvent {

    private String endMessage = null;
    private String jailReason;
    private int jailSeconds;

    public JailEvent(Main plugin, Player player, String jailReason, int jailSeconds) {
        super(plugin, player);
        this.jailReason = jailReason;
        this.jailSeconds = jailSeconds;
    }

    @Override
    public void start() {
        Tools.tellPlayer(player, LangDict.getString("playerEvents.jail.youHaveBeenSentToJailBecause") + jailReason + LangDict.getString("playerEvents.jail.sentenceTime") + (jailSeconds/60) + LangDict.getString("generalSentenceParts.minutes"), ChatColor.RED);
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.inJail"));

        Location jailLocation = new Location(Bukkit.getWorld("lladv"), 203.5, 74, -146.5);
        jailLocation.setPitch(11);
        jailLocation.setYaw(133);
        player.teleport(jailLocation);

        scheduleAutoEnd();
    }

    @Override
    public Integer getLengthOfEventInSeconds() {
        return jailSeconds;
    }

    @Override
    public boolean playerTPAwayAllowed() {
        return false;
    }

    @Override
    public void onWarningEventShouldCancel() {
        // make sure player is still online
        Player player1 = Bukkit.getPlayer(player.getUniqueId());
        if (player1 == null || !player1.isOnline()) {
            debugLog("The jail event ended without the player being online");
            endEvent();
            return;
        }

        // update status & tell player
        PlayerDataManager.updatePlayerStatus(player, LangDict.getString("playerStatus.home"));
        if (endMessage != null) {
            Tools.tellPlayer(player1, endMessage, ChatColor.GREEN);
        }

        player1.teleport(locationBeforeEvent);
        endEvent();
    }

    @Override
    public int getExtensionPrice() {
        return 0;
    }

    @Override
    public String getTitle() {
        return LangDict.getString("playerEvents.jail.jailTitle");
    }

    public void setEndMessage(String endMessage) {
        this.endMessage = endMessage;
    }
}
