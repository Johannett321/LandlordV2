package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.events.LandlordEvent;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S1Level6 extends Level{

    public S1Level6(Main plugin) {
        super(plugin, 1, 6);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.ANDESITE, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR, 448 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BIRCH_LOG, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SNOWBALL, 8 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COCOA_BEANS, 64 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("levelBooks.season1.level6.godSpeak"));
    }

    @Override
    public int getRouletteGamePrice() {
        return 600;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }

    @Override
    public Book getBook() {
        Book book = new Book("S1L6");
        book.addPage(LangDict.getString("levelBooks.season1.level6.page1"));
        book.addPage(LangDict.getString("levelBooks.season1.level6.page2"));
        book.addPage(LangDict.getString("levelBooks.season1.level6.page3"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
