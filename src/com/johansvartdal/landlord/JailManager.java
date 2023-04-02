package com.johansvartdal.landlord;

import com.johansvartdal.landlord.playerevents.JailEvent;
import org.bukkit.entity.Player;

public class JailManager {

    public static void sendToJail(Main plugin, Player player, String reason, int jailSeconds) {
        sendToJail(plugin, player, reason, null, jailSeconds);
    }

    public static void sendToJail(Main plugin, Player player, String reason, String endReason, int jailSeconds) {
        if (PlayerEventManager.playerIsInEvent(player)) {
            // don't send to jail twice
            if (PlayerEventManager.getEventForPlayer(player) instanceof JailEvent) {
                return;
            }
            PlayerEventManager.forceEndPlayerEvent(player);
        }

        JailEvent jailEvent = new JailEvent(plugin, player, reason, jailSeconds);
        if (endReason != null) {
            jailEvent.setEndMessage(endReason);
        }
        PlayerEventManager.startPlayerEvent(jailEvent);
    }
}
