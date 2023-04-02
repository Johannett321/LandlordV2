package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RentedPickaxe extends RentableItem {

    public RentedPickaxe(Main plugin) {
        super(plugin);
    }

    @Override
    public String getItemName() {
        return "pickaxe";
    }

    @Override
    public ItemStack craftItem() {
        // create pickaxe
        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 3);
        pickaxe.addEnchantment(Enchantment.DURABILITY, 3);
        pickaxe.addEnchantment(Enchantment.VANISHING_CURSE, 1);
        ItemMeta itemMeta = pickaxe.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(pickaxe.getType().getMaxDurability()-(560/3)); //TODO: MAYBE ADD UNBREAKING 3 AND LOWER THE 140
            damageable.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            pickaxe.setItemMeta(damageable);
            itemMeta.setDisplayName("Rented pickaxe");
        }
        if (itemMeta instanceof Repairable repairable) {
            repairable.setRepairCost(98);
            pickaxe.setItemMeta(repairable);
        }
        pickaxe.setAmount(1);
        return pickaxe;
    }

    @Override
    public int getItemRentPrice() {
        return 10;
    }

    @Override
    protected int getItemPurchaseFullPrice() {
        return 100;
    }
}
