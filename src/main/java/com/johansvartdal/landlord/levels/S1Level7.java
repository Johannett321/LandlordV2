package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.events.LandlordEvent;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S1Level7 extends Level{

    public S1Level7(Main plugin) {
        super(plugin, 1, 7);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();
        requiredItems.add(new ItemStack(Material.COOKED_BEEF, 16 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.INK_SAC, 13 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.HONEYCOMB, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CACTUS, 120 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WHEAT, 230 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 140 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_COD, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 370 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.POTATO, 77 * Main.properties.getNumberOfPlayers()));
        return requiredItems;
    }

    @Override
    public void justUpgraded() {

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
        Book book = new Book("S1L7");
        book.addPage(LangDict.getString("levelBooks.season1.level7.page1"));
        book.addPage(LangDict.getString("levelBooks.season1.level7.page2"));
        book.addPage(LangDict.getString("levelBooks.season1.level7.page3"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
