package com.johansvartdal.landlord.playerevents;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerEventManager {

    private static final ArrayList<PlayerEvent> playerEvents = new ArrayList<>();

    public static boolean playerIsInFlyingEvent(Player player) {
        PlayerEvent playerEvent = getEventForPlayer(player);
        return playerEvent instanceof FlyingEvent;
    }

    public static void cancelFlyingEventForPlayer(Player player) {
        PlayerEvent playerEvent = getEventForPlayer(player);
        if (playerEvent instanceof FlyingEvent flyingEvent) {
            flyingEvent.endEvent();
        }
    }

    public static void startPlayerEvent(PlayerEvent event) {
        event.start();
        playerEvents.add(event);
    }

    public static void forceEndPlayerEvent(Player player) {
        for (PlayerEvent event : playerEvents) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
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
        return playerEvents.size() != 0;
    }

    public static PlayerEvent getEventForPlayer(Player player) {
        for (PlayerEvent event : playerEvents) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
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

    public static void forceEndAllEvents() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            forceEndPlayerEvent(player);
        }
    }
}
