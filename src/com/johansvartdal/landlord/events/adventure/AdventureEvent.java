package com.johansvartdal.landlord.events.adventure;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.ChatColor;

public abstract class AdventureEvent extends LandlordEvent {

    public AdventureEvent(Main plugin) {
        super(plugin);
    }

    @Override
    public String getEventType() {
        return "Adventure";
    }

    @Override
    public void startEvent() {
        God.speak("Get ready, an excursion will begin in 5 minutes!");

        scheduleExcursionStart();
    }

    private void scheduleExcursionStart() {
        Tools.performTaskAfterCountdown(() -> {
            
        },20);
    }

    @Override
    public void resumeEvent() {
        Tools.broadcastMessage("The event was cancelled due to a server restart", ChatColor.RED);
        endEvent(true);
    }
}
