package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.events.LandlordEvent;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.events.arenafight.ArenaFight2;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S2Level4 extends Level{

    public S2Level4(Main plugin) {
        super(plugin, 2, 4);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        requiredItems.add(new ItemStack(Material.DRIPSTONE_BLOCK, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BARREL, 19 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SAND, 117 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.BOOK, 16 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.CACTUS, 70 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CHERRY_PLANKS, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR, 109 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BEETROOT, 34 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.TURTLE_EGG, 6 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.COPPER_INGOT, 160 * Main.properties.getNumberOfPlayers()));




/*
        requiredItems.add(new ItemStack(Material.ANDESITE, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.GRANITE, 100 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.DIORITE, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.TUFF, 40 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.DEEPSLATE, 90 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.MANGROVE_LOG, 23 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BIRCH_LOG, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LOG, 176 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.OAK_LEAVES, 144 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SPRUCE_LOG, 70*Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COBBLESTONE, 170 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.DRIPSTONE_BLOCK, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BARREL, 19 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SAND, 117 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.FLINT, 34 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CLAY, 28 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.POINTED_DRIPSTONE, 23 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SNOWBALL, 8 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.BOOK, 16 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.CACTUS, 70 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CHERRY_PLANKS, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CHERRY_SAPLING, 37 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR, 109 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BEETROOT, 34 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.TURTLE_EGG, 6 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.KELP, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.BAMBOO_MOSAIC, 11 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SUGAR_CANE, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_MUTTON, 5 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_SALMON, 7 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.HAY_BLOCK, 25 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.POTATO, 240 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.CARROT, 128 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SEA_PICKLE, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.PUMPKIN, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.MELON, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COCOA_BEANS, 256 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.EGG, 24));
        requiredItems.add(new ItemStack(Material.WHITE_WOOL, 21 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.MILK_BUCKET, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.PUMPKIN_SEEDS, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COOKED_PORKCHOP, 22 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.HONEY_BLOCK, 7 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.COPPER_INGOT, 160 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.DIAMOND, 2 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.RAW_IRON, 16 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.GOLD_INGOT, 19 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.EMERALD, Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.IRON_BLOCK, 2 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.NETHER_WART, 30 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.GLOWSTONE, 16 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WARPED_WART_BLOCK, 19 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SHROOMLIGHT, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WEEPING_VINES, 20 * Main.properties.getNumberOfPlayers()));
        */

        // COPPER ORE
        // Sand
        // Redstone
        // En eller annen farget concrete powder som lages fra en farge du får av dyr (for eksempel svart)
        // Waxed copper block (Lages fra honning og kobber)
        // Block of bamboo
        // Cherry planks?


        // -------- FRA NETHER -----------
        // warped nylium (grass) (KREVER SILK TOUCH)
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
        God.speak(LangDict.getString("levelBooks.season2.level4.godSpeak"));
    }

    @Override
    public int getRouletteGamePrice() {
        return 900;
    }

    @Override
    public LandlordEvent getEventToStartBeforeLevel() {
        return new ArenaFight2(plugin);
    }

    @Override
    public Book getBook() {
        Book book = new Book("S2L4");
        book.addPage(LangDict.getString("levelBooks.season2.level4.page1"));
        book.addPage(LangDict.getString("levelBooks.endSignature"));
        return book;
    }
}
