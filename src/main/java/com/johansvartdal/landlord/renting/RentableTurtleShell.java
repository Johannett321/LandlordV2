package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RentableTurtleShell extends RentableItem {

    public RentableTurtleShell(Main plugin) {
        super(plugin);
    }

    @Override
    public String getItemName() {
        return "turtle_shell";
    }

    @Override
    public ItemStack craftItem() {
        // create elytra
        return new ItemStack(Material.TURTLE_HELMET);
    }

    @Override
    public int getItemRentPrice() {
        return 790;
    }

    @Override
    protected int getItemPurchaseFullPrice() {
        return 30000;
    }
}
