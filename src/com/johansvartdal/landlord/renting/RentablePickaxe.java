package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.LevelManager;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RentablePickaxe extends RentableItem {

    public RentablePickaxe(Main plugin) {
        super(plugin);
    }

    @Override
    public String getItemName() {
        return "pickaxe";
    }

    @Override
    public ItemStack craftItem() {
        // create itemStack
        ItemStack itemStack = new ItemStack(Material.IRON_PICKAXE);
        itemStack.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
        itemStack.addEnchantment(Enchantment.DIG_SPEED, 3);

        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
            itemStack.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2);
            itemStack.addEnchantment(Enchantment.DIG_SPEED, 4);
        }
        return itemStack;
    }

    @Override
    public int getItemRentPrice() {
        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            return 3990;
        }
        return 1390;
    }

    @Override
    protected int getItemPurchaseFullPrice() {
        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            return 30000;
        }
        return 10000;
    }
}
