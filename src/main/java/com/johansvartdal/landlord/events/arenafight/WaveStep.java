package com.johansvartdal.landlord.events.arenafight;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

@RequiredArgsConstructor
@Getter
@Setter
public class WaveStep {

    private OnEntitySpawnedEvent onEntitySpawned;

    public final EntityType entityType;
    public final int amount;
    public final long sleepTics;
    public final Location[] ports;
    private Main plugin;
    private Runnable afterAllEntitiesSpawned;

    public void beginSpawning(Main plugin) {
        this.plugin = plugin;
        spawnNextEntity();
    }

    int currentlySpawned = 0;
    private void spawnNextEntity() {
        // actually spawn the entity
        Entity spawnedEntity = Bukkit.getWorld("lladv").spawnEntity(ports[currentlySpawned%ports.length], entityType);
        currentlySpawned++;

        // notify entity spawned
        onEntitySpawned.onEntitySpawned(spawnedEntity);

        // notify done spawning
        if (currentlySpawned == amount) {
            afterAllEntitiesSpawned.run();
            return;
        }

        // spawn more if more should be spawned
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                spawnNextEntity();
            }
        }, sleepTics);
    }

    public void doThisWhenCompleted(Runnable runnable) {
        afterAllEntitiesSpawned = runnable;
    }

    public void setOnEntitySpawned(OnEntitySpawnedEvent onEntitySpawned) {
        this.onEntitySpawned = onEntitySpawned;
    }
}
