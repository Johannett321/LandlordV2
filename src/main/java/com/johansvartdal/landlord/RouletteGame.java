package com.johansvartdal.landlord;

import com.johansvartdal.landlord.chatentities.ErrorChat;
import com.johansvartdal.landlord.chatentities.RouletteChat;
import com.johansvartdal.landlord.levels.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class RouletteGame {

    private Main plugin;
    private static ArrayList<Player> rouletteGamePlayers;
    public static boolean openForJoin = false;
    ItemStack itemStack = null;

    public RouletteGame(Main plugin) {
        this.plugin = plugin;
        rouletteGamePlayers = new ArrayList<>();
        startGame();
    }

    private void startGame() {
        checkIfGameShouldBeRunning();
        scheduleNewCheck();
    }

    private void scheduleNewCheck() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            checkIfGameShouldBeRunning();
            scheduleNewCheck();
        }, Tools.secToTicks(10));
    }

    private void checkIfGameShouldBeRunning() {
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);
        if (minute == 0 && !openForJoin && Main.properties.gameStateIsNormal() && LevelManager.featureUnlocked("roulette")) {
            runRouletteGame();
        }
    }

    private void runRouletteGame() {
        openForJoin = true;
        itemStack = getRandomItemStack();

        Tools.broadcastMessage(new RouletteChat(), LangDict.getString("roulette.rouletteBegin") + itemStack.getAmount() + " " + itemStack.getType().name());
        Tools.playSoundForEveryone(Sound.BLOCK_LEVER_CLICK);
        Tools.playSoundForEveryone(Sound.BLOCK_NOTE_BLOCK_HARP);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                thirtySecsLeft();
            }
        }, Tools.secToTicks(30));
    }

    public void thirtySecsLeft() {
        Tools.broadcastMessage(new RouletteChat(), LangDict.getString("roulette.rouletteEnding30"));
        Tools.playSoundForEveryone(Sound.BLOCK_LEVER_CLICK);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                fiveSecsLeft();
            }
        }, Tools.secToTicks(25));
    }

    public void fiveSecsLeft() {
        Tools.broadcastMessage(new RouletteChat(), LangDict.getString("roulette.rouletteEnding5"));
        Tools.playSoundForEveryone(Sound.BLOCK_LEVER_CLICK);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                announceWinner();
            }
        }, Tools.secToTicks(5));
    }

    public void announceWinner() {
        // close joining
        openForJoin = false;

        Random random = new Random();
        int randomInt = random.nextInt(rouletteGamePlayers.size() + 1);

        // No-one won
        if (randomInt >= rouletteGamePlayers.size()) {
            Tools.broadcastMessage(new RouletteChat(), ChatColor.RED + LangDict.getString("roulette.rouletteNoWinner"));
            rouletteGamePlayers.clear();
            return;
        }

        Player winner = rouletteGamePlayers.get(randomInt);

        // Announce winner
        Tools.broadcastMessage(new RouletteChat(), ChatColor.GREEN +  LangDict.getString("roulette.rouletteWinner") + winner.getDisplayName());
        Tools.playSoundForSinglePlayer(winner, Sound.ENTITY_PLAYER_LEVELUP);
        Tools.playSoundForEveryone(Sound.BLOCK_LEVER_CLICK, new Player[]{winner});
        Tools.givePlayerItemOrDrop(winner, itemStack, true);

        // Reset list of players who joined
        rouletteGamePlayers.clear();
    }

    public static void addToGame(Player player) {
        // Make sure there actually is a roulette running
        if (!openForJoin) {
            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);
            int minutesLeft = 60-minute;
            Tools.tellPlayer(new ErrorChat(), player,LangDict.getString("roulette.noRouletteStart") + minutesLeft + LangDict.getString("roulette.noRouletteEnd"));
            return;
        }

        // Don't join twice
        for (Player player1:rouletteGamePlayers) {
            if (player.getUniqueId().toString().equals(player1.getUniqueId().toString())) {
                Tools.tellPlayer(new ErrorChat(), player, LangDict.getString("roulette.rouletteJoinTwice"));
                return;
            }
        }

        // Make sure player can afford to join
        if (!Bank.playerCanAfford(player, LevelManager.getRouletteGamePrice())) {
            Bank.tellPlayerCannotAfford(player, LangDict.getString("banking.aRouletteTicket"), LevelManager.getRouletteGamePrice());
            return;
        }

        // Withdraw player and join
        Bank.withdrawPlayer(LangDict.getString("banking.aRouletteTicket"), player, LevelManager.getRouletteGamePrice());
        RouletteGame.rouletteGamePlayers.add(player);
        Tools.broadcastMessage(new RouletteChat(), player.getDisplayName() + LangDict.getString("roulette.justJoinedRoulette"), new Player[]{player});
        Tools.tellPlayer(new RouletteChat(), player, LangDict.getString("roulette.rouletteJoinMessage"), ChatColor.GREEN);
    }

    private ItemStack getRandomItemStack() {
        ItemStack itemStack = null;
        Random random = new Random();
        int randomInt = random.nextInt(41);

        switch (randomInt) {
            case 0 -> {
                itemStack = new ItemStack(Material.DIAMOND);
                itemStack.setAmount(1);
            }case 1 -> {
                itemStack = new ItemStack(Material.IRON_INGOT);
                itemStack.setAmount(14);
            }case 2 -> {
                itemStack = new ItemStack(Material.GUNPOWDER);
                itemStack.setAmount(9);
            }case 3 -> {
                itemStack = new ItemStack(Material.BONE);
                itemStack.setAmount(13);
            }case 4 -> {
                itemStack = new ItemStack(Material.STRING);
                itemStack.setAmount(11);
            }case 5 -> {
                itemStack = new ItemStack(Material.COOKED_BEEF);
                itemStack.setAmount(7);
            }case 6 -> {
                itemStack = new ItemStack(Material.GOLDEN_APPLE);
                itemStack.setAmount(1);
            }case 7 -> {
                itemStack = new ItemStack(Material.BLAZE_ROD);
                itemStack.setAmount(2);
            }case 8 -> {
                itemStack = new ItemStack(Material.SLIME_BALL);
                itemStack.setAmount(6);
            }case 9 -> {
                itemStack = new ItemStack(Material.NAME_TAG);
                itemStack.setAmount(2);
            }case 10 -> {
                itemStack = new ItemStack(Material.ANVIL);
                itemStack.setAmount(1);
            }case 11 -> {
                itemStack = new ItemStack(Material.ENCHANTING_TABLE);
                itemStack.setAmount(1);
            }case 12 -> {
                itemStack = new ItemStack(Material.COAL);
                itemStack.setAmount(32);
            }case 13 -> {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.DURABILITY, 2);
                itemStack.setAmount(1);
            }case 14 -> {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.MENDING, 1);
                itemStack.setAmount(1);
            }case 15 -> {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.DIG_SPEED, 3);
                itemStack.setAmount(1);
            }case 16 -> {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
            }case 17 -> {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.LOOT_BONUS_BLOCKS, 2);
                itemStack.setAmount(1);
            }case 18 -> {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.SILK_TOUCH, 1);
                itemStack.setAmount(1);
            }case 19 -> {
                itemStack = new ItemStack(Material.DIAMOND_BOOTS);
                itemStack.setAmount(1);
            }case 20 -> {
                itemStack = new ItemStack(Material.DIAMOND_CHESTPLATE);
                itemStack.setAmount(1);
            }case 21 -> {
                itemStack = new ItemStack(Material.DIAMOND_HELMET);
                itemStack.setAmount(1);
            }case 22 -> {
                itemStack = new ItemStack(Material.DIAMOND_LEGGINGS);
                itemStack.setAmount(1);
            }case 23 -> {
                itemStack = new ItemStack(Material.DIAMOND_SWORD);
                itemStack.setAmount(1);
            }case 24 -> {
                itemStack = new ItemStack(Material.DIAMOND_AXE);
                itemStack.setAmount(1);
            }case 25 -> {
                itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
                itemStack.setAmount(1);
            }case 26 -> {
                itemStack = new ItemStack(Material.DIAMOND_SHOVEL);
                itemStack.setAmount(1);
            }case 27 -> {
                itemStack = new ItemStack(Material.IRON_PICKAXE);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.DIG_SPEED, 4);
                itemStack.setAmount(1);
            }case 28 -> {
                itemStack = new ItemStack(Material.IRON_PICKAXE);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.LOOT_BONUS_BLOCKS, 1);
                itemStack.setAmount(1);
            }case 29 -> {
                itemStack = new ItemStack(Material.IRON_SWORD);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.DAMAGE_ALL, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.LOOT_BONUS_MOBS, 3);
                itemStack.setAmount(1);
            }case 30 -> {
                itemStack = new ItemStack(Material.IRON_SHOVEL);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack.setAmount(1);
            }case 31 -> {
                itemStack = new ItemStack(Material.IRON_AXE);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.DIG_SPEED, 5);
                itemStack.setAmount(1);
            }case 32 -> {
                itemStack = new ItemStack(Material.IRON_HELMET);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
            }case 33 -> {
                itemStack = new ItemStack(Material.IRON_CHESTPLATE);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
            }case 34 -> {
                itemStack = new ItemStack(Material.IRON_LEGGINGS);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
            }case 35 -> {
                itemStack = new ItemStack(Material.IRON_BOOTS);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
            }case 36 -> {
                itemStack = new ItemStack(Material.SHULKER_SHELL);
                itemStack.setAmount(2);
            }case 37 -> {
                itemStack = new ItemStack(Material.ELYTRA);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 2);
                itemStack = getEnchantedItem(itemStack, Enchantment.MENDING, 1);
                itemStack.setAmount(1);
            }case 38 -> {
                itemStack = new ItemStack(Material.ANCIENT_DEBRIS);
                itemStack.setAmount(2);
            }case 39 -> {
                itemStack = new ItemStack(Material.DIAMOND);
                itemStack.setAmount(3);
            }case 40 -> {
                itemStack = new ItemStack(Material.NAME_TAG);
                itemStack.setAmount(1);
            }
        }
        return itemStack;
    }

    public ItemStack getEnchantedBook(ItemStack item, Enchantment enchantment, int level) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getEnchantedItem(ItemStack item, Enchantment enchantment, int level) {
        ItemMeta testEnchantMeta = item.getItemMeta();
        testEnchantMeta.addEnchant(enchantment, level, true);
        item.setItemMeta(testEnchantMeta);
        return item;
    }
}
