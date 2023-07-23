package com.johansvartdal.landlord;

import com.johansvartdal.landlord.chatentities.HintChat;
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

    private void showHint() {
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
        hints.add("Some of the items that can be sold: " + BuySellManager.getSellableItemsHint());
        hints.add("The items you will need the most of for upgrading your town, is sugar_canes and cobblestone");
        hints.add("By typing '/buychunk info', you can see how many chunks you can claim");
        hints.add("Type '/bal' to see your current tax and fortune");
        hints.add("The value of items change during the day. By typing '/sell info', you can monitor the price, and sell when it's worth the most");
        hints.add("Have you calimed your bonus today? Do '/claimbonus' to claim your daily reward");
        hints.add("Do '/changelang [ LANG_CODE ]' to change the language of Landlord");
        hints.add("'/home' will almost always take you home. Almost...");
        hints.add("Do you want to build something cool, or just trade with someone? Do '/trade'");
        hints.add("Did you know you could find a better spot for your home? Do '/sethome' somewhere else inside your chunk");

        hints.add("Landlord supports up to 8 players!");
        hints.add("Did you know the Landlord plugin was developed by Johan Svartdal?");
        hints.add("Did you know the Landlord adventures was built by Johan Svartdal and Daniel Martinsen?");

        return hints;
    }

    private Collection<String> getSeasonSpecific() {
        ArrayList<String> hints = new ArrayList<>();
        if (LevelManager.getCurrentDisplaySeasonNum() == 2) {
            hints.add("In season 2, you may have to fight in an arena with different monsters");
            hints.add("In season 2, there will be hosted excursions to exciting adventure locations");
            hints.add("New season means new location for wilderness, both in nether and world. Make sure to put your most valuable items in a chest before visiting wilderness for the first time this season.");
        }
        return hints;
    }

    private ArrayList<String> getUnlockedSpecificFeatures() {
        ArrayList<String> hints = new ArrayList<>();

        if (LevelManager.featureUnlocked("wildworld")) {
            hints.add("By typing /wilderness time, you can see how much time you got left of your wilderness adventure");
            hints.add("Before using /wilderness for the first time, put your most important items in a chest. Just in case you spawn in lava");
        }

        if (LevelManager.featureUnlocked("roulette")) {
            hints.add("By typing /joinroulette, you can see how much time is left before the next round of roulette");
            hints.add("Every full hour there is a roulette game where you can win lots of items. Do /joinroulette to take part");
        }

        if (LevelManager.featureUnlocked("rent_basic_tool")) {
            hints.add("Tired of your slow pickaxe? Try '/rent pickaxe', and see both the speed and the number of diamonds increase!");
            hints.add("Need some wood? Do '/rent axe' before you chop down your next tree");
        }

        if (LevelManager.featureUnlocked("visit")) {
            hints.add("The command /visit allows you to visit other players for a visit fee of " + StaticValues.VISIT_PRICE + LangDict.getString(LangDict.CURRENCY));
            hints.add("Someone not following your rules inside your property? Send them home with '/sendhome [ USERNAME ]'");
        }

        if (LevelManager.featureUnlocked("fly")) {
            hints.add("Do '/fly' to fly like you're in creative mode. This makes it a lot easier to build");
        }

        if (LevelManager.featureUnlocked("day")) {
            hints.add("Do '/day' to get rid of the rain, change the time to day");
        }

        if (LevelManager.featureUnlocked("stocks")) {
            hints.add("Investing in stocks is perhaps the most effective approach to becoming wealthy in Landlord. This is because not only is there potential for the stock value to increase, but also there is no need to pay wealth tax on them");
            hints.add("Store your stocks in a secret place. You don't want anyone to steal them. Your inventory is probably the worst place. One evil creeper and their all gone");
            hints.add("Do '/stocks list' to see all the stocks and their value");
            hints.add("Do '/stocks info [ STOCKNAME ]' to see how the value of a stock has changed during the last hour");
            hints.add("Do '/stocks buy [ STOCKNAME ] [AMOUNT]' to buy some stocks");
        }

        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            hints.add("You can now rent diamond tools. Do '/rent info' to see everything you can rent");
        }

        if (LevelManager.featureUnlocked("rent_elytra")) {
            hints.add("Down for some exploring? Do '/wilderness world', and then '/rent elytra'. Just remember to bring rockets");
        }

        if (LevelManager.featureUnlocked("wildmining")) {
            hints.add("A great way to mine, is doing '/wilderness mine'. It will give you 45 minutes of non-stop mining for only " + StaticValues.MINING_PRICE + LangDict.getString(LangDict.CURRENCY));
        }

        return hints;
    }
}
