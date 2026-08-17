package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.events.LandlordEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S1Level4 extends Level{

    public S1Level4(Main plugin) {
        super(plugin, 1, 4);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 192 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.PUMPKIN_SEEDS, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 256 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 144 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.DIORITE, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.DEEPSLATE, 90 * Main.properties.getNumberOfPlayers()));
        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("levelBooks.season1.level4.godSpeak"));
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
        Book book = new Book("S1L4");
        book.addPage(LangDict.getString("levelBooks.season1.level4.page1"));
        book.addPage(LangDict.getString("levelBooks.season1.level4.page2"));
        book.addPage(LangDict.getString("levelBooks.season1.level4.page3"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
