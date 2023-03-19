package com.johansvartdal.landlord;

import com.johansvartdal.landlord.levels.Level;
import lombok.Getter;

public class LandlordEventManager {

    @Getter
    private static LandlordEvent currentEvent;

    public static void startEvent(LandlordEvent event) {
        currentEvent = event;

        // on event end
        event.setOnEventEndListener(() -> {
            currentEvent = null;
        });

        // start event
        event.startEvent();
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
