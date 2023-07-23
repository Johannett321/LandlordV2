package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        Tools.tellPlayer(player, LangDict.getString("youHaveBeenSentToJailBecause") + jailReason + LangDict.getString("sentenceTime") + (jailSeconds/60) + LangDict.getString("minutes"), ChatColor.RED);

        // TODO: teleport to actual jail location
        player.teleport(StaticValues.GAME_START_LOCATION);

        scheduleAutoEnd();
    }

    @Override
    public int getLengthOfEventInSeconds() {
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
        return LangDict.getString("jail");
    }

    public void setEndMessage(String endMessage) {
        this.endMessage = endMessage;
    }
}
