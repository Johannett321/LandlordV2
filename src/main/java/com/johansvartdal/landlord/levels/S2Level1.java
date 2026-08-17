package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.events.LandlordEvent;
import com.johansvartdal.landlord.events.adventure.ValleyVillageAdventure;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class S2Level1 extends Level{

    public S2Level1(Main plugin) {
        super(plugin, 2, 1);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.TUFF, 40 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.POTATO, 240 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CARROT, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SEA_PICKLE, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.PUMPKIN, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COCOA_BEANS, 256 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.DRIED_KELP_BLOCK, 21 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.OAK_LOG, 176 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 144 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.EGG, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WHITE_WOOL, 64 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.GLOWSTONE_DUST, 32 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CRYING_OBSIDIAN, 3 * Main.properties.getNumberOfPlayers()));

        return requiredItems;
    }

    @Override
    public void justUpgraded() {
        God.speak(LangDict.getString("levelBooks.season2.level1.godSpeak"));

        // schedule tax payback
        long paybackTime = Tools.secToTicks(60*25);
        if (Properties.DEV_CHEAT_MODE) {
            paybackTime = Tools.secToTicks(7);
        }
        Bukkit.getScheduler().runTaskLater(plugin, this::payBackTax, paybackTime);
    }

    private void payBackTax() {
        Random random = new Random();
        for (Player player : Bukkit.getOnlinePlayers()) {
            int tooMuchTaxAmount = 40000 + random.nextInt(80000);
            God.whisper(player, LangDict.getString("info.prePaybackTax") + player.getDisplayName() + LangDict.getString("info.midPaybackTax") + Tools.formatCurrency(tooMuchTaxAmount) + LangDict.getString("info.postPaybackTax"));
            Bank.depositPlayerWithoutTax(player, tooMuchTaxAmount);
        }
    }

    @Override
    public int getRouletteGamePrice() {
        return 800;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return new ValleyVillageAdventure(plugin);
    }

    @Override
    public Book getBook() {
        Book book = new Book("S2L1");
        book.addPage(LangDict.getString("levelBooks.season2.level1.page1"));
        book.addPage(LangDict.getString("levelBooks.season2.level1.page2"));
        book.addPage(LangDict.getString("levelBooks.season2.level1.page3"));
        book.addPage(LangDict.getString("levelBooks.season2.level1.page4"));
        book.addPage(LangDict.getString("levelBooks.season2.level1.page5"));
        book.addPage(LangDict.getString("levelBooks.season2.level1.page6"));
        book.addPage(LangDict.getString("levelBooks.season2.level1.page7"));
        book.addPage(LangDict.getString("levelBooks.season2.level1.page8"));
        book.addPage(LangDict.getString("levelBooks.season2.level1.page9"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
