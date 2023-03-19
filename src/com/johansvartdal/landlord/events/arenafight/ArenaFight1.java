package com.johansvartdal.landlord.events.arenafight;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.json.simple.JSONObject;

public class ArenaFight1 extends ArenaFightEvent {

    public ArenaFight1(Main plugin) {
        super(plugin);
    }

    @Override
    public Wave[] getWaves() {
        int mult = Bukkit.getOnlinePlayers().size();
        Wave wave1 = new Wave(1, 5);
        wave1.addWaveStep(new WaveStep(EntityType.ZOMBIE, 10*mult, Tools.secToTicks(2), new Location[]{Ports.PORT_1}));
        wave1.addWaveStep(new WaveStep(EntityType.SILVERFISH, 6*mult, 10, new Location[]{Ports.PORT_2}));
        wave1.addWaveStep(new WaveStep(EntityType.SKELETON, 3*mult, 5, new Location[]{Ports.PORT_1}));

        Wave wave2 = new Wave(2, 2);
        wave2.addWaveStep(new WaveStep(EntityType.ZOMBIE, 10*mult, 20, new Location[]{Ports.PORT_1, Ports.PORT_2, Ports.PORT_3}));
        wave2.addWaveStep(new WaveStep(EntityType.SILVERFISH, 8*mult, 10, new Location[]{Ports.PORT_1, Ports.PORT_2, Ports.PORT_3}));
        wave2.addWaveStep(new WaveStep(EntityType.SKELETON, 4*mult, 5, new Location[]{Ports.PORT_1}));

        return new Wave[]{wave1, wave2};
    }
}

