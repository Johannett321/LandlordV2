package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.InfoChat;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public abstract class Level implements LevelInterface {

    protected Main plugin;
    private final int seasonNumber;
    private final int levelNumber;
    @Setter
    private ArrayList<ItemStack> remainingItems;


    public Level(Main plugin, int seasonNumber, int levelNumber) {
        this.plugin = plugin;
        this.seasonNumber = seasonNumber;
        this.levelNumber = levelNumber;
        this.remainingItems = getRequiredItemsForNextLevel();
    }

    public ArrayList<ItemStack> getRemainingItemsForNextLevel() {
        return remainingItems;
    }

    public int getLevelNumber() {
        return levelNumber-1;
    }

    public int getDisplayLevelNumber() {
        return levelNumber;
    }

    public void updateRequiredItem(Material type, int newRequired) {
        System.out.println("Step3: " + type.name());
        Optional<ItemStack> optionalItemStack = remainingItems.stream().filter(itemStack -> itemStack.getType().equals(type)).findFirst();
        if (optionalItemStack.isEmpty()) {
            System.out.println("Cannot find " + type.name() + " in required items!");
            return;
        }

        // remove if 0 or less
        if (newRequired <= 0) {
            System.out.println("Removing " + type.name() + " as last was filled!");
            remainingItems.remove(optionalItemStack.get());
            return;
        }

        // update amount
        optionalItemStack.get().setAmount(newRequired);
    }

    public int getDisplaySeasonNumber() {
        return seasonNumber;
    }

    public abstract Book getBook();
}
