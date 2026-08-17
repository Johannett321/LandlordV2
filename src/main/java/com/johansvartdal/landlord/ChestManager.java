package com.johansvartdal.landlord;

import com.johansvartdal.landlord.mysterychest.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.Random;

public class ChestManager {

    private final Main plugin;
    private final Random random = new Random();

    public ChestManager(Main plugin) {
        this.plugin = plugin;
    }

    public void fillAdventureChest(Location location) {
        AdventureChest adventureChest = new AdventureChest(location);
        adventureChest.fillChest();
    }

    public void orderMysteryChestInTrade() {
        announceChestOrdered();

        AutomaticFillableChest automaticFillableChest = getRandomMysteryChest();

        Bukkit.getScheduler().runTaskLater(plugin, () -> announceChestTier(automaticFillableChest), Tools.secToTicks(5));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            automaticFillableChest.spawnChest();
            automaticFillableChest.fillChest();

            announceChestSpawned();
        }, Tools.secToTicks(15));
    }

    private void announceChestOrdered() {
        God.speak(LangDict.getString("treasury.treasuryOrderedMysteryChest"));
    }


    private void announceChestTier(AutomaticFillableChest automaticFillableChest) {
        God.speak(LangDict.getString("treasury.mysteryChestTier") + automaticFillableChest.getChestTierChatColor() + automaticFillableChest.getChestTierName());

        if (automaticFillableChest instanceof GoldenMysteryChest) {
            Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_HARP);
        }
        if (automaticFillableChest instanceof PlatinumMysteryChest) {
            Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_BELL);
            SpecialEffects.blastFireworks(0);
        }
        if (automaticFillableChest instanceof DiamondMysteryChest) {
            Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_CHIME);
            SpecialEffects.blastFireworks(5);
        }
    }

    private void announceChestSpawned() {
        God.speak(LangDict.getString("treasury.mysteryChestSpawned"));
    }

    private AutomaticFillableChest getRandomMysteryChest() {
        double randomNumber = random.nextDouble();
        if (randomNumber < 0.40) {
            return new BasicMysteryChest();
        }else if (randomNumber < 0.70) {
            return new GoldenMysteryChest();
        }else if (randomNumber < 0.90) {
            return new PlatinumMysteryChest();
        }else {
            return new DiamondMysteryChest();
        }
    }
}
