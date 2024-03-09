package com.johansvartdal.landlord;

import com.johansvartdal.landlord.chatentities.BankChat;
import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.events.taxevents.ChooseTreasuryEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.Random;

public class Bank {

    private static int taxBank = 0;
    private static String chancellorUsername = null;

    /**
     * Checks if the player can afford the amount + tax
     * @param player the player to withdraw
     * @param price the amount to withdraw
     * @return boolean value where true means can afford.
     */
    public static boolean playerCanAfford(Player player, int price) {
        return playerCanAfford(Main.playerDataManager.getPlayerData(player), price);
    }

    /**
     * Checks if the player can afford the amount + tax
     * @param playerData the player to withdraw
     * @param price the amount to withdraw
     * @return boolean value where true means can afford.
     */
    public static boolean playerCanAfford(PlayerData playerData, int price) {
        int tax = calculateWithdrawTaxAmount(price);
        return playerData.canAfford(price + tax);
    }

    public static String tellPlayerCannotAfford(Player player, String theProduct, int price) {
        int vatAmount = getTaxForPrice(price);
        Tools.tellPlayer(new ErrorChat(), player,
                LangDict.getString(LangDict.YOU_CANNOT_AFFORD_)
                        + theProduct + LangDict.getString("sellItem.for")
                        + price
                        + LangDict.getString(LangDict.CURRENCY)
                        + " + "
                        + vatAmount
                        + LangDict.getString(LangDict.CURRENCY)
                        + LangDict.getString(LangDict._IN_VAT),
                ChatColor.RED);
        return "";
    }

    public static String tellPlayerTheyNeed(Player player, int price, String forWhat) {
        int vatAmount = getTaxForPrice(price);
        Tools.tellPlayer(new ErrorChat(), player, LangDict.getString(LangDict.YOU_NEED_) +
                price +
                LangDict.getString(LangDict.CURRENCY) +
                " + " +
                vatAmount +
                LangDict.getString(LangDict.CURRENCY) +
                LangDict.getString(LangDict.CURRENCY) +
                LangDict.getString("banking.inVat") + " " +
                forWhat, ChatColor.RED);
        return "";
    }

    public static int getTaxForPrice(int price) {
        return calculateWithdrawTaxAmount(price);
    }

    public static boolean playerCanAffordTaxFree(Player player, int price) {
        return Main.playerDataManager.getPlayerData(player).canAfford(price);
    }

    public static void withdrawPlayer(String youJustPaidFor, Player player, int amount) {
        int tax = calculateWithdrawTaxAmount(amount);
        // inform player
        Tools.tellPlayer(new BankChat(), player, LangDict.getString("banking.youJustPaid") + amount +
                LangDict.getString(LangDict.CURRENCY) + LangDict.getString("sellItem.for") + youJustPaidFor + " + " +
                tax + LangDict.getString(LangDict.CURRENCY) + " (" + getWithdrawTaxPercentDisplay()
                + "%)"+ LangDict.getString("banking.inVat"), ChatColor.GRAY);
        withdrawPlayer(Main.playerDataManager.getPlayerData(player), amount);
    }

    public static void withdrawPlayer(PlayerData playerData, int amount) {
        int tax = calculateWithdrawTaxAmount(amount);
        taxBank += tax;

        // withdraw player and save
        playerData.withdrawBalance(amount + tax);
        save();
    }

    public static void withdrawPlayerWithoutTax(Player player, int amount) {
        Main.playerDataManager.getPlayerData(player).withdrawBalance(amount);
    }

    public static void depositPlayer(Player player, int amount) {
        PlayerData playerData = Main.playerDataManager.getPlayerData(player);

        // store if player was high end so we can compare
        boolean wasHighEnd = playerData.isHighEnd();

        // calculate tax amount
        int tax = calculateDepositTaxAmount(player, amount);

        // if the taxed amount for some reason is less than 0. This shouldn't happen.
        if (amount - tax <= 0) {
            tax = 0;
            return;
        }

        // add amount to treasury
        taxBank += tax;

        // add amount to player bank
        playerData.depositBalance(amount - tax);

        // inform player
        Tools.tellPlayer(new BankChat(), player, LangDict.getString("banking.youJustPaid") +
                tax +
                LangDict.getString(LangDict.CURRENCY) +
                " (" + getDepositTaxPercentDisplayForPlayer(player) +
                "%)" + LangDict.getString("banking.inTax"), ChatColor.GRAY);

        // player just became high, we should tell him
        if (!wasHighEnd && playerData.isHighEnd()) {
            Tools.tellPlayer(new BankChat(), player, "You just received high end status. You now have access to the lounge. Do /lounge info to read more", ChatColor.GOLD);
            Tools.playSoundForSinglePlayer(player, Sound.BLOCK_NOTE_BLOCK_BELL);
        }

        save();
    }

    public static void depositPlayerWithoutTax(Player player, int amount) {
        PlayerData playerData = Main.playerDataManager.getPlayerData(player);
        depositPlayerWithoutTax(player, playerData, amount);
    }

