package com.johansvartdal.landlord.events;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.lan.AudioLayer;
import com.johansvartdal.landlord.lan.LanController;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Preparations extends LandlordEvent {

    private final Main plugin;
    public int prepTime = 8;
    private World mainWorld;

    public Preparations(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public void startEvent() {
        if (Properties.DEBUG_MODE) {
            countdown(5);
            return;
        }

        // play welcome audio
        LanController.getLanMusicController().playAudioFile("countdown.wav", AudioLayer.BACKGROUND);

        God.speak("Preparations will start in 5 seconds. Get ready!");
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                startPrep();
            }
        }, Tools.secToTicks(5));
    }

    public void startPrep() {
        //teleport everyone to world
        teleportEveryoneToWorld();

        //change the weather and make sure the time is day
        changeWeatherGood();

        God.speak(LangDict.getString("welcomeMessage"));
        new BukkitRunnable() {
            public void run() {
                God.speak(LangDict.getString("listOfHelpful"));
            }
        }.runTaskLater(plugin, Tools.secToTicks(1));

        new BukkitRunnable() {
            @Override
            public void run() {
                fiveMinutesLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks((prepTime-5)*60));
    }

    private void changeWeatherGood() {
        World world = Bukkit.getWorld("world");
        world.setTime(0);
        world.setStorm(false);
    }

    private void teleportEveryoneToWorld() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(Main.tradeCenter.getLocation());
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    public void setMainWorld(World mainWorld) {
        this.mainWorld = mainWorld;
    }

    private void fiveMinutesLeft() {
        God.speak("5 " + LangDict.getString("minutesRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                threeMinutesLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(2*60));
    }

    private void threeMinutesLeft() {
        God.speak("3 " + LangDict.getString("minutesRemaining"));
        God.speak(LangDict.getString("secondListOfHelpful"));

        new BukkitRunnable() {
            @Override
            public void run() {
                twoMinutesLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(60));
    }

    private void twoMinutesLeft() {
        God.speak("2 " + LangDict.getString("minutesRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                oneMinuteLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(60));
    }

    private void oneMinuteLeft() {
        God.speak("1 " + LangDict.getString("minutesRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                thirtySecondsLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(30));
    }

    private void thirtySecondsLeft() {
        God.speak("30 " + LangDict.getString("secondsRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                fifteenSecondsLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(15));
    }

    private void fifteenSecondsLeft() {
        God.speak("15 " + LangDict.getString("secondsRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                tenSecondsLeft();
            }
        }.runTaskLater(plugin, Tools.secToTicks(5));
    }

    private void tenSecondsLeft() {
        God.speak("10 " + LangDict.getString("secondsRemaining"));
        new BukkitRunnable() {
            @Override
            public void run() {
                countdown(5);
            }
        }.runTaskLater(plugin, Tools.secToTicks(5));
    }

    private void countdown(int count) {
        if (count <= 0) {
            God.speak(LangDict.getString("risingBorders"));
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