package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level1 extends Level {

    private Main plugin;

    public Level1(Main plugin) {
        super(plugin, 1);
        this.plugin = plugin;
    }

    @Override
    public void load() {
        //TODO get already collected items
    }

    @Override
    public void justUpgraded() {
        God.speak("Welcome to level " + this.getDisplayLevelNumber());
        Main.properties.setGameState(Properties.GameState.NORMAL);
    }

    @Override
    public Challenge getUpgradeChallenge() {
        return null;
    }

    @Override
    public LandlordEvent getUpgradeEvent() {
        return null;
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        ItemStack paper = new ItemStack(Material.COAL, 64 * Main.properties.getNumberOfPlayers());
        requiredItems.add(paper);

        return requiredItems;
    }
}
