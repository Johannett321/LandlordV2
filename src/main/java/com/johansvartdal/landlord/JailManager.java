package com.johansvartdal.landlord;

import com.johansvartdal.landlord.playerevents.JailEvent;
import com.johansvartdal.landlord.playerevents.PlayerEventManager;
import org.bukkit.entity.Player;

import static com.johansvartdal.landlord.Tools.debugLog;

public class JailManager {

    public static void sendToJail(Main plugin, Player player, String reason, int jailSeconds) {
        sendToJail(plugin, player, reason, null, jailSeconds);
    }

    public static void sendToJail(Main plugin, Player player, String reason, String endReason, int jailSeconds) {
        if (!Main.properties.gameStateIsNormal()) {
            debugLog("A player was not sent to jail due to a global event currently happening.");
            return;
        }
        if (PlayerEventManager.playerIsInEvent(player)) {
            // don't send to jail twice
            if (PlayerEventManager.getEventForPlayer(player) instanceof JailEvent) {
                debugLog("Player was already in jail, and was therefore not sent to jail again. Username: " + player.getDisplayName());
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

    public static boolean playerIsInJail(Player player) {
        return PlayerEventManager.getEventForPlayer(player) instanceof JailEvent;
    }
}
