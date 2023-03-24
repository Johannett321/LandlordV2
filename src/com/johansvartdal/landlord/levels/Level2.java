package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level2 extends Level{

    public Level2(Main plugin) {
        super(plugin, 1, 2);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 80 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR, 208 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WHEAT, 80 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak("Heeeey, how exciting! Your first town upgrade! Everytime your town levels up, something will happen." +
                " This time you unlocked a feature called roulette. Every hour, a roulette will run. The roulette gives you" +
                " an opportunity to win a price. However, there is a participation fee to join. You will be notified before" +
                " a roulette is about to start");
        God.speak("You were also rewarded a chunk point each. Stand next to one of your chunk borders. While looking towards" +
                " the border, execute the command '/buychunk', and watch the walls magically fall as you unlock your second chunk!");
    }

    @Override
    public int getRouletteGamePrice() {
        return 300;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }
}
