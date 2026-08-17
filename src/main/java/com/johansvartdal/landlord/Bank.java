package com.johansvartdal.landlord;

import com.johansvartdal.landlord.chatentities.BankChat;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.events.LandlordEventManager;
import com.johansvartdal.landlord.events.taxevents.ChooseTreasuryEvent;
import com.johansvartdal.landlord.levels.LevelManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.json.simple.JSONObject;

import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class Bank {

    @Getter
    private static double treasuryBalance = 0;
    private static String chancellorUsername = null;

    private static Economy economy = null;

    public static void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            throw new RuntimeException("Could not find Vault plugin!");
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new RuntimeException("Could not find economy plugin!");
        }
        economy = rsp.getProvider();
    }

    /**
     * Checks if the player can afford the amount + tax
     * @param offlinePlayer the player to withdraw
     * @param price the amount to withdraw
     * @return boolean value where true means can afford.
     */
    public static boolean playerCanAfford(OfflinePlayer offlinePlayer, double price) {
        double tax = calculateWithdrawTaxAmount(price);
        double total = price + tax;
        return economy.has(offlinePlayer, total);
    }

    public static void tellPlayerCannotAfford(Player player, String theProduct, double price) {
        double vatAmount = getTaxForPrice(price);
        Tools.tellPlayer(new ErrorChat(), player,
                LangDict.getString(LangDict.YOU_CANNOT_AFFORD_)
                        + theProduct + LangDict.getString("sellItem.for")
                        + Tools.formatCurrency(price)
                        + " + "
                        + Tools.formatCurrency(vatAmount)
                        + LangDict.getString(LangDict._IN_VAT),
                ChatColor.RED);
    }

    public static void tellPlayerTheyNeed(Player player, double price, String forWhat) {
        double vatAmount = getTaxForPrice(price);
        Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_NEED_) +
                Tools.formatCurrency(price) +
                " + " +
                Tools.formatCurrency(vatAmount) +
                LangDict.getString("banking.inVat") + " " +
                forWhat, ChatColor.RED);
    }

    public static String getPriceDisplayWithTax(int price) {
        double vatAmount = getTaxForPrice(price);
        return Tools.formatCurrency(price) +
                " + " +
                Tools.formatCurrency(vatAmount) +
                LangDict.getString("banking.inVat");
    }

    public static double getTaxForPrice(double price) {
        return calculateWithdrawTaxAmount(price);
    }

    public static boolean playerCanAffordTaxFree(OfflinePlayer offlinePlayer, double price) {
        return economy.has(offlinePlayer, price);
    }

    public static void withdrawPlayer(String youJustPaidFor, Player player, double amount) {
        double tax = calculateWithdrawTaxAmount(amount);
        // inform player
        Tools.tellPlayer(new BankChat(), player, LangDict.getString("banking.youJustPaid") + Tools.formatCurrency(amount) + LangDict.getString("sellItem.for") + youJustPaidFor + " + " +
                Tools.formatCurrency(tax) + " (" + getWithdrawTaxPercentDisplay()
                + "%)"+ LangDict.getString("banking.inVat"), ChatColor.GRAY);
        withdrawPlayer(player, amount);
    }

    public static void withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        double tax = calculateWithdrawTaxAmount(amount);
        treasuryBalance += tax;

        double total = amount + tax;

        // withdraw player and save
        economy.withdrawPlayer(offlinePlayer, total);
    }

    public static void withdrawPlayerWithoutTax(OfflinePlayer offlinePlayer, double amount) {
        economy.withdrawPlayer(offlinePlayer, amount);
    }

    public static void depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (!Main.playerDataManager.playerExists(offlinePlayer)) {
            return;
        }

        // store if player was high end so we can compare
        boolean wasHighEnd = isHighEnd(offlinePlayer);
        boolean wasMillionaire = isMillionaire(offlinePlayer);

        // calculate tax amount
        double tax = calculateDepositTaxAmount(offlinePlayer, amount);

        // if the taxed amount for some reason is less than 0. This shouldn't happen.
        if (amount - tax <= 0) {
            tax = 0;
            return;
        }

        // add amount to treasury
        treasuryBalance += tax;

        // add amount to player bank
        economy.depositPlayer(offlinePlayer, amount);

        // inform player
        if (offlinePlayer instanceof Player) {
            Player player = (Player) offlinePlayer;

            Tools.tellPlayer(new BankChat(), player, LangDict.getString("banking.youJustPaid") +
                    Tools.formatCurrency(tax) +
                    " (" + getDepositTaxPercentDisplayForPlayer(player) +
                    "%)" + LangDict.getString("banking.inTax"), ChatColor.GRAY);

            // player just became high, we should tell him
            if (!wasHighEnd && isHighEnd(player)) {
                Tools.tellPlayer(new BankChat(), player, "You just received high end status. You now have access to the lounge. Do /lounge info to read more", ChatColor.GOLD);
                Tools.playSoundForSinglePlayer(player, Sound.BLOCK_NOTE_BLOCK_BELL);
            }

            // player just became millionaire, we should tell him
            if (!wasMillionaire && isMillionaire(offlinePlayer)) {
                Tools.tellPlayer(new BankChat(), player, "Congratulations, you just became a millionaire! Do /millionaire to see available commands.", ChatColor.GOLD);
                Tools.playSoundForSinglePlayer(player, Sound.BLOCK_NOTE_BLOCK_BELL);
            }
        }

        save();
    }

    public static void depositPlayerWithoutTax(OfflinePlayer offlinePlayer, double amount) {
        // store if player was high end so we can compare
        boolean wasHighEnd = isHighEnd(offlinePlayer);
        boolean wasMillionaire = isMillionaire(offlinePlayer);

        // deposit the amount
        economy.depositPlayer(offlinePlayer, amount);

        // if player is online
        if (offlinePlayer instanceof Player) {
            Player player = (Player) offlinePlayer;

            // player just became high, we should tell him
            if (player != null && !wasHighEnd && isHighEnd(player)) {
                Tools.tellPlayer(new BankChat(), player, "You just received high end status. You now have access to the lounge. Do /lounge info to read more", ChatColor.GOLD);
                Tools.playSoundForSinglePlayer(player, Sound.BLOCK_NOTE_BLOCK_BELL);
            }

            // player just became millionaire, we should tell him
            if (player != null && !wasMillionaire && isMillionaire(player)) {
                Tools.tellPlayer(new BankChat(), player, "Congratulations, you just became a millionaire! Do /millionaire to see available commands.", ChatColor.GOLD);
                Tools.playSoundForSinglePlayer(player, Sound.BLOCK_NOTE_BLOCK_BELL);
            }
        }
    }

    public static double getPlayerBalance(OfflinePlayer player) {
        if (Main.playerDataManager.getPlayerData(player) == null) {
            return 0;
        }

        return economy.getBalance(player);
    }

    private static double calculateWithdrawTaxAmount(double price) {
        return price*getWithdrawTaxPercent();
    }

    private static double calculateDepositTaxAmount(OfflinePlayer player, double price) {
        return price*getDepositTaxPercentForPlayer(player);
    }

    private static double getWithdrawTaxPercent() {
        return 0.13;
    }

    public static double getWithdrawTaxPercentDisplay() {
        return getWithdrawTaxPercent()*100;
    }

    private static double getDepositTaxPercentForPlayer(OfflinePlayer player) {
        double balance = getPlayerBalance(player);
        if (balance < 2000) {
            return 0.16;
        }else if (balance < 5000) {
            return 0.17;
        }else if (balance < 10000) {
            return 0.18;
        }else if (balance < 15000) {
            return 0.19;
        }else if (balance < 20000) {
            return 0.20;
        }else if (balance < 40000) {
            return 0.22;
        }else if (balance < 60000) {
            return 0.25;
        }else if (balance < 80000) {
            return 0.30;
        }else if (balance < 100000) {
            return 0.32;
        }else {
            return 0.34;
        }
    }

    private static double getWealthTaxPercentForPlayer(Player player) {
        double balance = getPlayerBalance(player);
        if (balance < 2000) {
            return 0;
        }else if (balance < 5000) {
            return 0;
        }else if (balance < 10000) {
            return 0.02;
        }else if (balance < 15000) {
            return 0.022;
        }else if (balance < 20000) {
            return 0.023;
        }else if (balance < 40000) {
            return 0.025;
        }else if (balance < 60000) {
            return 0.026;
        }else if (balance < 80000) {
            return 0.03;
        }else if (balance < 100000) {
            return 0.035;
        }else {
            return 0.04;
        }
    }

    public static double getDepositTaxPercentDisplayForPlayer(Player player) {
        return getDepositTaxPercentForPlayer(player)*100;
    }

    public static double getWealthTaxPercentDisplayForPlayer(Player player) {
        return getWealthTaxPercentForPlayer(player)*100;
    }

    public static void save() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taxBank", treasuryBalance);
        jsonObject.put("treasuryUsername", chancellorUsername);
        Tools.saveJsonToFile("Bank.json", jsonObject);
    }

    public static void load() {
        JSONObject jsonObject = Tools.loadJson("Bank.json");
        if (jsonObject == null) {
            return;
        }
        treasuryBalance = (double) jsonObject.get("taxBank");
        chancellorUsername = (String) jsonObject.get("treasuryUsername");
    }

    public static void startTaxCollector(Main plugin) {
        Random random = new Random();
        int minute = random.nextInt(30) + 45;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!Main.properties.gameHasStarted()) {
                startTaxCollector(plugin);
                return;
            }
            // collect tax
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!Main.playerDataManager.playerExists(player)) {
                    continue;
                }
                payWealthTaxForPlayer(plugin, player);
                payPropertyTaxForPlayer(plugin, player);
            }

            save();

            startTaxCollector(plugin);
        }, Tools.secToTicks(60*minute));
    }

    private static void payWealthTaxForPlayer(Main plugin, Player player) {
        // calculate wealth tax
        double balance = getPlayerBalance(player);
        double tax = (int) (balance * getWealthTaxPercentForPlayer(player));

        if (payTax(plugin, player, tax)) {
            Tools.tellPlayer(new BankChat(), player, LangDict.getString("banking.youJustPaid") + Tools.formatCurrency(tax) + LangDict.getString("banking.inWealthTax"));
        }
    }

    private static void payPropertyTaxForPlayer(Main plugin, Player player) {
        // get the property tax
        double tax = getPropertyTaxForPlayer(player);

        // pay it
        if (payTax(plugin, player, tax)) {
            Tools.tellPlayer(new BankChat(), player, LangDict.getString("banking.youJustPaid") + Tools.formatCurrency(tax) + LangDict.getString("banking.inPropertyTax") + StaticValues.CHUNK_TAX + ")");
        }
    }

    /**
     * calculate property tax
     * @param player
     * @return
     */
    public static double getPropertyTaxForPlayer(Player player) {
        int chunkPoints = Main.playerDataManager.getPlayerData(player).getOwnedChunks().size();
        double tax = chunkPoints*StaticValues.CHUNK_TAX;
        return tax;
    }

    public static boolean payTax(Main plugin, Player player, double tax) {
        if (tax == 0) {
            return false;
        }

        // send player to jail if it cannot afford tax
        if (!playerCanAffordTaxFree(player, tax)) {
            double bal = getPlayerBalance(player);
            withdrawPlayerWithoutTax(player, bal);
            treasuryBalance += bal;

            JailManager.sendToJail(plugin, player, LangDict.getString("playerEvents.jail.jailReasonTax"), LangDict.getString("playerEvents.jail.jailOutTax"), 60*4);
            save();
            return false;
        }

        treasuryBalance += tax;
        withdrawPlayerWithoutTax(player, tax);
        save();
        return true;
    }

    public static boolean playerIsTreasuryChancellor(Player player) {
        if (chancellorUsername == null) {
            return false;
        }
        return player.getDisplayName().equalsIgnoreCase(chancellorUsername);
    }

    public static void promotePlayerToTreasuryChancellor(Player player) {
        chancellorUsername = player.getDisplayName().toLowerCase();
        save();
    }

    public static boolean aTreasuryChancellorIsChosen() {
        return chancellorUsername != null;
    }

    public static void resignChancellor(Main plugin) {
        chancellorUsername = null;
        save();
        LandlordEventManager.startEvent(new ChooseTreasuryEvent(plugin));
    }

    /**
     * Checks if treasury can afford the inputted price. Treasury does not pay tax
     * @param price The price of what you want to check
     * @return true if treasury can afford
     */
    public static boolean treasuryCanAfford(double price) {
        return treasuryBalance >= price;
    }

    public static void withdrawTreasury(double amount) {
        treasuryBalance -= amount;
        save();
    }

    public static void depositTreasury(double amount) {
        treasuryBalance += amount;
        save();
    }

    /**
     * Removes all money from a player.
     * @param player
     */
    public static void bankruptPlayer(Player player) {
        withdrawPlayerWithoutTax(player, getPlayerBalance(player));
    }

    /**
     * Checks if the player is seen as a high end player. That is players with a balance above 50.000kr.
     * @return True if they are VIP.
     */
    public static boolean isHighEnd(OfflinePlayer offlinePlayer) {
        if (Properties.DEV_CHEAT_MODE) {
            return true;
        }

        double balance = getPlayerBalance(offlinePlayer);

        switch (LevelManager.getCurrentDisplaySeasonNum()) {
            case 1 -> {
                return balance >= 50000;
            }case 2 -> {
                return balance >= 100000;
            }case 3 -> {
                return balance >= 200000;
            }
        }
        return false;
    }

    public static boolean isMillionaire(OfflinePlayer offlinePlayer) {
        if (Properties.DEV_CHEAT_MODE) {
            return true;
        }
        double balance = getPlayerBalance(offlinePlayer);
        return balance >= 1000000;
    }
}
