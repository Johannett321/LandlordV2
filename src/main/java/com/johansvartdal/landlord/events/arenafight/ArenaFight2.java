package com.johansvartdal.landlord.events.arenafight;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class ArenaFight2 extends ArenaFightEvent {

    public ArenaFight2(Main plugin) {
        super(plugin);
    }

    @Override
    public Wave[] getWaves() {
        int mult = Bukkit.getOnlinePlayers().size();
        Wave wave1 = new Wave(1, 5);
        wave1.addWaveStep(new WaveStep(EntityType.ZOMBIE, 10*mult, Tools.secToTicks(2), new Location[]{Ports.PORT_1}));
        wave1.addWaveStep(new WaveStep(EntityType.SPIDER, 5*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1, Ports.PORT_2}));

        Wave wave2 = new Wave(2, 2);
        wave2.addWaveStep(new WaveStep(EntityType.SPIDER, 12*mult, 5, new Location[]{Ports.PORT_1}));
        wave2.addWaveStep(new WaveStep(EntityType.SKELETON, 6*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1}));
        wave2.addWaveStep(new WaveStep(EntityType.SILVERFISH, 3*mult, 5, new Location[]{Ports.PORT_1}));

        Wave wave3 = new Wave(3, 2);
        wave3.addWaveStep(new WaveStep(EntityType.DROWNED, 10*mult, Tools.secToTicks(2), new Location[]{Ports.PORT_1, Ports.PORT_3}));
        wave3.addWaveStep(new WaveStep(EntityType.SPIDER, 12*mult, 5, new Location[]{Ports.PORT_2}));
        wave3.addWaveStep(new WaveStep(EntityType.PILLAGER, 6*mult, 5, new Location[]{Ports.PORT_1, Ports.PORT_2}));
        wave3.addWaveStep(new WaveStep(EntityType.SILVERFISH, 3*mult, 5, new Location[]{Ports.PORT_4, Ports.PORT_3}));

        return new Wave[]{wave1, wave2, wave3};
    }
}

