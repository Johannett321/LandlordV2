package com.johansvartdal.landlord.mysterychest;

import com.johansvartdal.landlord.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static com.johansvartdal.landlord.Tools.enchantBook;
import static com.johansvartdal.landlord.Tools.enchantItem;

public class PlatinumMysteryChest extends MysteryChest {
    @Override
    public ChatColor getChestTierChatColor() {
        return ChatColor.LIGHT_PURPLE; // Represents Platinum tier
    }

    @Override
    public String getChestTierName() {
        return "PLATINUM";
    }

    @Override
    public ItemStack[] getFillerItems() {
        ArrayList<ItemStack> items = new ArrayList<>();

        ItemStack iron = new ItemStack(Material.IRON_INGOT, 6 * Main.properties.getNumberOfPlayers());
        items.add(iron);

        ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT, 5 * Main.properties.getNumberOfPlayers());
        items.add(goldIngot);

        ItemStack coal = new ItemStack(Material.COAL, 8 * Main.properties.getNumberOfPlayers());
        items.add(coal);

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier1Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        ItemStack diamond = new ItemStack(Material.DIAMOND, 2 * Main.properties.getNumberOfPlayers());
        items.add(diamond);

        // Emeralds (used for trading)
        ItemStack emerald = new ItemStack(Material.EMERALD, 2 * Main.properties.getNumberOfPlayers());
        items.add(emerald);

        // Golden Apples
        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 2 * Main.properties.getNumberOfPlayers());
        items.add(goldenApple);

        // Blaze Powder (useful for brewing)
        ItemStack blazePowder = new ItemStack(Material.BLAZE_POWDER, 6 * Main.properties.getNumberOfPlayers());
        items.add(blazePowder);

        // Ender Pearls
        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL, 2 * Main.properties.getNumberOfPlayers());
        items.add(enderPearl);

        // Firework
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        firework.setAmount(6 * Main.properties.getNumberOfPlayers());

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier2Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Diamond Sword with Sharpness
        ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
        enchantItem(diamondSword, Enchantment.SHARPNESS, 5);
        enchantItem(diamondSword, Enchantment.MENDING, 1);
        items.add(diamondSword);

        // Enchanted Bow with Power
        ItemStack bow = new ItemStack(Material.BOW);
        enchantItem(bow, Enchantment.POWER, 4);
        enchantItem(bow, Enchantment.INFINITY, 1);
        enchantItem(bow, Enchantment.UNBREAKING, 3);
        items.add(bow);

        // Enchanted Diamond Chestplate
        ItemStack diamondChestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        enchantItem(diamondChestplate, Enchantment.PROTECTION, 3);
        enchantItem(diamondChestplate, Enchantment.MENDING, 1);
        items.add(diamondChestplate);

        // Enchanted Diamond Chestplate
        ItemStack diamondHelmet = new ItemStack(Material.DIAMOND_HELMET);
        enchantItem(diamondHelmet, Enchantment.PROTECTION, 3);
        enchantItem(diamondHelmet, Enchantment.MENDING, 1);
        items.add(diamondHelmet);

        // fortune book
        ItemStack fortuneBook = new ItemStack(Material.ENCHANTED_BOOK);
        enchantBook(fortuneBook, Enchantment.FORTUNE, 3);
        fortuneBook.setAmount(1);
        items.add(fortuneBook);

        return items.toArray(ItemStack[]::new);
    }
}
