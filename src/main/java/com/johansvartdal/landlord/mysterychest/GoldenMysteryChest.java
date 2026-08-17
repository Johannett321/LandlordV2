package com.johansvartdal.landlord.mysterychest;

import com.johansvartdal.landlord.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static com.johansvartdal.landlord.Tools.enchantBook;
import static com.johansvartdal.landlord.Tools.enchantItem;

public class GoldenMysteryChest extends AutomaticFillableChest {
    @Override
    public ChatColor getChestTierChatColor() {
        return ChatColor.GOLD; // Represents Gold tier
    }

    @Override
    public String getChestTierName() {
        return "GOLD";
    }

    @Override
    public ItemStack[] getFillerItems() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Filler items (useful but common)
        items.add(new ItemStack(Material.COAL, 6));

        items.add(new ItemStack(Material.BREAD, 4));
        items.add(new ItemStack(Material.IRON_NUGGET, 5));

        items.add(new ItemStack(Material.CARROT, 2));
        items.add(new ItemStack(Material.POTATO, 2));

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier1Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Iron Ingot (useful for crafting)
        items.add(new ItemStack(Material.IRON_INGOT, 3 * Main.properties.getNumberOfPlayers()));

        // Gold Ingots (gold tier should have gold!)
        items.add(new ItemStack(Material.GOLD_INGOT, 2 * Main.properties.getNumberOfPlayers()));

        // Leather Boots
        ItemStack leatherBoots = new ItemStack(Material.LEATHER_BOOTS);
        enchantItem(leatherBoots, Enchantment.FEATHER_FALLING, 2);
        items.add(leatherBoots);

        items.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 4));
        items.add(new ItemStack(Material.BOOK, 2 * Main.properties.getNumberOfPlayers()));
        items.add(new ItemStack(Material.NAME_TAG, 1));

        // Silk touch book
        ItemStack silkTouch = new ItemStack(Material.ENCHANTED_BOOK);
        enchantBook(silkTouch, Enchantment.SILK_TOUCH, 1);
        silkTouch.setAmount(1);
        items.add(silkTouch);

        // Fortune book
        ItemStack fortuneBook = new ItemStack(Material.ENCHANTED_BOOK);
        enchantBook(fortuneBook, Enchantment.FORTUNE, 2);
        fortuneBook.setAmount(1);
        items.add(fortuneBook);

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier2Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Iron Sword with Sharpness
        ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
        enchantItem(ironSword, Enchantment.SHARPNESS, 2);
        items.add(ironSword);

        // Golden Helmet with Protection
        ItemStack goldenHelmet = new ItemStack(Material.GOLDEN_HELMET);
        enchantItem(goldenHelmet, Enchantment.PROTECTION, 2);
        items.add(goldenHelmet);

        // Bow with Power I
        ItemStack bow = new ItemStack(Material.BOW);
        enchantItem(bow, Enchantment.POWER, 1);
        items.add(bow);

        // Fishing Rod with Luck of the Sea
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);
        enchantItem(fishingRod, Enchantment.LUCK_OF_THE_SEA, 2);
        items.add(fishingRod);

        items.add(new ItemStack(Material.ENCHANTING_TABLE, 1));

        items.add(new ItemStack(Material.DIAMOND_HORSE_ARMOR, 1));

        return items.toArray(ItemStack[]::new);
    }
}
