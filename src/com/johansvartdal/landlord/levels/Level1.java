package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Properties;
import com.johansvartdal.landlord.Tools;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level1 extends Level {

    Main plugin;

    public Level1(Main plugin) {
        super(plugin, 1);
        this.plugin = plugin;
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        ItemStack paper = new ItemStack(Material.OAK_LOG, 64 * Main.properties.getNumberOfPlayers());
        requiredItems.add(paper);

        return requiredItems;
    }

    @Override
    public void afterUpgradeEvent() {
        super.afterUpgradeEvent();
        Main.properties.setGameState(Properties.GameState.NORMAL);
    }
}
