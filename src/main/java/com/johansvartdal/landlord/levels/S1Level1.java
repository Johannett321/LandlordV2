package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S1Level1 extends Level {

    public S1Level1(Main plugin) {
        super(plugin, 1, 1);
    }

    @Override
    public void justUpgraded() {
        Main.properties.setGameState(Properties.GameState.NORMAL);
    }

    @Override
    public int getRouletteGamePrice() {
        return 300;
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 80 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WHEAT, 80 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 144 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }

    @Override
    public @NonNull Book getBook() {
        return null;
    }
}
