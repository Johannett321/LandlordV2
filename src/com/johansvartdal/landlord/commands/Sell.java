package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
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
        Player player = (Player) sender;

        if (!Main.properties.gameStateIsNormal()) {
            Tools.tellPlayer(player, "You cannot run this command at the moment", ChatColor.RED);
            return true;
        }

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
        StockMarket.AmountWorth currentWorth = StockMarket.getWorth(itemStack.getType());

        Tools.printMenuHeader(player, "INFO");
        Tools.printMenuOption(player, "Item:", itemStack.getType().name());
        Tools.printMenuOption(player, "Current value:", String.valueOf(currentWorth.getWorth()));

        int worthAt1 = currentWorth.getWorthAtTime(System.currentTimeMillis()-(1000*60));
        int worthAt2 = currentWorth.getWorthAtTime(System.currentTimeMillis()-(1000*60*2));
        int worthAt3 = currentWorth.getWorthAtTime(System.currentTimeMillis()-(1000*60*3));
        int worthAt4 = currentWorth.getWorthAtTime(System.currentTimeMillis()-(1000*60*4));
        Tools.printMenuOption(player, "Values (1, 2, 3, 4) mins:", worthAt1 + ", " + worthAt2 + ", " + worthAt3 + ", " + worthAt4);

        int worthAt15 = currentWorth.getWorthAtTime(System.currentTimeMillis()-(1000*60*15));
        int worthAt30 = currentWorth.getWorthAtTime(System.currentTimeMillis()-(1000*60*30));
        int worthAt45 = currentWorth.getWorthAtTime(System.currentTimeMillis()-(1000*60*45));
        int worthAt60 = currentWorth.getWorthAtTime(System.currentTimeMillis()-(1000*60*60));
        Tools.printMenuOption(player, "Values (15, 30, 45, 60) mins:", worthAt15 + ", " + worthAt30 + ", " + worthAt45 + ", " + worthAt60);

        Tools.printMenuOption(player, "Tax percentage:", Bank.getDepositTaxPercentDisplayForPlayer(player) + "%");
    }

    private void sellHand(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Material itemType = itemStack.getType();
        StockMarket.AmountWorth amountWorth = StockMarket.getWorth(itemType);

        if (amountWorth.getWorth() == 0) {
            Tools.tellPlayer(player, "You cannot sell the current item in your hand!", ChatColor.RED);
        }

        if (itemStack.getAmount() < amountWorth.getAmountNeeded()) {
            Tools.tellPlayer(player, "You need at least " + amountWorth.getAmountNeeded() + " " + itemType.name() + " to sell.", ChatColor.RED);
            return;
        }

        // SELL
        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-amountWorth.getAmountNeeded());
        Bank.depositPlayer(player, amountWorth.getWorth());

        Tools.tellPlayer(player, "You just sold " + amountWorth.getAmountNeeded() + " " + itemType.name() + " for " + amountWorth.getWorth() + LangDict.getString("currency"));
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK,1, 0);
    }

    private void sellAll(Player player) {
        int amountSold = 0;
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        Material itemType = mainHandItem.getType();

        StockMarket.AmountWorth amountWorth = StockMarket.getWorth(itemType);

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
            Tools.tellPlayer(player, "You need at least " + amountWorth.getAmountNeeded() + " " + itemType.name() + " to sell.", ChatColor.RED);
            return;
        }

        // TELL PLAYER
        Bank.depositPlayer(player, amountToDeposit);
        Tools.tellPlayer(player, "You just sold " + (amountSold * amountWorth.getAmountNeeded()) + " " + itemType.name() + " for " + (amountSold * amountWorth.getWorth()), ChatColor.GREEN);
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK,1, 0);
    }
}
