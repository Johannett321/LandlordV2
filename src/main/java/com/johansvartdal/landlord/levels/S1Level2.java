package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S1Level2 extends Level{

    public S1Level2(Main plugin) {
        super(plugin, 1, 2);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 170 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR, 603 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WHEAT, 120 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("levelBooks.season1.level2.godSpeak"));
    }

    @Override
    public int getRouletteGamePrice() {
        return 300;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return null;
    }

    @Override
    public @NonNull Book getBook() {
        Book book = new Book("S1L2");
        book.addPage(LangDict.getString("levelBooks.season1.level2.page1"));
        book.addPage(LangDict.getString("levelBooks.season1.level2.page2"));
        book.addPage(LangDict.getString("levelBooks.season1.level2.page3"));
        book.addPage(LangDict.getString("levelBooks.season1.level2.page4"));
        book.addPage(LangDict.getString("levelBooks.season1.level2.page5"));
        book.addPage(LangDict.getString("levelBooks.season1.level2.page6"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
