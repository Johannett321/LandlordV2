package com.johansvartdal.landlord;

import com.johansvartdal.landlord.playerevents.PlayerEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerEventManager {

    private static final ArrayList<PlayerEvent> playerEvents = new ArrayList<>();

    public static void startPlayerEvent(PlayerEvent event) {
        event.start();
        playerEvents.add(event);
    }

    public static void forceEndPlayerEvent(Player player) {
        for (PlayerEvent event : playerEvents) {
            if (event.getPlayer().getUniqueId() == player.getUniqueId()) {
                forceEndPlayerEvent(event);
                return;
            }
        }
    }

    public static void forceEndPlayerEvent(PlayerEvent event) {
        event.endEvent();
        playerEvents.remove(event);
    }

    public static boolean anyPlayersInEvent() {
        return playerEvents.size() == 0;
    }

    public static PlayerEvent getEventForPlayer(Player player) {
        for (PlayerEvent event : playerEvents) {
            if (event.getPlayer().getUniqueId() == player.getUniqueId()) {
                return event;
            }
        }
        return null;
    }

    public static boolean playerIsInEvent(Player player) {
        return getEventForPlayer(player) != null;
    }

    public static void notifyEventEnd(PlayerEvent playerEvent) {
        playerEvents.remove(playerEvent);
    }
}
