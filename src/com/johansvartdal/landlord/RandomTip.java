package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Random;

public class RandomTip {

    private Main plugin;
    public RandomTip(Main plugin) {
        this.plugin = plugin;

        scheduleTip();
    }

    private void scheduleTip() {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                showTip();
                scheduleTip();
            }
        }, 20*60*22);
    }

    private void showTip() {
        Tools.broadcastMessage(ChatColor.GRAY + "Tip: " + getRandomTip());
    }

    private String getRandomTip() {
        //TODO SKRIV TIPS FOR LEVELER SOM IKKE
        Random random = new Random();
        if (LevelManager.getCurrentDisplaySeasonNum() == 1) {
            int randomTip = random.nextInt(12);

            switch (randomTip) {
                case 0: return "You can sell the following items " + BuySellManager.getSellableItemsString();
                case 1: return "The most needed items in upgrades are Cobblestone and Sugar canes";
                case 2: return "By typing /wilderness time, you can see how much time you got left of your wilderness adventure";
                case 3: return "By typing /joinroulette, you can see how much time is left before the next round of roulette";
                case 4: return "By typing /chunkbalance, you can see how many chunks you can claim";
                case 5: return "By typing /richness, you can see how rich you are";
                case 6: return "Before using /wilderness for the first time, put your most important items in a chest. Just in case you spawn in lava";
                case 7: return "By typing /worth, you can see how much the item in your hand will sell for";
                case 8: return "Before claiming a new chunk, make sure you clear all blocks next to the barrier you want to claim. Then you don't have to worry about large stone walls!";
                case 9: return "Type /easteregg";
                case 10: return "Every full hour there is a roulette game where you can win lots of items. Do /joinroulette to take part";
                case 11: return "You can capture the following animals: ";// + Capture.getCaptureAnimalsString();
            }
        }else if (LevelManager.getCurrentDisplaySeasonNum() == 2) {
            int randomTip = random.nextInt(11);

            switch (randomTip) {
                case 0: return "You can sell the following items " + BuySellManager.getSellableItemsString();
                case 1: return "By typing /chunkbalance, you can see how many chunks you can claim";
                case 2: return "By typing /richness, you can see how rich you are";
                case 3: return "Before claiming a new chunk, make sure you clear all blocks next to the barrier you want to claim. Then you don't have to worry about large stone walls!";
                case 4: return "Type /easteregg";
                case 5: return "You can capture the following animals: "; // + Capture.getCaptureAnimalsString();

                case 6: return "The command /visit allows you to visit other players for a visit fee of "; // + StaticValues.VISIT_PRICE;
                case 7: return "In season 2, you will fight in the arena after every second level";
                case 8: return "In season 2, there will be hosted an adventure after every second level";
                case 9: return "Sometime by the end of season 2, you will all get together like never before";
                case 10: return "New season means new location for wilderness, both in nether and world. Make sure to put your most valuable items in a chest before visiting wilderness for the first time this season.";
            }
        }else if (LevelManager.getCurrentDisplaySeasonNum() == 3) {
            int randomTip = random.nextInt(7);

            switch (randomTip) {
                case 0: return "Before claiming a new chunk, make sure you clear all blocks next to the barrier you want to claim. Then you don't have to worry about large stone walls!";
                case 1: return "Type /easteregg";
                case 2: return "New season means new location for wilderness, both in nether and world. Make sure to put your most valuable items in a chest before visiting wilderness for the first time this season.";

                case 3: return "There will be two huge events this season. I can't tell you anything more about when it will happen. Make sure you prepare yourself with diamond/netherite weapons and armor";
                case 5: return "All the arena fights in season 2 was not without a reason. Get ready for bigger battles. However, i cannot tell you when it will happen";
                case 6: return "You are getting closer to the end of the game. At level 5, something insane will happen. Something to really look forward to";
            }
        }

        return "";
    }
}
