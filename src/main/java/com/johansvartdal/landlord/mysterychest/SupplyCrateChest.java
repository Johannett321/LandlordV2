package com.johansvartdal.landlord.mysterychest;

import com.johansvartdal.landlord.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

import static com.johansvartdal.landlord.Tools.enchantBook;
import static com.johansvartdal.landlord.Tools.enchantItem;

public class SupplyCrateChest extends AutomaticFillableChest {
    @Override
    public ChatColor getChestTierChatColor() {
        return ChatColor.GOLD;
    }

    @Override
    public String getChestTierName() {
        return "SUPPLY CRATE";
    }

    @Override
    public ItemStack[] getFillerItems() {
        Random random = new Random();
        ArrayList<ItemStack> items = new ArrayList<>();

        ItemStack iron = new ItemStack(Material.IRON_INGOT, random.nextInt(13)+1);
        items.add(iron);

        ItemStack coal = new ItemStack(Material.COAL, random.nextInt(63)+1);
        items.add(coal);

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier1Items() {
        Random random = new Random();
        ArrayList<ItemStack> items = new ArrayList<>();

        ItemStack diamond = new ItemStack(Material.DIAMOND, random.nextInt(5)+5);
        items.add(diamond);

        // Emeralds (used for trading)
        ItemStack emerald = new ItemStack(Material.EMERALD, random.nextInt(5)+5);
        items.add(emerald);

        // Golden Apples
        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, random.nextInt(5)+5);
        items.add(goldenApple);

        // Firework
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        firework.setAmount(random.nextInt(50)+5);

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier2Items() {
        Random random = new Random();
        ArrayList<ItemStack> items = new ArrayList<>();

        // Netherite Sword with Sharpness
        ItemStack netheriteSword = new ItemStack(Material.NETHERITE_SWORD);
        enchantItem(netheriteSword, Enchantment.SHARPNESS, 5);
        enchantItem(netheriteSword, Enchantment.MENDING, 1);
        enchantItem(netheriteSword, Enchantment.UNBREAKING, 3);
        items.add(netheriteSword);

        // Enchanted Bow with Power
        ItemStack bow = new ItemStack(Material.BOW);
        enchantItem(bow, Enchantment.POWER, 5);
        enchantItem(bow, Enchantment.INFINITY, 1);
        enchantItem(bow, Enchantment.UNBREAKING, 3);
        items.add(bow);

        // Enchanted Netherite Chestplate
        ItemStack netheriteChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        enchantItem(netheriteChestplate, Enchantment.PROTECTION, 4);
        enchantItem(netheriteChestplate, Enchantment.MENDING, 1);
        enchantItem(netheriteChestplate, Enchantment.UNBREAKING, 3);
        items.add(netheriteChestplate);

        // Enchanted Netherite Chestplate
        ItemStack netheriteHelmet = new ItemStack(Material.NETHERITE_HELMET);
        enchantItem(netheriteHelmet, Enchantment.PROTECTION, 4);
        enchantItem(netheriteHelmet, Enchantment.MENDING, 1);
        enchantItem(netheriteHelmet, Enchantment.UNBREAKING, 3);
        items.add(netheriteHelmet);

        // Enchanted Netherite Boots
        ItemStack netheriteBoots = new ItemStack(Material.NETHERITE_BOOTS);
        enchantItem(netheriteBoots, Enchantment.PROTECTION, 4);
        enchantItem(netheriteBoots, Enchantment.MENDING, 1);
        enchantItem(netheriteBoots, Enchantment.UNBREAKING, 3);
        items.add(netheriteBoots);

        // fortune book
        ItemStack fortuneBook = new ItemStack(Material.ENCHANTED_BOOK);
        enchantBook(fortuneBook, Enchantment.FORTUNE, 3);
        fortuneBook.setAmount(1);
        items.add(fortuneBook);

        // mending book
        ItemStack mendingBook = new ItemStack(Material.ENCHANTED_BOOK);
        enchantBook(mendingBook, Enchantment.MENDING, 1);
        mendingBook.setAmount(1);
        items.add(mendingBook);

        // shulker shells
        ItemStack shulkerShells = new ItemStack(Material.SHULKER_SHELL, random.nextInt(6)+1);
        items.add(shulkerShells);

        // shulker shells
        ItemStack netherStar = new ItemStack(Material.NETHER_STAR, 1);
        items.add(netherStar);

        return items.toArray(ItemStack[]::new);
    }
}
