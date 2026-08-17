package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.events.LandlordEvent;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S1Level8 extends Level{

    public S1Level8(Main plugin) {
        super(plugin, 1, 8);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();
        requiredItems.add(new ItemStack(Material.COOKED_BEEF, 16 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CACTUS, 120 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_MUTTON, 20 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_CHICKEN, 8 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 1200 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WHEAT, 180 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OBSIDIAN, 16 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.LAVA_BUCKET, 2 * Main.properties.getNumberOfPlayers()));
        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("levelBooks.season1.level8.godSpeak"));
    }

    @Override
    public int getRouletteGamePrice() {
        return 700;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }

    @Override
    public Book getBook() {
        Book book = new Book("S1L8");
        book.addPage(LangDict.getString("levelBooks.season1.level8.page1"));
        book.addPage(LangDict.getString("levelBooks.season1.level8.page2"));
        book.addPage(LangDict.getString("levelBooks.season1.level8.page3"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
