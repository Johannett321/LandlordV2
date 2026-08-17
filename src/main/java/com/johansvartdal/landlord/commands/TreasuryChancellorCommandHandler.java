package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.events.LandlordEventManager;
import com.johansvartdal.landlord.events.taxevents.HasteEvent;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TreasuryChancellorCommandHandler {

    private Main plugin;

    public TreasuryChancellorCommandHandler(Main plugin) {
        this.plugin = plugin;
    }

    /*
    ------------------------------------------------------ CHANCELLOR COMMANDS ------------------------------------------------------
     */

    public Boolean executeChancellorCommand(Player player, String[] args) {
        if (!Main.properties.gameStateIsNormal()) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
            return buyCommand(args, player);
        }else if (args[0].equalsIgnoreCase("withdraw") && LevelManager.featureUnlocked("treasurywithdraw")) {
            return withdrawCommand(args, player);
        }
        return false;
    }

    private boolean buyCommand(String[] args, Player player) {
        if (args[1].equalsIgnoreCase("haste") && LevelManager.featureUnlocked("treasuryhaste")) {
            return buyHaste(player);
        }else if (args[1].equalsIgnoreCase("chunkdiscount") && LevelManager.featureUnlocked("treasurychunkdiscount")) {
            return buyChunkDiscount(player);
        }else if (args[1].equalsIgnoreCase("donations") && LevelManager.featureUnlocked("treasurydonations")) {
            return buyDonations(player);
        }else if (args[1].equalsIgnoreCase("mysterychest") && LevelManager.featureUnlocked("treasurymysterychest")) {
            return buyMysteryChest(player);
        }

        return false;
    }

    private boolean buyDonations(Player player) {
        // make sure the treasury can afford
        int donationsPrice = StaticValues.TREASURY_DONATIONS_BASE_PRICE + LevelManager.getNumberOfRemainingItemsTotal() * StaticValues.TREASURY_DONATIONS_PRICE_PER_UNIT;
        if (!Bank.treasuryCanAfford(donationsPrice)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.treasuryCannotAffordDonations") + Tools.formatCurrency(donationsPrice));
            return true;
        }

        Bank.withdrawTreasury(donationsPrice);

        ArrayList<ItemStack> listOfRemainingItems = LevelManager.getListOfRemainingItems();
        for (int i = 0; i < listOfRemainingItems.size(); i++) {
            listOfRemainingItems.get(i).setAmount(listOfRemainingItems.get(i).getAmount()/2);
        }
        LevelManager.save();

        God.speak(LangDict.getString("treasury.broadcastBoughtDonations"));
        return true;
    }

    private boolean buyMysteryChest(Player player) {
        if (!Bank.treasuryCanAfford(StaticValues.TREASURY_MYSTERY_CHEST_PRICE)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.treasuryCannotAffordDonations") + Tools.formatCurrency(StaticValues.TREASURY_MYSTERY_CHEST_PRICE));
            return true;
        }

        Bank.withdrawTreasury(StaticValues.TREASURY_MYSTERY_CHEST_PRICE);

        Main.chestManager.orderMysteryChestInTrade();
        return true;
    }

    private boolean buyChunkDiscount(Player player) {
        // make sure the treasury can afford
        if (!Bank.treasuryCanAfford(StaticValues.TREASURY_CHUNK_DISCOUNT_PRICE)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.treasuryCannotAffordChunkDiscount") + Tools.formatCurrency(StaticValues.TREASURY_CHUNK_DISCOUNT_PRICE));
            return true;
        }

        // make sure chunk discount not exceeding 70% discount
        double existingDiscountPercentagePoint = Main.properties.getChunkDiscountPercentPoint();
        if (existingDiscountPercentagePoint >= 0.7) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.noMoreChunkDiscount"));
            return true;
        }

        Bank.withdrawTreasury(StaticValues.TREASURY_CHUNK_DISCOUNT_PRICE);

        double newDiscountPercentagePoint = existingDiscountPercentagePoint + .05;
        Main.properties.setChunkDiscountPercentPoint(newDiscountPercentagePoint);
        Main.properties.save();

        God.speak(LangDict.getString("treasury.broadcastChunkDiscount") + (newDiscountPercentagePoint*100) + "%");
        return true;
    }

    private boolean buyHaste(Player player) {
        // make sure the treasury can afford
        if (!Bank.treasuryCanAfford(StaticValues.TREASURY_HASTE_PRICE)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.treasuryCannotAffordHaste") + Tools.formatCurrency(StaticValues.TREASURY_HASTE_PRICE));
            return true;
        }

        Bank.withdrawTreasury(StaticValues.TREASURY_HASTE_PRICE);

        LandlordEventManager.startEvent(new HasteEvent(plugin));
        return true;
    }

    private boolean withdrawCommand(String[] args, Player player) {
        int withdrawalAmountPerPlayer = 4000;
        int price = Main.properties.getNumberOfPlayers() * withdrawalAmountPerPlayer + StaticValues.TREASURY_WITHDRAW_PRICE;

        // can treasury afford it?
        if (!Bank.treasuryCanAfford(price)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("treasury.treasuryCannotAffordWithdrawal") + Tools.formatCurrency(price));
            return true;
        }

        // deposit players
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            Bank.depositPlayer(offlinePlayer, withdrawalAmountPerPlayer);
         }

        // tell players
        God.speak(LangDict.getString("treasury.treasuryOrderedWithdrawal") + Tools.formatCurrency(withdrawalAmountPerPlayer) + LangDict.getString("treasury.treasuryWithdrawalUnfortunately") + Tools.formatCurrency(StaticValues.TREASURY_WITHDRAW_PRICE) +
                LangDict.getString("treasury.lostDuringWithdrawal"));
        return true;
    }

    public void printAvailableCommands(Player player) {
        int donationsPrice = StaticValues.TREASURY_DONATIONS_BASE_PRICE + LevelManager.getNumberOfRemainingItemsTotal() * StaticValues.TREASURY_DONATIONS_PRICE_PER_UNIT;

        Tools.printMenuHeader(player, LangDict.getString("generalSentenceParts.commands"));
        if (LevelManager.featureUnlocked("treasuryhaste")) {
            Tools.printMenuOption(player, "/treasury", "buy haste " + ChatColor.GOLD + "(" + Tools.formatCurrency(StaticValues.TREASURY_HASTE_PRICE) + ")");
        }

        if (LevelManager.featureUnlocked("treasurychunkdiscount")) {
            Tools.printMenuOption(player, "/treasury", "buy chunkdiscount " + ChatColor.GOLD + "(" + Tools.formatCurrency(StaticValues.TREASURY_CHUNK_DISCOUNT_PRICE) + ")");
        }

        if (LevelManager.featureUnlocked("treasurydonations")) {
            Tools.printMenuOption(player, "/treasury", "buy donations " + ChatColor.GOLD + "(" + Tools.formatCurrency(donationsPrice) + ")");
        }

        if (LevelManager.featureUnlocked("treasurymysterychest")) {
            Tools.printMenuOption(player, "/treasury", "buy mysterychest " + ChatColor.GOLD + "(" + Tools.formatCurrency(StaticValues.TREASURY_MYSTERY_CHEST_PRICE) + ")");
        }

        if (LevelManager.featureUnlocked("treasurywithdraw")) {
            Tools.printMenuOption(player, "/treasury", "withdraw "+ ChatColor.GOLD + "(" + Tools.formatCurrency(StaticValues.TREASURY_WITHDRAW_PRICE) + ")");
        }
    }
}
