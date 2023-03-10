package com.johansvartdal.landlord.playerevents;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.StaticValues;
import com.johansvartdal.landlord.Tools;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
        Tools.tellPlayer(player, "You have been sent to jail as " + jailReason, ChatColor.RED);

        // teleport to actual jail location
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
        if (endMessage != null) {
            Tools.tellPlayer(player, endMessage, ChatColor.GREEN);
        }
        player.teleport(locationBeforeEvent);
        endEvent();
    }

    @Override
    public int getExtensionPrice() {
        return 0;
    }

    @Override
    public String getTitle() {
        return "jail";
    }

    public void setEndMessage(String endMessage) {
        this.endMessage = endMessage;
    }
}
