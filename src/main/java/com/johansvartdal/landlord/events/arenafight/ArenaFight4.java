package com.johansvartdal.landlord.events.arenafight;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class ArenaFight4 extends ArenaFightEvent {

    public ArenaFight4(Main plugin) {
        super(plugin);
    }

    @Override
    public Wave[] getWaves() {
        int mult = Bukkit.getOnlinePlayers().size();
        Wave wave1 = new Wave(1, 5);
        wave1.addWaveStep(new WaveStep(EntityType.SILVERFISH, 7*mult, 2, new Location[]{Ports.PORT_1}));
        wave1.addWaveStep(new WaveStep(EntityType.ZOMBIE, 7*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1}));
        wave1.addWaveStep(new WaveStep(EntityType.SKELETON, 6*mult, 10, new Location[]{Ports.PORT_2}));
        wave1.addWaveStep(new WaveStep(EntityType.SPIDER, 5*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1}));

        Wave wave2 = new Wave(1, 5);
        wave2.addWaveStep(new WaveStep(EntityType.ENDERMITE, 10*mult, 2, new Location[]{Ports.PORT_1}));
        wave2.addWaveStep(new WaveStep(EntityType.WITCH, 4*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_2}));
        wave2.addWaveStep(new WaveStep(EntityType.SPIDER, 20*mult, 10, new Location[]{Ports.PORT_1}));

        Wave wave3 = new Wave(2, 2);
        wave3.addWaveStep(new WaveStep(EntityType.HUSK, 7*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1}));
        wave3.addWaveStep(new WaveStep(EntityType.PILLAGER, 6*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_2}));
        wave3.addWaveStep(new WaveStep(EntityType.BLAZE, 2*mult, Tools.secToTicks(3), new Location[]{Ports.PORT_3}));
        wave3.addWaveStep(new WaveStep(EntityType.CAVE_SPIDER, 12*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1, Ports.PORT_3}));

        Wave wave4 = new Wave(3, 2);
        wave4.addWaveStep(new WaveStep(EntityType.ENDERMITE, 4*mult, 2, new Location[]{Ports.PORT_1, Ports.PORT_2, Ports.PORT_3, Ports.PORT_4}));
        wave4.addWaveStep(new WaveStep(EntityType.SLIME, 7*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1}));
        wave4.addWaveStep(new WaveStep(EntityType.BLAZE, 4*mult, Tools.secToTicks(2), new Location[]{Ports.PORT_2, Ports.PORT_3}));
        wave4.addWaveStep(new WaveStep(EntityType.WITHER_SKELETON, mult, Tools.secToTicks(1), new Location[]{Ports.PORT_4}));
        wave4.addWaveStep(new WaveStep(EntityType.EVOKER, 6*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_2}));

        Wave wave5 = new Wave(3, 2);
        wave5.addWaveStep(new WaveStep(EntityType.ENDERMITE, 10*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1, Ports.PORT_2, Ports.PORT_3, Ports.PORT_4}));
        wave5.addWaveStep(new WaveStep(EntityType.BLAZE, 4*mult, Tools.secToTicks(2), new Location[]{Ports.PORT_2, Ports.PORT_3}));
        wave5.addWaveStep(new WaveStep(EntityType.WITHER_SKELETON, 3*mult, Tools.secToTicks(2), new Location[]{Ports.PORT_4}));
        wave5.addWaveStep(new WaveStep(EntityType.ZOMBIE, 7*mult, 10, new Location[]{Ports.PORT_1}));
        wave5.addWaveStep(new WaveStep(EntityType.SKELETON, 4*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_2}));
        wave5.addWaveStep(new WaveStep(EntityType.CAVE_SPIDER, 12*mult, Tools.secToTicks(3), new Location[]{Ports.PORT_1, Ports.PORT_3}));
        wave5.addWaveStep(new WaveStep(EntityType.SILVERFISH, 12*mult, Tools.secToTicks(1), new Location[]{Ports.PORT_1, Ports.PORT_3}));


        // arena fight 4, wither

        return new Wave[]{wave1, wave2, wave3, wave4, wave5};
    }
}

