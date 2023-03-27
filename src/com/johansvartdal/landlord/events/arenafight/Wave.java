package com.johansvartdal.landlord.events.arenafight;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Properties;
import com.johansvartdal.landlord.Tools;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Wave {

    private Main plugin;
    private final int waveDisplayNumber;

    @Getter
    private final ArrayList<WaveStep> waveSteps;

    private OnWaveEndListener afterWave;

    private final int sleepSecsBetweenSteps;

    private final ArrayList<Entity> spawnedEntities;


    public Wave(int waveDisplayNumber, int sleepSecsBetweenSteps) {
        waveSteps = new ArrayList<>();
        spawnedEntities = new ArrayList<>();

        this.waveDisplayNumber = waveDisplayNumber;
        this.sleepSecsBetweenSteps = sleepSecsBetweenSteps;
    }

    public void addWaveStep(WaveStep waveStep) {
        waveSteps.add(waveStep);
    }

    public void doThisAfterEvent(OnWaveEndListener waveEndListener) {
        afterWave = waveEndListener;
    }

    public void startWave(Main plugin) {
        this.plugin = plugin;

        // blindness
        if (Properties.DEBUG_MODE) {
            giveBlindnessToEveryone();
        }

        Tools.broadcastMessage("Wave " + (waveDisplayNumber) + LangDict.getString("hasBegun"), ChatColor.RED);
        loopWaveSteps();
    }

    private void giveBlindnessToEveryone() {
        for (Player player: Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*5, 1));
        }
    }

    private void loopWaveSteps() {
        if (waveSteps.size() == 0) {
            loopEntityChecker();
            return;
        }

        waveSteps.get(0).doThisWhenCompleted(() -> {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    waveSteps.remove(waveSteps.get(0));
                    loopWaveSteps();
                }
            }, Tools.secToTicks(sleepSecsBetweenSteps));
        });

        waveSteps.get(0).setOnEntitySpawned(spawnedEntities::add);
        waveSteps.get(0).beginSpawning(plugin);
    }

    private void loopEntityChecker() {
        if (allEntitiesKilled()) {
            afterWave.onWaveEnd();
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                loopEntityChecker();
            }
        }, Tools.secToTicks(3));
    }

    int lastCheckLivingEntities = 0;
    long lastChangeInLivingEntities = 0;

    private Boolean allEntitiesKilled () {
        int currentLivingEntities = 0;
        for (Entity entity : spawnedEntities) {
            if (!entity.isDead()) {
                currentLivingEntities++; // found living entity
            }
        }

        if (currentLivingEntities == 0) {
            lastCheckLivingEntities = currentLivingEntities;
            lastChangeInLivingEntities = 0;
            return true;
        }

        if (System.currentTimeMillis() > lastChangeInLivingEntities + 1000 * 15) {
            // make sure we have progress
            if (lastCheckLivingEntities == currentLivingEntities) {
                lastChangeInLivingEntities = 0;
                Tools.killAllMobsInWorld(Bukkit.getWorld("lladv"));
                return true;
            }else {
                lastCheckLivingEntities = currentLivingEntities;
                lastChangeInLivingEntities = System.currentTimeMillis();
            }
        }

        // all entities are dead
        return false;
    }
}
