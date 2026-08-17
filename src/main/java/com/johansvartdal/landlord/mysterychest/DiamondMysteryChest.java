package com.johansvartdal.landlord.mysterychest;

import com.johansvartdal.landlord.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static com.johansvartdal.landlord.Tools.enchantBook;
import static com.johansvartdal.landlord.Tools.enchantItem;

public class DiamondMysteryChest extends AutomaticFillableChest {
    @Override
    public ChatColor getChestTierChatColor() {
        return ChatColor.BLUE;
    }

    @Override
    public String getChestTierName() {
        return "DIAMOND";
    }

    @Override
    public ItemStack[] getFillerItems() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Expensive filler items
        items.add(new ItemStack(Material.DIAMOND, 3));
        items.add(new ItemStack(Material.GOLDEN_CARROT, 4));
        items.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 6));
        items.add(new ItemStack(Material.ENDER_PEARL, 1));

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier1Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // shulker shells
        ItemStack shulkerShells = new ItemStack(Material.SHULKER_SHELL);
        shulkerShells.setAmount(4 + Main.properties.getNumberOfPlayers());
        items.add(shulkerShells);

        // diamonds 3
        ItemStack diamonds = new ItemStack(Material.DIAMOND);
        diamonds.setAmount(5 * Main.properties.getNumberOfPlayers());
        items.add(diamonds);

        // gold
        ItemStack gold = new ItemStack(Material.GOLD_INGOT);
        gold.setAmount(5 * Main.properties.getNumberOfPlayers());
        items.add(gold);

        // Emerald
        ItemStack emerald = new ItemStack(Material.EMERALD);
        emerald.setAmount(8 * Main.properties.getNumberOfPlayers());
        items.add(emerald);

        // Ender Pearl
        ItemStack enderPearl = new ItemStack(Material.ENDER_PEARL);
        enderPearl.setAmount(2 * Main.properties.getNumberOfPlayers());
        items.add(enderPearl);

        // fortune book
        ItemStack fortuneBook = new ItemStack(Material.ENCHANTED_BOOK);
        enchantBook(fortuneBook, Enchantment.FORTUNE, 3);
        fortuneBook.setAmount(1);
        items.add(fortuneBook);

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier2Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // elytra
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        enchantItem(elytra, Enchantment.UNBREAKING, 2);
        enchantItem(elytra, Enchantment.MENDING, 1);
        elytra.setAmount(1);
        items.add(elytra);

        // netherite pickaxe
        ItemStack netheritePickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        enchantItem(netheritePickaxe, Enchantment.MENDING, 1);
        netheritePickaxe.setAmount(1);
        items.add(netheritePickaxe);

        // netherite chestplate
        ItemStack netheriteChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        enchantItem(netheriteChestplate, Enchantment.MENDING, 3);
        enchantItem(netheriteChestplate, Enchantment.PROTECTION, 3);
        netheriteChestplate.setAmount(1);
        items.add(netheriteChestplate);

        // totem of undying
        ItemStack totemOfUndying = new ItemStack(Material.TOTEM_OF_UNDYING);
        totemOfUndying.setAmount(1);
        items.add(totemOfUndying);

        // netherStar
        ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
        netherStar.setAmount(1);
        items.add(netherStar);

        // villageSpawnEgg
        ItemStack villagerSpawnEgg = new ItemStack(Material.VILLAGER_SPAWN_EGG);
        villagerSpawnEgg.setAmount(1);
        items.add(villagerSpawnEgg);

        // mendingBook
        ItemStack mendingBook = new ItemStack(Material.ENCHANTED_BOOK);
        enchantBook(mendingBook, Enchantment.MENDING, 1);
        mendingBook.setAmount(1);
        items.add(mendingBook);

        return items.toArray(ItemStack[]::new);
    }
}
