package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.Book;
import com.johansvartdal.landlord.LandlordEvent;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class S2Level2 extends Level{

    public S2Level2(Main plugin) {
        super(plugin, 2, 1);
    }

    @Override
    public ArrayList<ItemStack> getRequiredItemsForNextLevel() {
        ArrayList<ItemStack> requiredItems = new ArrayList<>();

        //TODO NOT DONE!!
        requiredItems.add(new ItemStack(Material.FLINT, 34 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.POINTED_DRIPSTONE, 23 * Main.properties.getNumberOfPlayers()));
        // CHERRY: requiredItems.add(new ItemStack(Material.CHERRY_LOG, 23 * Main.properties.getNumberOfPlayers()));


        requiredItems.add(new ItemStack(Material.KELP, 64 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.MILK_BUCKET, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.COCOA_BEANS, 256 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WHITE_WOOL, 100 * Main.properties.getNumberOfPlayers()));

        requiredItems.add(new ItemStack(Material.WARPED_WART_BLOCK, 19 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.SHROOMLIGHT, 4 * Main.properties.getNumberOfPlayers()));
        requiredItems.add(new ItemStack(Material.WEEPING_VINES, 20 * Main.properties.getNumberOfPlayers()));

        // COPPER ORE
        // TUFF fant jeg veldig dypt! -45Y. Denne er ganske vanlig.
        // Pointed dripstone
        // Dripstone block
        // Melons
        // Potatoes
        // Carrots
        // Grisekjøtt
        // Bambus
        // Sand
        // clay
        // Spruce logs (mange)
        // Raw cod, Raw salmon (kanskje cooked)
        // Blomster (poppy)
        // Hay bales
        // Barrels
        // Redstone
        // Honeycomb block
        // Turtle egg
        // Glowstone
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

    }

    @Override
    public int getRouletteGamePrice() {
        return 800;
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
