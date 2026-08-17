package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RentableElytra extends RentableItem {

    public RentableElytra(Main plugin) {
        super(plugin);
    }

    @Override
    public String getItemName() {
        return "elytra";
    }

    @Override
    public ItemStack craftItem() {
        // create elytra
        return new ItemStack(Material.ELYTRA);
    }

    @Override
    public int getItemRentPrice() {
        return 1990;
    }

    @Override
    protected int getItemPurchaseFullPrice() {
        return 30000;
    }
}
