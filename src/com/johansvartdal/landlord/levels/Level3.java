package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Level3 extends Level{

    public Level3(Main plugin) {
        super(plugin, 1, 3);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.COBBLESTONE, 210 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR, 448 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 203 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LOG, 176 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COPPER_INGOT, 19 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("levelBooks.level3.godSpeak"));
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
    public Book getBook() {
        Book book = new Book("S1L3");
        book.addPage(LangDict.getString("levelBooks.level3.page1"));
        book.addPage(LangDict.getString("levelBooks.level3.page2"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
