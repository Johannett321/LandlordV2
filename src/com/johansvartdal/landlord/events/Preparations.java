package com.johansvartdal.landlord.events;

import com.johansvartdal.landlord.*;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class Preparations extends LandlordEvent {

    private final Main plugin;
    public int prepTime = 8;
    public Preparations(Main plugin) {
        this.plugin = plugin;
    }
    private World mainWorld;

    @Override
    public void startEvent() {
        if (Properties.DEBUG_MODE) {
            countdown(5);
            return;
        }
        God.speak(Main.langDict.getString("welcomeMessage"));
        new BukkitRunnable() {
            public void run() {
                God.speak(Main.langDict.getString("listOfHelpful"));
            }
        }.runTaskLater(plugin, Tools.secToTicks(1));

        new BukkitRunnable() {
            @Override
            public void run() {
                fiveMinutesLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks((prepTime-5)*60));
    }

    public void setMainWorld(World mainWorld) {
        this.mainWorld = mainWorld;
    }

    private void fiveMinutesLeft() {
        God.speak("5 " + Main.langDict.getString("minutesRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                threeMinutesLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(2*60));
    }

    private void threeMinutesLeft() {
        God.speak("3 " + Main.langDict.getString("minutesRemaining"));
        God.speak(Main.langDict.getString("secondListOfHelpful"));

        new BukkitRunnable() {
            @Override
            public void run() {
                twoMinutesLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(60));
    }

    private void twoMinutesLeft() {
        God.speak("2 " + Main.langDict.getString("minutesRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                oneMinuteLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(60));
    }

    private void oneMinuteLeft() {
        God.speak("1 " + Main.langDict.getString("minutesRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                thirtySecondsLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(30));
    }

    private void thirtySecondsLeft() {
        God.speak("30 " + Main.langDict.getString("secondsRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                fifteenSecondsLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(15));
    }

    private void fifteenSecondsLeft() {
        God.speak("15 " + Main.langDict.getString("secondsRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                tenSecondsLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(5));
    }

    private void tenSecondsLeft() {
        God.speak("10 " + Main.langDict.getString("secondsRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                countdown(5);
            }
        }.runTaskLater(plugin, Tools.secToTicks(5));
    }

    private void countdown(int count) {
        if (count <= 0) {
            God.speak(Main.langDict.getString("risingBorders"));
            riseBorders();
            return;
        }

        God.speak(String.valueOf(count));
        new BukkitRunnable() {
            @Override
            public void run() {
                countdown(count-1);
            }
        }.runTaskLater(plugin, Tools.secToTicks(1));
    }

    private void riseBorders() {
        new GameJustStarted(plugin, mainWorld).doStart();
        eventEnded();
    }
}