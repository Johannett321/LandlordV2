package com.johansvartdal.landlord;

import com.johansvartdal.landlord.events.Preparations;
import com.johansvartdal.landlord.events.TestEvent;
import com.johansvartdal.landlord.events.adventure.ValleyVillageAdventure;
import com.johansvartdal.landlord.events.arenafight.ArenaFight1;
import com.johansvartdal.landlord.levels.Level;
import lombok.Getter;

public class LandlordEventManager {

    @Getter
    private static LandlordEvent currentEvent;

    public static void loadEventIfAny(Main plugin) {
        if (Main.properties.getGameState() != Properties.GameState.EVENT_RUNNING) {
            return;
        }

        String eventType = Tools.read("runningEventType.txt");
        System.out.println("Event: " + eventType);
        if (eventType == null) {
            return;
        }

        LandlordEvent event = null;

        switch (eventType) {
            case "ArenaFight":
                event = new ArenaFight1(plugin);
                break;
            case "Preparations":
                event = new Preparations(plugin);
                break;
            case "TestEvent":
                event = new TestEvent(plugin);
                break;
            case "Adventure":
                event = new ValleyVillageAdventure(plugin);
                break;
        }

        resumeEvent(event);
    }

    public static void startEvent(LandlordEvent event) {
        configEvent(event);

        // save eventType
        Tools.write("runningEventType.txt", event.getEventType());

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
        LandlordEvent newLevelEvent = newLevel.getLevelStartEvent();
        if (newLevelEvent != null) {
            startEvent(newLevelEvent);
        }
    }
}
