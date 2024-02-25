package com.johansvartdal.landlord;

import com.johansvartdal.landlord.events.Preparations;
import com.johansvartdal.landlord.events.TestEvent;
import com.johansvartdal.landlord.events.adventure.ValleyVillageAdventure;
import com.johansvartdal.landlord.events.arenafight.ArenaFight1;
import com.johansvartdal.landlord.events.mystery.Mystery1;
import com.johansvartdal.landlord.events.taxevents.ChooseTreasuryEvent;
import com.johansvartdal.landlord.events.taxevents.HasteEvent;
import com.johansvartdal.landlord.levels.Level;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;

@Slf4j
public class LandlordEventManager {

    @Getter
    private static LandlordEvent currentEvent;

    public static void loadEventIfAny(Main plugin) {
        if (Main.properties.getGameState() != Properties.GameState.EVENT_RUNNING) {
            return;
        }

        String eventType = Tools.read("runningEventType.txt");
        if (Properties.DEBUG_LOGGING) System.out.println("Event that should be resumed: " + eventType);
        if (eventType == null) {
            return;
        }

        LandlordEvent event = switch (eventType) {
            case "ArenaFight" -> new ArenaFight1(plugin);
            case "Preparations" -> new Preparations(plugin);
            case "TestEvent" -> new TestEvent(plugin);
            case "Adventure" -> new ValleyVillageAdventure(plugin);
            case "Mystery" -> new Mystery1(plugin);
            case "TaxEvent" -> new HasteEvent(plugin);
            case "VoteForTreasury" -> new ChooseTreasuryEvent(plugin);
            default -> null;
        };

        if (event == null) {
            log.error("An event was running, but could not be resumed: " + eventType);
            return;
        }
        resumeEvent(event);
    }

    public static void startEvent(LandlordEvent event) {
        configEvent(event);

        // save eventType
        Tools.write("runningEventType.txt", event.getEventType());

        // cancel all player events
        PlayerEventManager.forceEndAllEvents();

        // start event
        event.startEvent();
    }

    public static void resumeEvent(LandlordEvent event) {
        configEvent(event);

        // start event
        event.resumeEvent();
    }

    private static void configEvent(LandlordEvent event) {
        currentEvent = event;

        // on event end
        event.setOnEventEndListener(() -> {
            currentEvent = null;
        });

        // update game state
        Main.properties.setGameState(Properties.GameState.EVENT_RUNNING);
    }

    public Boolean eventRunning() {
        return currentEvent != null;
    }

    public static void notifyLevelReached(Level newLevel) {
        LandlordEvent newLevelEvent = newLevel.getEventToStartBeforeLevel();
        if (newLevelEvent != null) {
            startEvent(newLevelEvent);
        }
    }
}
