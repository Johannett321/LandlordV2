package com.johansvartdal.landlord.mysterychest;

import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

@Slf4j
public abstract class AutomaticFillableChest {

    private final Random random = new Random();
    protected Chest chest;

    public Location spawnChest() {
        return spawnChest(getRandomLocationForChest());
    }

    public Location spawnChest(Location location) {
        Block block = location.getBlock();
        block.setType(Material.CHEST);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playEffect(location, Effect.ELECTRIC_SPARK, null);
        }

        this.chest = (Chest) block.getState();

        return block.getLocation();
    }

    private Location getRandomLocationForChest() {
        Chunk tradeChunk = Main.tradeCenter.getLocation().getChunk();
        int randomXOffset = random.nextInt(16);
        int randomZOffset = random.nextInt(16);

        int x = (tradeChunk.getX() * 16) + randomXOffset;
        int z = (tradeChunk.getZ() * 16) + randomZOffset;
        log.info("Spawned mystery chest at {}, {}", x, z);
        Location location = Tools.highestStandingPoint(new Location(tradeChunk.getWorld(), x, 0, z));
        location.setY(location.getY() - 1);
        return location;
    }

    public void fillChest() {
        chest.getInventory().clear();
        for (int i = 0; i < chest.getInventory().getSize(); i++) {
            double randomDouble = random.nextDouble();
            if (randomDouble > 0.6) {
                continue; // 60 % chance the slot is empty
            }

            // get a randomItem
            ItemStack itemStack = getRandomItem();

            // place item in chest
            chest.getInventory().setItem(i, itemStack);
        }
    }

    protected ItemStack getRandomItem() {
        // get random item
        double randomDouble = random.nextDouble();
        ItemStack itemStack;
        if (randomDouble < 0.1) {
            // 10% chance the item is tier 2 (1 item per chest)
            itemStack = getRandomItem(getTier2Items());
        }else if (randomDouble < 0.4) {
            // 30% chance the item is tier 1 (3 items per chest)
            itemStack = getRandomItem(getTier1Items());
        }else {
            // 60% chance the item is filler (6 items per chest)
            itemStack = getRandomItem(getFillerItems());
        }

        return itemStack;
    }

    protected ItemStack getRandomItem(ItemStack[] fromItems) {
        // get random item
        int randomIndex = random.nextInt(fromItems.length);
        ItemStack itemStack = fromItems[randomIndex];

        // update to random amount
        int randomAmount = random.nextInt(itemStack.getAmount()) + 1;
        itemStack.setAmount(randomAmount);

        return itemStack;
    }

    public abstract ChatColor getChestTierChatColor();
    public abstract String getChestTierName();

    public abstract ItemStack[] getFillerItems();
    public abstract ItemStack[] getTier1Items();
    public abstract ItemStack[] getTier2Items();
}
