package com.johansvartdal.landlord;

import com.johansvartdal.landlord.mysterychest.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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

        MysteryChest mysteryChest = getRandomMysteryChest();

        Bukkit.getScheduler().runTaskLater(plugin, () -> announceChestTier(mysteryChest), Tools.secToTicks(5));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            mysteryChest.spawnChest();
            mysteryChest.fillChest();

            announceChestSpawned();
        }, Tools.secToTicks(15));
    }

    private void announceChestOrdered() {
        God.speak(LangDict.getString("treasury.treasuryOrderedMysteryChest"));
    }


    private void announceChestTier(MysteryChest mysteryChest) {
        God.speak(LangDict.getString("treasury.mysteryChestTier") + mysteryChest.getChestTierChatColor() + mysteryChest.getChestTierName());

        if (mysteryChest instanceof GoldenMysteryChest) {
            Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_HARP);
        }
        if (mysteryChest instanceof PlatinumMysteryChest) {
            Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_BELL);
            SpecialEffects.blastOneFireWork(Main.tradeCenter.getLocation(), Color.BLUE);
        }
        if (mysteryChest instanceof DiamondMysteryChest) {
            Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_CHIME);
            SpecialEffects.blastFireworks(5);
        }
    }

    private void announceChestSpawned() {
        God.speak(LangDict.getString("treasury.mysteryChestSpawned"));
    }

    private MysteryChest getRandomMysteryChest() {
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
