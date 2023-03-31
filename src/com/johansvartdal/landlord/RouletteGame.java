package com.johansvartdal.landlord;

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

        Tools.broadcastMessage(LangDict.getString("rouletteBegin") + itemStack.getAmount() + " " + itemStack.getType().name());
        Tools.playSoundForEveryone(Sound.BLOCK_LEVER_CLICK);

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                thirtySecsLeft();
            }
        }, Tools.secToTicks(30));
    }

    public void thirtySecsLeft() {
        Tools.broadcastMessage(ChatColor.YELLOW + LangDict.getString("rouletteEnding30"));
        Tools.playSoundForEveryone(Sound.BLOCK_LEVER_CLICK);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                fiveSecsLeft();
            }
        }, Tools.secToTicks(25));
    }

    public void fiveSecsLeft() {
        Tools.broadcastMessage(ChatColor.YELLOW + LangDict.getString("rouletteEnding5"));
        Tools.playSoundForEveryone(Sound.BLOCK_LEVER_CLICK);
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                announceWinner();
            }
        }, Tools.secToTicks(5));
    }

    public void announceWinner() {
        openForJoin = false;
        Random random = new Random();
        int randomInt = random.nextInt(rouletteGamePlayers.size() + 1);
        if (randomInt < rouletteGamePlayers.size()) {
            Tools.broadcastMessage(ChatColor.GREEN +  LangDict.getString("rouletteWinner") + rouletteGamePlayers.get(randomInt).getDisplayName());
            Tools.playSoundForEveryone(Sound.ENTITY_PLAYER_LEVELUP);
            rouletteGamePlayers.get(randomInt).getInventory().addItem(itemStack);
        }else {
            Tools.broadcastMessage(ChatColor.RED + LangDict.getString("rouletteNoWinner"));
        }
        rouletteGamePlayers.clear();
    }

    public static void addToGame(Player player) {
        for (Player player1:rouletteGamePlayers) {
            if (player.getUniqueId().toString().equals(player1.getUniqueId().toString())) {
                Tools.tellPlayer(player, LangDict.getString("rouletteJoinTwice"), ChatColor.RED);
                return;
            }
        }
        if (openForJoin) {
            if (Bank.playerCanAfford(player, LevelManager.getRouletteGamePrice())) {
                Bank.withdrawPlayer(LangDict.getString("aRouletteTicket"), player, LevelManager.getRouletteGamePrice());
                RouletteGame.rouletteGamePlayers.add(player);
                Tools.broadcastMessage(player.getDisplayName() + LangDict.getString("justJoinedRoulette"));
                Tools.tellPlayer(player,LangDict.getString("rouletteJoinMessage"), ChatColor.GREEN);
            }else {
                Tools.tellPlayer(player,LangDict.getString("cannotAfford") + LangDict.getString("aRouletteTicket") + LangDict.getString("for") + LevelManager.getRouletteGamePrice() + LangDict.getString(LangDict.CURRENCY) + LangDict.getString("plusTax"), ChatColor.RED);
            }

        }else {
            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);
            int minutesLeft = 60-minute;
            Tools.tellPlayer(player,LangDict.getString("noRouletteStart") + minutesLeft + LangDict.getString("noRouletteEnd"), ChatColor.RED);
        }
    }

    private ItemStack getRandomItemStack() {
        ItemStack itemStack = null;
        Random random = new Random();
        int randomInt = random.nextInt(36);

        switch (randomInt) {
            case 0: {
                itemStack = new ItemStack(Material.DIAMOND);
                itemStack.setAmount(1);
                break;
            }
            case 1: {
                itemStack = new ItemStack(Material.IRON_INGOT);
                itemStack.setAmount(14);
                break;
            }
            case 2: {
                itemStack = new ItemStack(Material.GUNPOWDER);
                itemStack.setAmount(9);
                break;
            }
            case 3: {
                itemStack = new ItemStack(Material.BONE);
                itemStack.setAmount(13);
                break;
            }
            case 4: {
                itemStack = new ItemStack(Material.STRING);
                itemStack.setAmount(11);
                break;
            }
            case 5: {
                itemStack = new ItemStack(Material.COOKED_BEEF);
                itemStack.setAmount(7);
                break;
            }
            case 6: {
                itemStack = new ItemStack(Material.GOLDEN_APPLE);
                itemStack.setAmount(1);
                break;
            }
            case 7: {
                itemStack = new ItemStack(Material.BLAZE_ROD);
                itemStack.setAmount(2);
                break;
            }
            case 8: {
                itemStack = new ItemStack(Material.SLIME_BALL);
                itemStack.setAmount(6);
                break;
            }
            case 9: {
                itemStack = new ItemStack(Material.NAME_TAG);
                itemStack.setAmount(2);
                break;
            }
            case 10: {
                itemStack = new ItemStack(Material.ANVIL);
                itemStack.setAmount(1);
                break;
            }
            case 11: {
                itemStack = new ItemStack(Material.ENCHANTING_TABLE);
                itemStack.setAmount(1);
                break;
            }
            case 12: {
                itemStack = new ItemStack(Material.COAL);
                itemStack.setAmount(32);
                break;
            }
            case 13: {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.DURABILITY, 2);
                itemStack.setAmount(1);
                break;
            }
            case 14: {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.MENDING, 1);
                itemStack.setAmount(1);
                break;
            }
            case 15: {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.DIG_SPEED, 3);
                itemStack.setAmount(1);
                break;
            }
            case 16: {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
                break;
            }
            case 17: {
                itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                itemStack = getEnchantedBook(itemStack, Enchantment.LOOT_BONUS_BLOCKS, 2);
                itemStack.setAmount(1);
                break;
            }
            case 18: {
                itemStack = new ItemStack(Material.DIAMOND_BOOTS);
                itemStack.setAmount(1);
                break;
            }
            case 19: {
                itemStack = new ItemStack(Material.DIAMOND_CHESTPLATE);
                itemStack.setAmount(1);
                break;
            }
            case 20: {
                itemStack = new ItemStack(Material.DIAMOND_HELMET);
                itemStack.setAmount(1);
                break;
            }
            case 21: {
                itemStack = new ItemStack(Material.DIAMOND_LEGGINGS);
                itemStack.setAmount(1);
                break;
            }
            case 22: {
                itemStack = new ItemStack(Material.DIAMOND_SWORD);
                itemStack.setAmount(1);
                break;
            }
            case 23: {
                itemStack = new ItemStack(Material.DIAMOND_AXE);
                itemStack.setAmount(1);
                break;
            }
            case 24: {
                itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
                itemStack.setAmount(1);
                break;
            }
            case 25: {
                itemStack = new ItemStack(Material.DIAMOND_SHOVEL);
                itemStack.setAmount(1);
                break;
            }
            case 26: {
                itemStack = new ItemStack(Material.IRON_PICKAXE);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.DIG_SPEED, 4);
                itemStack.setAmount(1);
                break;
            }
            case 27: {
                itemStack = new ItemStack(Material.IRON_PICKAXE);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.LOOT_BONUS_BLOCKS, 1);
                itemStack.setAmount(1);
                break;
            }
            case 28: {
                itemStack = new ItemStack(Material.IRON_SWORD);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.DAMAGE_ALL, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.LOOT_BONUS_MOBS, 3);
                itemStack.setAmount(1);
                break;
            }
            case 29: {
                itemStack = new ItemStack(Material.IRON_SHOVEL);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack.setAmount(1);
                break;
            }
            case 30: {
                itemStack = new ItemStack(Material.IRON_AXE);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.DIG_SPEED, 5);
                itemStack.setAmount(1);
                break;
            }
            case 31: {
                itemStack = new ItemStack(Material.IRON_HELMET);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
                break;
            }
            case 32: {
                itemStack = new ItemStack(Material.IRON_CHESTPLATE);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
                break;
            }
            case 33: {
                itemStack = new ItemStack(Material.IRON_LEGGINGS);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
                break;
            }
            case 34: {
                itemStack = new ItemStack(Material.IRON_BOOTS);
                itemStack = getEnchantedItem(itemStack, Enchantment.DURABILITY, 3);
                itemStack = getEnchantedItem(itemStack, Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                itemStack.setAmount(1);
                break;
            }
            case 35: {
                itemStack = new ItemStack(Material.SHULKER_SHELL);
                itemStack.setAmount(2);
                break;
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
