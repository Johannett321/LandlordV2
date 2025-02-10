package com.johansvartdal.landlord.commands;

import com.johansvartdal.landlord.*;
import com.johansvartdal.landlord.levels.LevelManager;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.stocks.Stock;
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
        if (Tools.stateNotNormal(sender)) {
            Tools.tellPlayer(sender, LangDict.getString(LangDict.CMD_NOT_NOW), ChatColor.RED);
            return true;
        }

        Player player = (Player) sender;

        if (!LevelManager.featureUnlocked("stocks")) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.CMD_NOT_UNLOCKED), ChatColor.RED);
            return true;
        }

        if (args.length == 0) {
            Tools.printMenuHeader(player, LangDict.getString("generalSentenceParts.commands"));
            Tools.printMenuOption(player, "/stocks", "buy [stockname] [amount]");
            Tools.printMenuOption(player, "/stocks", "sell [stockname]");
            Tools.printMenuOption(player, "/stocks", "info [stockname]");
            Tools.printMenuOption(player, "/stocks", "list");
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
        }else if (args[0].equals("list")) {
            showList(player);
            return true;
        }
        return false;
    }

    private void showList(Player player) {
        Tools.printMenuHeader(player, LangDict.getString("stocks.stocksTitle"));
        for (Stock stock : StockManager.getAllStocks()) {
            Tools.printMenuOption(player, stock.getDisplayName() + " (" + stock.getID() + "):", Tools.formatCurrency(stock.getCurrentPrice()));
        }
    }

    private void buyStocks(Player player, String[] args) {
        if (args.length < 3) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("commandResponses.errorMessages.invalidCommandUse"), ChatColor.RED);
            return;
        }
        String stockName = args[1];
        Stock stock = StockManager.getStockByID(stockName);
        if (stock == null) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("stocks.couldNotFindStockWithName") + stockName, ChatColor.RED);
            return;
        }

        // Calculate stock price
        int amount = Integer.parseInt(args[2]);
        int price = stock.getCurrentPrice();
        int sumStockPrices = price*amount;

        // Set platform fee. (Min 5kr, max 3%)
        int platformFee = (int) (sumStockPrices*0.03);
        if (platformFee < 5) {
            platformFee = 5;
        }

        // Total price
        int totalPrice = sumStockPrices + platformFee;

        // Can player afford it
        if (!Bank.playerCanAffordTaxFree(player, totalPrice)) {
            Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_CANNOT_AFFORD_) + amount + " " + stockName + LangDict.getString("stocks.stocksFor") + Tools.formatCurrency(totalPrice) + LangDict.getString("stocks.inclPlatform"), ChatColor.RED);
            return;
        }

        // Withdraw the player
        Bank.withdrawPlayerWithoutTax(player, totalPrice);

        // Create the stock item
        ItemStack itemStack = new ItemStack(Material.PAPER);
        itemStack.setAmount(amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.setDisplayName(stockName);
        itemStack.setItemMeta(meta);

        Tools.tellPlayer(player, LangDict.getString("justBought") + amount + " " + stockName + LangDict.getString("stocks.stocksFor") + Tools.formatCurrency(totalPrice) + LangDict.getString("stocks.inclPlatform"), ChatColor.GREEN);

        // Give the stock item to the player
        Tools.givePlayerItemOrDrop(player, itemStack, true);
    }

    private void sellStocks(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        // check if stock is valid
        if (!isValidStock(player, itemStack)) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        String displayName = meta.getDisplayName();
        Stock stock = StockManager.getStockByID(displayName);

        // sell the stocks
        int sellAmount = player.getInventory().getItemInMainHand().getAmount();
        int pricePerStock = stock.getCurrentPrice();

        int sellPrice = pricePerStock*sellAmount;
        int platformFee = (int) (sellPrice*0.03);

        Bank.depositPlayerWithoutTax(player, sellPrice);
        Bank.withdrawPlayerWithoutTax(player, platformFee);

        Tools.tellPlayer(player, LangDict.getString("sellItem.youJustSold") + sellAmount + " " + displayName + LangDict.getString("sellItem.for") + Tools.formatCurrency(sellPrice) + LangDict.getString("stocks.andPaid") + Tools.formatCurrency(platformFee) + LangDict.getString("stocks.inPlatformFee"), ChatColor.GREEN);
        player.getInventory().getItemInMainHand().setAmount(0);
    }

    private Boolean isValidStock(Player player, ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        // make sure meta is not null
        if (meta == null) {
            Tools.tellPlayer(player, LangDict.getString("stocks.stocksInvalid"), ChatColor.RED);
            return false;
        }

        if (!meta.hasEnchant(Enchantment.MENDING)) {
            Tools.tellPlayer(player, LangDict.getString("stocks.stocksInvalid"), ChatColor.RED);
            return false;
        }

        // make sure the stocks exist
        String displayName = meta.getDisplayName();
        Stock stock = StockManager.getStockByID(displayName);
        if (stock == null) {
            Tools.tellPlayer(player, LangDict.getString("stocks.stocksInvalid"), ChatColor.RED);
            return false;
        }
        return true;
    }

    private void info(Player player, String[] args) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (args.length > 1) {
            itemStack = new ItemStack(Material.PAPER);
            itemStack.setAmount(1);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(args[1]);
            meta.addEnchant(Enchantment.MENDING, 1, false);
            itemStack.setItemMeta(meta);
        }

        // check if stock is valid
        if (!isValidStock(player, itemStack)) {
            return;
        }

        ItemMeta meta = itemStack.getItemMeta();
        String displayName = meta.getDisplayName();
        Stock stock = StockManager.getStockByID(displayName);

        int price = stock.getCurrentPrice();

        Tools.printMenuHeader(player, "INFO");
        Tools.printMenuOption(player, LangDict.getString("stocks.stock"), stock.getDisplayName() + " (" + stock.getID() + ")");
        Tools.printMenuOption(player, LangDict.getString("generalSentenceParts.description"), stock.getDescription());
        Tools.printMenuOption(player, LangDict.getString("sellItem.currentValue"), Tools.formatCurrency(price) + LangDict.getString("stocks.perStock"));

        int worthAt1 = stock.getPriceAtMillis(System.currentTimeMillis()-(1000*60));
        int worthAt2 = stock.getPriceAtMillis(System.currentTimeMillis()-(1000*60*2));
        int worthAt3 = stock.getPriceAtMillis(System.currentTimeMillis()-(1000*60*3));
        int worthAt4 = stock.getPriceAtMillis(System.currentTimeMillis()-(1000*60*4));
        Tools.printMenuOption(player, LangDict.getString("stocks.values1To4Mins"), worthAt1 + ", " + worthAt2 + ", " + worthAt3 + ", " + worthAt4);

        int worthAt15 = stock.getPriceAtMillis(System.currentTimeMillis()-(1000*60*15));
        int worthAt30 = stock.getPriceAtMillis(System.currentTimeMillis()-(1000*60*30));
        int worthAt45 = stock.getPriceAtMillis(System.currentTimeMillis()-(1000*60*45));
        int worthAt60 = stock.getPriceAtMillis(System.currentTimeMillis()-(1000*60*60));
        Tools.printMenuOption(player, LangDict.getString("stocks.values15To60Mins"), worthAt15 + ", " + worthAt30 + ", " + worthAt45 + ", " + worthAt60);

        Tools.printMenuOption(player, LangDict.getString("stocks.platformFee"), "3%");
    }
}
