package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.InfoChat;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class Level implements LevelInterface {

    protected Main plugin;
    private final int seasonNumber;
    private final int levelNumber;
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

    public void donateItem(Player player, ItemStack itemStack) {
        for (int i = 0; i < remainingItems.size(); i++) {
            if (remainingItems.get(i).getType() == itemStack.getType()) {
                int onHand = itemStack.getAmount();
                int required = remainingItems.get(i).getAmount();

                if (onHand >= required) {
                    onHand -= required;
                    required = 0;
                }else {
                    required -= onHand;
                    onHand = 0;
                }

                if (required > 0) {
                    remainingItems.get(i).setAmount(required);
                    Tools.tellPlayer(new InfoChat(), player, LangDict.getString("youJustDonated") + itemStack.getAmount() + " " + Tools.getDisplayNameOfItem(itemStack) + " " + LangDict.getString("toCommunity"), ChatColor.GREEN);
                }else {
                    Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_GUITAR);
                    remainingItems.remove(i);

                    if (remainingItems.size() == 0) {
                        God.speak(LangDict.getString("itemDonationsComplete"));
                    }else {
                        God.speak(player.getDisplayName() + LangDict.getString("justDonated") + Tools.getDisplayNameOfItem(itemStack) + LangDict.getString("toCommunity"));
                    }
                }

                player.getInventory().getItemInMainHand().setAmount(onHand);
                LevelManager.save();
                break;
            }
        }
    }

    public void setRemainingItems(ArrayList<ItemStack> remainingItems) {
        this.remainingItems = remainingItems;
    }

    public int getDisplaySeasonNumber() {
        return seasonNumber;
    }

    public ItemStack getBook() {
        return null;
    }
}
