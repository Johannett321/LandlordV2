package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.events.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.events.adventure.IcyHillsEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S2Level3 extends Level{

    public S2Level3(Main plugin) {
        super(plugin, 2, 3);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 170 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.MANGROVE_LOG, 23 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.KELP, 512 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BAMBOO_MOSAIC, 41 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_MUTTON, 5 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_SALMON, 21 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.DIAMOND, 2 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.RAW_IRON, 24 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.GOLD_INGOT, 19 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.EMERALD, 10 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("levelBooks.season2.level3.godSpeak"));
    }

    @Override
    public int getRouletteGamePrice() {
        return 900;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return new IcyHillsEvent(plugin);
    }

    @Override
    public Book getBook() {
        Book book = new Book("S2L3");
        book.addPage(LangDict.getString("levelBooks.season2.level3.page1"));
        book.addPage(LangDict.getString("levelBooks.season2.level3.page2"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
