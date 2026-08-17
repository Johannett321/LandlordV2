package com.johansvartdal.landlord.mysterychest;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

import static com.johansvartdal.landlord.Tools.enchantItem;

public class AdventureChest extends AutomaticFillableChest {

    private final Random random = new Random();
    private final ItemStack[] selectedFillerItems;

    public AdventureChest(Location location) {
        this.chest = (Chest) location.getBlock().getState();

        ItemStack[] fillerItems = getFillerItems();
        ItemStack fillerItem1 = fillerItems[random.nextInt(fillerItems.length)];
        ItemStack fillerItem2 = fillerItems[random.nextInt(fillerItems.length)];

        this.selectedFillerItems = new ItemStack[]{fillerItem1, fillerItem2};
    }

    @Override
    public ChatColor getChestTierChatColor() {
        return ChatColor.GRAY; // Represents Basic tier
    }

    @Override
    public String getChestTierName() {
        return "ADVENTURE";
    }

    @Override
    public ItemStack[] getFillerItems() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Filler items (useful but common)
        items.add(new ItemStack(Material.COBWEB, 1));
        items.add(new ItemStack(Material.FLINT, 2));
        items.add(new ItemStack(Material.FLINT, 2));
        items.add(new ItemStack(Material.GRAVEL, 1));
        items.add(new ItemStack(Material.GRAVEL, 1));
        items.add(new ItemStack(Material.DIRT, 1));
        items.add(new ItemStack(Material.DIRT, 1));
        items.add(new ItemStack(Material.COBBLESTONE, 1));
        items.add(new ItemStack(Material.COBBLESTONE, 1));
        items.add(new ItemStack(Material.BREAD, 1));
        items.add(new ItemStack(Material.COAL, 1));
        items.add(new ItemStack(Material.BOOK, 1));
        items.add(new ItemStack(Material.CARROT, 1));
        items.add(new ItemStack(Material.POTATO, 1));
        items.add(new ItemStack(Material.BEETROOT, 1));
        items.add(new ItemStack(Material.ROTTEN_FLESH, 3));
        items.add(new ItemStack(Material.GUNPOWDER, 1));
        items.add(new ItemStack(Material.PAPER, 1));
        items.add(new ItemStack(Material.MUSHROOM_STEW, 1));
        items.add(new ItemStack(Material.KELP, 4));

        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier1Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Basic Weapons & Tools
        items.add(new ItemStack(Material.STONE_SWORD));
        items.add(new ItemStack(Material.STONE_PICKAXE));
        items.add(new ItemStack(Material.STONE_AXE));
        items.add(new ItemStack(Material.STONE_SHOVEL));
        items.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
        items.add(new ItemStack(Material.IRON_LEGGINGS, 1));
        items.add(new ItemStack(Material.IRON_HELMET, 1));
        items.add(new ItemStack(Material.IRON_BOOTS, 1));

        items.add(new ItemStack(Material.IRON_SWORD, 1));
        items.add(new ItemStack(Material.IRON_AXE, 1));
        items.add(new ItemStack(Material.IRON_SHOVEL, 1));

        ItemStack ironPickaxe = new ItemStack(Material.IRON_PICKAXE, 1);
        enchantItem(ironPickaxe, Enchantment.EFFICIENCY, 2);
        enchantItem(ironPickaxe, Enchantment.UNBREAKING, 2);
        items.add(ironPickaxe);

        ItemStack bow = new ItemStack(Material.BOW, 1);
        enchantItem(bow, Enchantment.POWER, 2);
        items.add(bow);

        // Food & Cooking Ingredients
        items.add(new ItemStack(Material.BEEF, 8));
        items.add(new ItemStack(Material.BREAD, 5));
        items.add(new ItemStack(Material.COOKED_SALMON, 4));
        items.add(new ItemStack(Material.GOLDEN_CARROT, 2));

        // Crafting & Resources
        items.add(new ItemStack(Material.COAL, 6));
        items.add(new ItemStack(Material.IRON_INGOT, 7));
        items.add(new ItemStack(Material.BLAZE_ROD, 2));
        items.add(new ItemStack(Material.SLIME_BALL, 4));
        items.add(new ItemStack(Material.STRING, 6));
        items.add(new ItemStack(Material.GUNPOWDER, 3));
        items.add(new ItemStack(Material.REDSTONE, 10));

