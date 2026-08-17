package com.johansvartdal.landlord;

import com.johansvartdal.landlord.chatentities.HintChat;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class RandomHint {

    private final Main plugin;
    public RandomHint(Main plugin) {
        this.plugin = plugin;
        scheduleHint();
    }

    private void scheduleHint() {
        Bukkit.getScheduler().runTaskLater(plugin, this::showHint, Tools.secToTicks(60*22));
    }

    public void showHint() {
        if (Main.properties.gameStateIsNormal()) {
            Tools.broadcastMessage(new HintChat(), getRandomHint());
        }
        scheduleHint();
    }

    private String getRandomHint() {
        // load all hints
        ArrayList<String> possibleHints = new ArrayList<>();
        possibleHints.addAll(getStandardHints());
        possibleHints.addAll(getUnlockedSpecificFeatures());
        possibleHints.addAll(getSeasonSpecific());

        // choose a random hint
        Random random = new Random();
        int randomTipNumber = random.nextInt(possibleHints.size());
        return possibleHints.get(randomTipNumber);
    }

    private ArrayList<String> getStandardHints() {
        ArrayList<String> hints = new ArrayList<>();
        hints.add(LangDict.getString("hints.standardHints.1") + BuySellManager.getSellableItemsHint());
        hints.add(LangDict.getString("hints.standardHints.2"));
        hints.add(LangDict.getString("hints.standardHints.3"));
        hints.add(LangDict.getString("hints.standardHints.4"));
        hints.add(LangDict.getString("hints.standardHints.5"));
        hints.add(LangDict.getString("hints.standardHints.6"));
        hints.add(LangDict.getString("hints.standardHints.7"));
        hints.add(LangDict.getString("hints.standardHints.8"));
        hints.add(LangDict.getString("hints.standardHints.9"));
        hints.add(LangDict.getString("hints.standardHints.10"));
        hints.add(LangDict.getString("hints.standardHints.11"));
        hints.add(LangDict.getString("hints.standardHints.12"));
        hints.add(LangDict.getString("hints.standardHints.13"));
        hints.add(LangDict.getString("hints.standardHints.14"));
        hints.add(LangDict.getString("hints.standardHints.15"));
        hints.add(LangDict.getString("hints.standardHints.16"));
        hints.add(LangDict.getString("hints.standardHints.17"));
        hints.add(LangDict.getString("hints.standardHints.18"));
        hints.add(LangDict.getString("hints.standardHints.19"));
        hints.add(LangDict.getString("hints.standardHints.20"));
        return hints;
    }

    private Collection<String> getSeasonSpecific() {
        ArrayList<String> hints = new ArrayList<>();

        if (LevelManager.getCurrentDisplaySeasonNum() == 2) {
            hints.add(LangDict.getString("hints.seasonHints.season2.1"));
            hints.add(LangDict.getString("hints.seasonHints.season2.2"));
            hints.add(LangDict.getString("hints.seasonHints.season2.3"));
            hints.add(LangDict.getString("hints.seasonHints.season2.4"));
        } else if (LevelManager.getCurrentDisplaySeasonNum() == 3) {
            hints.add(LangDict.getString("hints.seasonHints.season3.1"));
        }

        return hints;
    }

    private ArrayList<String> getUnlockedSpecificFeatures() {
        ArrayList<String> hints = new ArrayList<>();

        if (LevelManager.featureUnlocked("wildworld")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.wildworld.1"));
            hints.add(LangDict.getString("hints.featureSpecificHints.wildworld.2"));
            hints.add(LangDict.getString("hints.featureSpecificHints.wildworld.3"));
        }

        if (LevelManager.featureUnlocked("wildnether")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.wildnether.1"));
        } else {
            hints.add(LangDict.getString("hints.featureSpecificHints.wildnether.2"));
        }

        if (LevelManager.featureUnlocked("roulette")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.roulette.1"));
            hints.add(LangDict.getString("hints.featureSpecificHints.roulette.2"));
            hints.add(LangDict.getString("hints.featureSpecificHints.roulette.3"));
        }

        if (LevelManager.featureUnlocked("rent_basic_tool")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.rent_basic_tool.1"));
            hints.add(LangDict.getString("hints.featureSpecificHints.rent_basic_tool.2"));
            hints.add(LangDict.getString("hints.featureSpecificHints.rent_basic_tool.3"));
            hints.add(LangDict.getString("hints.featureSpecificHints.rent_basic_tool.4"));
        }

        if (LevelManager.featureUnlocked("visit")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.visit.1") + Tools.formatCurrency(StaticValues.VISIT_PRICE) + ".");
            hints.add(LangDict.getString("hints.featureSpecificHints.visit.2"));
        }

        if (LevelManager.featureUnlocked("fly")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.fly.1"));
        }

        if (LevelManager.featureUnlocked("day")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.day.1"));
        }

        if (LevelManager.featureUnlocked("stocks")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.stocks.1"));
            hints.add(LangDict.getString("hints.featureSpecificHints.stocks.2"));
            hints.add(LangDict.getString("hints.featureSpecificHints.stocks.3"));
            hints.add(LangDict.getString("hints.featureSpecificHints.stocks.4"));
            hints.add(LangDict.getString("hints.featureSpecificHints.stocks.5"));
        }

        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.rent_diamond_tools.1"));
        }

        if (LevelManager.featureUnlocked("rent_elytra")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.rent_elytra.1"));
        }

        if (LevelManager.featureUnlocked("wildmining")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.wildmining.1") + Tools.formatCurrency(StaticValues.MINING_PRICE) + ".");
            hints.add(LangDict.getString("hints.featureSpecificHints.wildmining.2"));
        }

        if (LevelManager.featureUnlocked("capture")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.capture.1"));
            hints.add(LangDict.getString("hints.featureSpecificHints.capture.2"));
            hints.add(LangDict.getString("hints.featureSpecificHints.capture.3"));
        }

        if (LevelManager.featureUnlocked("pay")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.pay.1"));
        }

        if (LevelManager.featureUnlocked("treasury")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.treasury.1"));
            hints.add(LangDict.getString("hints.featureSpecificHints.treasury.2"));

            if (LevelManager.featureUnlocked("treasuryhaste")) {
                hints.add(LangDict.getString("hints.featureSpecificHints.treasury.haste.1"));
            }

            if (LevelManager.featureUnlocked("treasurydonations")) {
                hints.add(LangDict.getString("hints.featureSpecificHints.treasury.donations.1"));
            }

            if (LevelManager.featureUnlocked("treasurychunkdiscount")) {
                hints.add(LangDict.getString("hints.featureSpecificHints.treasury.chunkdiscount.1"));
            }

            if (LevelManager.featureUnlocked("treasurymysterychest")) {
                hints.add(LangDict.getString("hints.featureSpecificHints.treasury.mysterychest.1"));
                hints.add(LangDict.getString("hints.featureSpecificHints.treasury.mysterychest.2"));
            }

            if (LevelManager.featureUnlocked("treasurywithdraw")) {
                hints.add(LangDict.getString("hints.featureSpecificHints.treasury.withdraw.1"));
            }
        }

        if (LevelManager.featureUnlocked("chunkguard")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.chunkguard.1"));
            hints.add(LangDict.getString("hints.featureSpecificHints.chunkguard.2"));
        }

        if (LevelManager.featureUnlocked("business")) {
            hints.add(LangDict.getString("hints.featureSpecificHints.business.1"));
            hints.add(LangDict.getString("hints.featureSpecificHints.business.2"));
            hints.add(LangDict.getString("hints.featureSpecificHints.business.3"));
        }

        return hints;
    }
}
