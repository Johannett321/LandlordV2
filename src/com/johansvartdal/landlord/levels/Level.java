package com.johansvartdal.landlord.levels;

import com.johansvartdal.landlord.God;
import com.johansvartdal.landlord.LandlordEventInterface;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class Level implements LevelInterface {

    protected Main plugin;
    private int levelNumber;
    private ArrayList<ItemStack> remainingItems;


    public Level(Main plugin, int levelNumber) {
        this.plugin = plugin;
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

                God.speak(player.getDisplayName() + Main.langDict.getString("justDonated") + Tools.getDisplayNameOfItem(itemStack) + Main.langDict.getString("toCommunity"));

                if (required > 0) {
                    remainingItems.get(i).setAmount(required);
                }else {
                    remainingItems.remove(i);
                }

                player.getInventory().getItemInMainHand().setAmount(onHand);
                Main.levelManager.save();

                if (remainingItems.size() == 0) {
                    God.speak("That was actually the last item required! Are you ready to upgrade?");
                }
                break;
            }
        }
    }

    public void load() {

    }
}
