package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Sell implements CommandExecutor {

    Main plugin;

    public Sell(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("sell").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            Tools.printMenuHeader(player, "Commands");
            Tools.printMenuOption(player, "/Sell", "now");
            Tools.printMenuOption(player, "/Sell", "all");
            Tools.printMenuOption(player, "/Sell", "info");
            return true;
        }

        if (args[0].equals("now")) {
            sellHand(player);
            return true;
        }else if (args[0].equals("all")) {
            sellAll(player);
            return true;
        }else if (args[0].equals("info")) {
            sellInfo(player);
            return true;
        }
        return false;
    }

    private void sellInfo(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        BuySellManager.AmountWorth currentWorth = BuySellManager.getItemValue(itemStack.getType());
        if (currentWorth.getWorth() == 0) {
            Tools.printMenuHeader(player, "INFO");
            Tools.printMenuOption(player, "Error:", LangDict.getString("sellItem.itemCannotBeSold"));
            return;
        }

        Tools.printMenuHeader(player, "INFO");
        Tools.printMenuOption(player, LangDict.getString("sellItem.item"), itemStack.getType().name());
        Tools.printMenuOption(player, LangDict.getString("sellItem.currentValue"), String.valueOf(currentWorth.getWorth()));
        Tools.printMenuOption(player, LangDict.getString("sellItem.requiredAmount"), String.valueOf(currentWorth.getAmountNeeded()));
        Tools.printMenuOption(player, LangDict.getString("banking.currentVAT"), Bank.getDepositTaxPercentDisplayForPlayer(player) + "%");
    }

    private void sellHand(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Material itemType = itemStack.getType();

        if (itemType == Material.PAPER) {
            // STOCK!!!
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("sellItem.cannotSellStock"), ChatColor.RED);
            return;
        }

        BuySellManager.AmountWorth amountWorth = BuySellManager.getItemValue(itemType);

        if (amountWorth.getWorth() == 0) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("sellItem.itemCannotBeSold"), ChatColor.RED);
            return;
        }

        if (itemStack.getAmount() < amountWorth.getAmountNeeded()) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_NEED) + amountWorth.getAmountNeeded() + " " + itemType.name() + LangDict.getString("sellItem.toSell"), ChatColor.RED);
            return;
        }

        // SELL
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-amountWorth.getAmountNeeded());
        Bank.depositPlayer(player, amountWorth.getWorth());

        Tools.tellPlayer(player, LangDict.getString("sellItem.youJustSold") + amountWorth.getAmountNeeded() + " " + itemType.name() + LangDict.getString("sellItem.for") + amountWorth.getWorth() + LangDict.getString("banking.currency"));
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK,1, 0);
    }

    private void sellAll(Player player) {
        int amountSold = 0;
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        Material itemType = mainHandItem.getType();

        BuySellManager.AmountWorth amountWorth = BuySellManager.getItemValue(itemType);

        int amountToDeposit = 0;
        for (int i = 0; i < 9; i++) { //loop through hotbar to see if user got more to sell
            ItemStack itemInCurrentSlot = player.getInventory().getItem(i);
            if (itemInCurrentSlot == null || itemInCurrentSlot.getType() != itemType) {
                continue;
            }

            if (itemInCurrentSlot.getAmount() > amountWorth.getAmountNeeded()) {
                player.getInventory().getItem(i).setAmount(itemInCurrentSlot.getAmount()-amountWorth.getAmountNeeded());
                amountToDeposit += amountWorth.getWorth();
                amountSold = amountSold + 1;
            }else if (itemInCurrentSlot.getAmount() == amountWorth.getAmountNeeded()) {
                player.getInventory().getItem(i).setAmount(0);
                amountToDeposit += amountWorth.getWorth();
                amountSold = amountSold + 1;
            }
        }

        if (amountSold == 0) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_NEED) + amountWorth.getAmountNeeded() + " " + itemType.name() + LangDict.getString("sellItem.toSell"), ChatColor.RED);
            return;
        }

        // TELL PLAYER
        Bank.depositPlayer(player, amountToDeposit);
        Tools.tellPlayer(player, LangDict.getString("sellItem.youJustSold") + (amountSold * amountWorth.getAmountNeeded()) + " " + itemType.name() + LangDict.getString("sellItem.for") + (amountSold * amountWorth.getWorth()), ChatColor.GREEN);
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK,1, 0);
    }
}
