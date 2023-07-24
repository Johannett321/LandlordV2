package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level4 extends Level{

    public Level4(Main plugin) {
        super(plugin, 1, 4);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 192 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.PUMPKIN_SEEDS, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 144 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 144 * Main.properties.getNumberOfPlayers()));

        // COPPER ORE
        // DIORITE?
        // Deepslate fant jeg under Y0
        // TUFF fant jeg veldig dypt! -45Y
        // cocoa beans
        // milk bucket

        // -------- FRA NETHER -----------
        // warped nylium (grass)
        // warped stem (trestamme blå)
        // warped wart block (Leaves til blått tre)
        // warped fungus (blomst fra nether)
        // crimson fungus (annen blomst fra nether)
        // shroomlights (glowstone lignende fra forest i nether)

        // [rød nether]
        // weeping wine (henger ned fra taket, er rød og dropper weeping wine

        // [dark biome] (skjelden tror jeg)
        // basalt (grå stein lignende blokk)
        // blackstone (sort, og ganske vanlig i dark biome. En hel blokk)
        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("levelBooks.level4.page1") + LevelManager.getWildernessPrice() + LangDict.getString(LangDict.CURRENCY));
        God.speak(LangDict.getString("levelBooks.level4.page2"));
    }

    @Override
    public int getRouletteGamePrice() {
        return 500;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }

    @Override
    public Book getBook() {
        return null;
    }
}