        // Utility Items
        items.add(new ItemStack(Material.TORCH, 8));
        items.add(new ItemStack(Material.LEATHER, 6));
        items.add(new ItemStack(Material.FEATHER, 5));
        items.add(new ItemStack(Material.OAK_BOAT, 1)); // For travel in maps with water
        items.add(new ItemStack(Material.ARROW, 12)); // Useful if they have a bow

        items.add(new ItemStack(Material.HONEY_BOTTLE, 4));

        items.add(new ItemStack(Material.GLOWSTONE_DUST, 8));

        // Special Food & Potions
        items.add(new ItemStack(Material.GOLDEN_APPLE, 1));



        return items.toArray(ItemStack[]::new);
    }

    @Override
    public ItemStack[] getTier2Items() {
        ArrayList<ItemStack> items = new ArrayList<>();

        // Tools & Weapons
        ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
        enchantItem(crossbow, Enchantment.QUICK_CHARGE, 1);
        items.add(crossbow);

        ItemStack goldenHelmet = new ItemStack(Material.GOLDEN_HELMET, 1);
        enchantItem(goldenHelmet, Enchantment.PROTECTION, 2);
        items.add(goldenHelmet);

        // Valuable Materials
        items.add(new ItemStack(Material.EMERALD, 6));
        items.add(new ItemStack(Material.DIAMOND, 4));
        items.add(new ItemStack(Material.DIAMOND, 2));
        items.add(new ItemStack(Material.DIAMOND, 1));
        items.add(new ItemStack(Material.DIAMOND, 3));
        items.add(new ItemStack(Material.GOLD_INGOT, 5));
        items.add(new ItemStack(Material.LAPIS_LAZULI, 10));

        // Utility Items
        items.add(new ItemStack(Material.EXPERIENCE_BOTTLE, 8));
        items.add(new ItemStack(Material.ENDER_PEARL, 2));
        items.add(new ItemStack(Material.FIREWORK_ROCKET, 5));

        // Enchanted Fishing Rod (for adventure-style gameplay)
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD, 1);
        enchantItem(fishingRod, Enchantment.LUCK_OF_THE_SEA, 2);
        enchantItem(fishingRod, Enchantment.UNBREAKING, 2);
        items.add(fishingRod);

        // Extra Survival Items
        items.add(new ItemStack(Material.SHIELD, 1));

        // Special Blocks & Survival Tools
        items.add(new ItemStack(Material.TNT, 2)); // Limited but fun!
        items.add(new ItemStack(Material.SPECTRAL_ARROW, 8)); // Useful in combat

        ItemStack enchantedTrident = new ItemStack(Material.TRIDENT);
        enchantItem(enchantedTrident, Enchantment.LOYALTY, 2);
        items.add(enchantedTrident);

        items.add(new ItemStack(Material.HEART_OF_THE_SEA, 1));

        items.add(new ItemStack(Material.TOTEM_OF_UNDYING, 1));

        items.add(new ItemStack(Material.ARMADILLO_SCUTE, 2));

        items.add(new ItemStack(Material.PRISMARINE_CRYSTALS, 5));

        ItemStack enchantedCarrotOnAStick = new ItemStack(Material.CARROT_ON_A_STICK);
        enchantItem(enchantedCarrotOnAStick, Enchantment.MENDING, 1);
        items.add(enchantedCarrotOnAStick);

        items.add(new ItemStack(Material.LEAD, 2));

        return items.toArray(ItemStack[]::new);
    }

    @Override
    protected ItemStack getRandomItem() {
        // get random item
        double randomDouble = random.nextDouble();
        ItemStack itemStack;
        if (randomDouble < 0.05) {
            // 5% chance the item is tier 2 (0.5 item per chest)
            itemStack = getRandomItem(getTier2Items());
        }else if (randomDouble < 0.20) {
            // 15% chance the item is tier 1 (2,75 items per chest)
            itemStack = getRandomItem(getTier1Items());
        }else {
            // 80% chance the item is filler (6 items per chest)
            itemStack = getRandomItem(selectedFillerItems);
        }

        return itemStack;
    }
}