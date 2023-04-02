package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RentedElytra extends RentableItem {

    public RentedElytra(Main plugin) {
        super(plugin);
    }

    @Override
    public String getItemName() {
        return "elytra";
    }

    @Override
    public ItemStack craftItem() {
        // create elytra
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        elytra.addEnchantment(Enchantment.DURABILITY, 3);
        elytra.addEnchantment(Enchantment.VANISHING_CURSE, 1);
        ItemMeta itemMeta = elytra.getItemMeta();
        if (itemMeta instanceof Damageable damageable) {
            damageable.setDamage(elytra.getType().getMaxDurability()-(560/3)); //TODO: MAYBE ADD UNBREAKING 3 AND LOWER THE 140
            damageable.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            elytra.setItemMeta(damageable);
            itemMeta.setDisplayName("RENTED");
        }
        if (itemMeta instanceof Repairable repairable) {
            repairable.setRepairCost(98);
            elytra.setItemMeta(repairable);
        }
        elytra.setAmount(1);
        return elytra;
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
