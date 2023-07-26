package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.LevelManager;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RentableSword extends RentableItem {

    public RentableSword(Main plugin) {
        super(plugin);
    }

    @Override
    public String getItemName() {
        return "sword";
    }

    @Override
    public ItemStack craftItem() {
        // create elytra
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        itemStack.addEnchantment(Enchantment.KNOCKBACK, 1);
        itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 2);

        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            itemStack = new ItemStack(Material.DIAMOND_SWORD);
            itemStack.addEnchantment(Enchantment.KNOCKBACK, 2);
            itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 4);
        }
        return itemStack;
    }

    @Override
    public int getItemRentPrice() {
        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            return 1990;
        }
        return 790;
    }

    @Override
    protected int getItemPurchaseFullPrice() {
        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            return 30000;
        }
        return 10000;
    }
}
