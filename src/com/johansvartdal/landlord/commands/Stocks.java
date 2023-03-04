package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Stocks implements CommandExecutor {

    private Main plugin;

    public Stocks(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("stocks").setExecutor(this);
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
            Tools.printMenuOption(player, "/stocks", "buy [stockname] [amount]");
            Tools.printMenuOption(player, "/stocks", "sell [stockname]");
            Tools.printMenuOption(player, "/stocks", "info [stockname]");
            return true;
        }

        if (args[0].equals("buy")) {
            buyStocks(player, args);
            return true;
        }else if (args[0].equals("sell")) {
            sellStocks(player);
            return true;
        }else if (args[0].equals("info")) {
            info(player, args);
            return true;
        }
        return false;
    }

    private void buyStocks(Player player, String[] args) {
        if (args.length < 3) {
            Tools.tellPlayer(player, "Invalid use of command. Please type /stocks for info about command usage", ChatColor.RED);
            return;
        }
        String stockName = args[1];
        int amount = Integer.parseInt(args[2]);
        StockMarket.AmountWorth amountWorth = StockMarket.getStockWorth(stockName);
        int totalPrice = amountWorth.getWorth()*amount;

        if (!Bank.playerCanAffordTaxFree(player, totalPrice)) {
            Tools.tellPlayer(player, "You cannot afford " + amount + " " + stockName + " stocks for " + totalPrice + " + tax", ChatColor.RED);
            return;
        }

        int platformFee = (int) (totalPrice*0.03);
        Tools.tellPlayer(player, "You just paid a platform fee of " + platformFee + LangDict.getString("currency") + " (3%)");
        Bank.withdrawPlayerWithoutTax(player, totalPrice + platformFee);

        ItemStack itemStack = new ItemStack(Material.PAPER);
        itemStack.setAmount(amount);

        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.setDisplayName(stockName + " stocks");

        itemStack.setItemMeta(meta);
        player.getInventory().addItem(itemStack);

        Tools.tellPlayer(player, "You just purchased " + amount + " " + stockName + " stocks for " + totalPrice + LangDict.getString("currency"), ChatColor.GREEN);
    }

    private void sellStocks(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        // check if stock is valid
        if (!isValidStock(player, itemStack)) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        String displayName = meta.getDisplayName();

        // sell the stocks
        int sellAmount = player.getInventory().getItemInMainHand().getAmount();
        StockMarket.AmountWorth amountWorth = StockMarket.getStockWorth(displayName);

        int sellPrice = amountWorth.getWorth()*sellAmount;
        int platformFee = (int) (sellPrice*0.03);

        Bank.depositPlayerWithoutTax(player, sellPrice + platformFee);

        Tools.tellPlayer(player, "You just paid a platform fee of " + platformFee + LangDict.getString("currency") + " (3%)");
        Tools.tellPlayer(player, "You just sold " + sellAmount + " " + displayName + " for " + sellPrice + LangDict.getString("currency"), ChatColor.GREEN);
        player.getInventory().getItemInMainHand().setAmount(0);
    }

    private Boolean isValidStock(Player player, ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        // make sure meta is not null
        if (meta == null) {
            Tools.tellPlayer(player, "These stocks are invalid!", ChatColor.RED);
            return false;
        }

        // make sure the stocks exist
        String displayName = meta.getDisplayName();
        if (displayName.isEmpty() || !displayName.contains("stocks")) {
            Tools.tellPlayer(player, "These stocks are invalid!", ChatColor.RED);
            return false;
        }

        if (!meta.hasEnchant(Enchantment.MENDING)) {
            Tools.tellPlayer(player, "These stocks are invalid!", ChatColor.RED);
            return false;
        }
        return true;
    }

    private void info(Player player, String[] args) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (args.length > 1) {
            itemStack = new ItemStack(Material.PAPER);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(args[1] + " stocks");
            itemStack.setItemMeta(meta);
        }

        // check if stock is valid
        if (!isValidStock(player, itemStack)) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        String displayName = meta.getDisplayName();

        StockMarket.AmountWorth currentWorth = StockMarket.getStockWorth(displayName);

        Tools.printMenuHeader(player, "INFO");
        Tools.printMenuOption(player, "Stock:", displayName);
        Tools.printMenuOption(player, "Current value:", String.valueOf(currentWorth.getWorth()) + LangDict.getString("currency") + " per stock");

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

        Tools.printMenuOption(player, "Platform-fee:", "3%");
    }
}
