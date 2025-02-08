package com.johansvartdal.landlord.renting;

import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class RentableShovel extends RentableItem {

    public RentableShovel(Main plugin) {
        super(plugin);
    }

    @Override
    public String getItemName() {
        return "shovel";
    }

    @Override
    public ItemStack craftItem() {
        // create itemStack
        ItemStack itemStack = new ItemStack(Material.IRON_SHOVEL);
        itemStack.addEnchantment(Enchantment.EFFICIENCY, 3);

        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            itemStack = new ItemStack(Material.DIAMOND_SHOVEL);
            itemStack.addEnchantment(Enchantment.EFFICIENCY, 4);
        }
        return itemStack;
    }

    @Override
    public int getItemRentPrice() {
        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            return 3490;
        }
        return 1190;
    }

    @Override
    protected int getItemPurchaseFullPrice() {
        if (LevelManager.featureUnlocked("rent_diamond_tools")) {
            return 30000;
        }
        return 10000;
    }
}