    public static void depositPlayerWithoutTax(Player player, PlayerData playerData, int amount) {
        // store if player was high end so we can compare
        boolean wasHighEnd = playerData.isHighEnd();

        // deposit the amount
        playerData.depositBalance(amount);

        // player just became high, we should tell him
        if (player != null && !wasHighEnd && playerData.isHighEnd()) {
            Tools.tellPlayer(new BankChat(), player, "You just received high end status. You now have access to the lounge. Do /lounge info to read more", ChatColor.GOLD);
            Tools.playSoundForSinglePlayer(player, Sound.BLOCK_NOTE_BLOCK_BELL);
        }
    }

    public static int getPlayerBalance(Player player) {
        if (Main.playerDataManager.getPlayerData(player) == null) {
            return 0;
        }
        return Main.playerDataManager.getPlayerData(player).getBalance();
    }

    private static int calculateStocksWithdrawTaxAmount(int price) {
        return (int) (price*getWithdrawTaxPercent());
    }

    private static int calculateWithdrawTaxAmount(int price) {
        return (int) (price*getWithdrawTaxPercent());
    }

    private static int calculateDepositTaxAmount(Player player, int price) {
        return (int) (price*getDepositTaxPercentForPlayer(player));
    }

    private static double getWithdrawTaxPercent() {
        return 0.13;
    }

    public static int getWithdrawTaxPercentDisplay() {
        return (int) (getWithdrawTaxPercent()*100);
    }

    private static double getDepositTaxPercentForPlayer(Player player) {
        int balance = Main.playerDataManager.getPlayerData(player).getBalance();
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
        int balance = Main.playerDataManager.getPlayerData(player).getBalance();
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

    public static int getDepositTaxPercentDisplayForPlayer(Player player) {
        return (int) (getDepositTaxPercentForPlayer(player)*100);
    }

    public static double getWealthTaxPercentDisplayForPlayer(Player player) {
        return getWealthTaxPercentForPlayer(player)*100;
    }

    public static void save() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taxBank", taxBank);
        jsonObject.put("treasuryUsername", chancellorUsername);
        Tools.saveJsonToFile("Bank.json", jsonObject);
    }

    public static void load() {
        JSONObject jsonObject = Tools.loadJson("Bank.json");
        if (jsonObject == null) {
            return;
        }
        taxBank = (int) (long) jsonObject.get("taxBank");
        chancellorUsername = (String) jsonObject.get("treasuryUsername");
    }

    public static int getBankBalance() {
        return taxBank;
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
                payWealthTaxForPlayer(plugin, player);
                payPropertyTaxForPlayer(plugin, player);
            }

            save();

            startTaxCollector(plugin);
        }, Tools.secToTicks(60*minute));
    }

    private static void payWealthTaxForPlayer(Main plugin, Player player) {
        // calculate wealth tax
        int balance = getPlayerBalance(player);
        int tax = (int) (balance * getWealthTaxPercentForPlayer(player));

        if (payTax(plugin, player, tax)) {
            Tools.tellPlayer(new BankChat(), player, LangDict.getString("banking.youJustPaid") + tax + LangDict.getString(LangDict.CURRENCY) + LangDict.getString("banking.inWealthTax"));
        }
    }

    private static void payPropertyTaxForPlayer(Main plugin, Player player) {
        // get the property tax
        int tax = getPropertyTaxForPlayer(player);

        // pay it
        if (payTax(plugin, player, tax)) {
            Tools.tellPlayer(new BankChat(), player, LangDict.getString("banking.youJustPaid") + tax + LangDict.getString(LangDict.CURRENCY) + LangDict.getString("banking.inPropertyTax") + StaticValues.CHUNK_TAX + ")");
        }
    }

    /**
     * calculate property tax
     * @param player
     * @return
     */
    public static int getPropertyTaxForPlayer(Player player) {
        int chunkPoints = Main.playerDataManager.getPlayerData(player).getOwnedChunks().size();
        int tax = chunkPoints*StaticValues.CHUNK_TAX;
        return tax;
    }

    public static boolean payTax(Main plugin, Player player, int tax) {
        if (tax == 0) {
            return false;
        }

        // send player to jail if it cannot afford tax
        if (!playerCanAffordTaxFree(player, tax)) {
            int bal = getPlayerBalance(player);
            withdrawPlayerWithoutTax(player, bal);
            taxBank += bal;

            JailManager.sendToJail(plugin, player, LangDict.getString("playerEvents.jail.jailReasonTax"), LangDict.getString("playerEvents.jail.jailOutTax"), 60*4);
            save();
            return false;
        }

        taxBank += tax;
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
    public static boolean treasuryCanAfford(int price) {
        return taxBank >= price;
    }

    public static void withdrawTreasury(int amount) {
        taxBank -= amount;
        save();
    }

    /**
     * Removes all money from a player.
     * @param player
     */
    public static void bankruptPlayer(Player player) {
        withdrawPlayerWithoutTax(player, getPlayerBalance(player));
    }
}
