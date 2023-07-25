package com.johansvartdal.landlord;

import com.johansvartdal.landlord.chatentities.ChatEntity;
import com.johansvartdal.landlord.chatentities.InfoChat;
import com.johansvartdal.landlord.chatentities.WarningChat;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.johansvartdal.landlord.ItemGivenAction.dropped;

public class Tools {

    public static Main plugin;
    public static File pluginDir;

    public static void init(Main plugin) {
        Tools.plugin = plugin;
        pluginDir = plugin.getDataFolder();
    }

    public static String readInternal(String fileName) {
        InputStream inputStream = Tools.class.getResourceAsStream(fileName);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            if (Properties.DEBUG_MODE) e.printStackTrace();
        }
        return null;
    }
	
	public static String read(String fileName) {
		String wholeText = null;
        String line;

        try {
            FileReader fileReader = new FileReader(Tools.pluginDir.getAbsolutePath() + "/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                if (wholeText == null) {
                    wholeText = line;
                }else {
                    wholeText = wholeText + System.lineSeparator();
                    wholeText = wholeText + line;
                }
            }   
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");                  
        }
		return wholeText;
	}
	
	public static void write(String fileName, String textToWrite) {
        try {
            FileWriter fileWriter = new FileWriter(Tools.pluginDir.getAbsolutePath() + "/" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(textToWrite);
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '" + fileName + "'");
        }
	}

    public static void deleteFile(String fileName) {
        File file = new File(Tools.pluginDir.getAbsolutePath() + "/" + fileName);
        file.delete();
    }

    public static void serialize(String fileName, Serializable object) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Tools.pluginDir.getAbsolutePath() + "/" + fileName);
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object deSerialize(String fileName) {
        Object readObject = null;
        try {
            String filePath = pluginDir.getAbsolutePath() + "/" + fileName;
            System.out.println("Attempting to load from: " + filePath);
            FileInputStream fileInputStream
                    = new FileInputStream(filePath);
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            readObject = objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return readObject;
    }

    public static int getNumberOfFilesInDirectory(String fileName) {
        File directory = new File(pluginDir.getAbsolutePath() + "/" + fileName);
        int fileCount = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                fileCount++;
            }
        }
        return fileCount;
    }

    public static boolean fileExists(String fileName) {
        File file = new File(pluginDir.getAbsolutePath() + "/" + fileName);
        return file.exists();
    }

    public static JSONObject loadJson(String fileName) {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(pluginDir.getAbsolutePath() + "/" + fileName))
        {
            //Read JSON file
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            if (Properties.DEBUG_MODE) System.out.println("ERROR: Cannot find file: " + fileName);
        }
        return null;
    }

    public static void saveJsonToFile(String fileName, JSONObject object) {
        //Write JSON file
        try (FileWriter file = new FileWriter(pluginDir.getAbsolutePath() + "/" + fileName)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(object.toJSONString());
            file.flush();
        } catch (IOException e) {
            if (Properties.DEBUG_MODE) System.out.println("ERROR: Failed to save json to file: " + fileName);
        }
    }

    public static void broadcastMessage(String message) {
        broadcastMessage(new InfoChat(), message, null, null);
    }

    public static void broadcastMessage(String message, Player[] excludePlayers) {
        broadcastMessage(new InfoChat(), message, null, excludePlayers);
    }

    public static void broadcastMessage(String message, ChatColor chatColor) {
        broadcastMessage(new InfoChat(), message, chatColor, null);
    }

    public static void broadcastMessage(ChatEntity chatEntity, String message) {
        broadcastMessage(chatEntity, message, null, null);
    }

    public static void broadcastMessage(ChatEntity chatEntity, String message, Player[] excludePlayers) {
        broadcastMessage(chatEntity, message, null, excludePlayers);
    }

    public static void broadcastMessage(ChatEntity chatEntity, String message, ChatColor chatColor) {
        broadcastMessage(chatEntity, message, chatColor, null);
    }

    public static void broadcastMessage(ChatEntity chatEntity, String message, ChatColor chatColor, Player[] excludePlayers) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // check if player should be excluded
            if (excludePlayers != null) {
                boolean playerShouldBeExcluded = false;
                for (Player excludedPlayer : excludePlayers) {
                    if (player.equals(excludedPlayer)) {
                        playerShouldBeExcluded = true;
                    }
                }
                if (playerShouldBeExcluded) {
                    continue;
                }
            }

            if (chatColor == null) {
                chatColor = chatEntity.getMessageColor();
            }

            // broadcast the message to the player
            Tools.tellPlayer(chatEntity, player, message, chatColor);
        }
    }

    public static long secToTicks(int sec) {
        return sec* 20L;
    }

    public static Location highestStandingPoint(Location location) {
        for (int y = 319; y > -64; y--) {
            location.setY(y);
            if (!location.getBlock().getType().isAir() && location.getBlock().getType() != Material.BARRIER) {
                location.setY(location.getY()+2);
                location.setX(location.getX());
                location.setZ(location.getZ());
                return location;
            }
        }
        return location;
    }

    public static boolean stateNotNormal(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players");
            return true;
        }
        if (!Main.properties.gameStateIsNormal()) {
            return true;
        }
        return false;
    }

    public static String getDisplayNameOfItem(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack.getType().name().replace("_", " ").toLowerCase();
        }
        if (meta.hasDisplayName()) {
            return meta.getDisplayName().toLowerCase();
        }
        return stack.getType().name().toLowerCase();
    }

    public static Location middlePointBlock(Location location) {
        location.setX(location.getX() + 0.5);
        location.setZ(location.getZ() + 0.5);
        return location;
    }

    /**
     * Puts the given item in the players inventory. If the inventory is full, the item will be
     * thrown out instead
     * @param player
     * @param itemStack
     * @return ItemGivenAction If the item was successfully added to inventory or dropped.
     */
    public static ItemGivenAction givePlayerItemOrDrop(Player player, ItemStack itemStack, boolean informIfDropped) {
        // Give player item, and drop if there are no space in inventory
        HashMap<Integer, ItemStack> itemsThatDidntFit = player.getInventory().addItem(itemStack);
        for (ItemStack itemThatDidntFit: itemsThatDidntFit.values()) {
            player.getWorld().dropItem(player.getLocation().add(0,1,0), itemThatDidntFit);
        }

        // Check if there were any dropped items
        if (itemsThatDidntFit.size() > 0 ) {
            debugLog("Attempted to give player an item, but there was not enough space in the inventory." +
                    "The item was therefore dropped on the ground instead. Item: " + itemStack.getItemMeta().getDisplayName());

            // Tell player about the dropped item if it should be done
            if (informIfDropped) {
                Tools.tellPlayer(new WarningChat(), player, LangDict.getString("generalSentenceParts.itemDroppedWarning"));
            }
            return ItemGivenAction.dropped;
        }
        return ItemGivenAction.success;
    }

    public static void playSoundForEveryone(Sound sound) {
        for(Player player : Bukkit.getOnlinePlayers()){
            player.playSound(player, sound, 1, 0);
        }
    }

    public static void playSoundForEveryone(Sound sound, Player[] excludePlayers) {
        for(Player player : Bukkit.getOnlinePlayers()){
            player.playSound(player, sound, 1, 0);
        }
    }

    public static void playSoundForSinglePlayer(Player player, Sound sound) {
        player.playSound(player, sound, 1, 0);
    }

    public static void tellPlayer(CommandSender player, String message, ChatColor chatColor) {
        if (player instanceof Player) {
            tellPlayer((Player) player, message, chatColor);
        }
    }

    public static void tellPlayer(CommandSender player, String message) {
        if (player instanceof Player) {
            tellPlayer((Player) player, message, null);
        }
    }

    public static void tellPlayer(Player player, String message) {
        tellPlayer(player, message, null);
    }

    public static void tellPlayer(ChatEntity chatEntity, Player player, String message) {
        tellPlayer(chatEntity, player, message, null);
    }

    public static void tellPlayer(Player player, String message, ChatColor chatColor) {
        tellPlayer(null, player, message, chatColor);
    }

    public static void tellPlayer(ChatEntity chatEntity, Player player, String message, ChatColor chatColor) {
        if (chatEntity == null) {
            chatEntity = new InfoChat();
        }
        if (chatColor == null) {
            chatColor = chatEntity.getMessageColor();
        }
        player.sendMessage(chatEntity.getChatColor() + "[" + chatEntity.getDisplayName() + "] " + chatColor + message);
    }

    public static void printMenuHeader(Player player, String title) {
        player.sendMessage(ChatColor.YELLOW + "--- " + title +  " ---");
    }

    public static void printMenuOption(Player player, String title, String desc) {
        player.sendMessage(ChatColor.YELLOW + title + " " + ChatColor.WHITE + desc);
    }

    public static void killAllMobsInWorld(World world) {
        for(Entity entity : world.getEntities()) {
            if (entity instanceof Mob && !(entity instanceof Player)) {
                entity.remove();
            }
        }
    }

    public static String getTextTimeSeconds(int timeLeftSeconds) {
        if (timeLeftSeconds >= 60) {
            return timeLeftSeconds/60 + LangDict.getString("generalSentenceParts.minutes");
        }else {
            return timeLeftSeconds + LangDict.getString("generalSentenceParts.seconds");
        }
    }

    public static void performTaskAfterCountdown(Runnable runnable, String beginMessage, int seconds) {

        // we can safely wait a minute before counting down
        if (seconds > 90) {
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                handleTime(runnable, beginMessage, seconds-60);
            }, Tools.secToTicks(60));
            return;
        }

        // we can't wait a minute, as there is no minute left. Start countdown on 30 sec left
        int timeTill30SecLeft = seconds-30;
        Bukkit.getScheduler().runTaskLater(plugin, () -> handleTime(runnable, beginMessage, 30), Tools.secToTicks(timeTill30SecLeft));
    }

    private static void handleTime(Runnable runnable, String beginMessage, int seconds) {
        if (seconds > 0) Tools.broadcastMessage(beginMessage + " " + getTextTimeSeconds(seconds));

        if (seconds <= 0) {
            runnable.run();
            return;
        }

        // ----------------- SCHEDULE NEW MESSAGE -----------------

        // One minute till next
        if (seconds > 60) {
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                handleTime(runnable, beginMessage, seconds-60);
            }, Tools.secToTicks(60));
            return;
        }

        // 30 sec left
        if (seconds > 30) {
            scheduleHandleTimeIn(runnable, beginMessage, seconds, 30);
            return;
        }

        // 15 sec left
        if (seconds > 15) {
            scheduleHandleTimeIn(runnable, beginMessage, seconds, 15);
            return;
        }

        // 10 sec left
        if (seconds > 10) {
            scheduleHandleTimeIn(runnable, beginMessage, seconds, 10);
            return;
        }

        // 5 sec left
        if (seconds > 5) {
            scheduleHandleTimeIn(runnable, beginMessage, seconds, 5);
            return;
        }

        // counting seconds
        Bukkit.getScheduler().runTaskLater(plugin, () -> handleTime(runnable, beginMessage, seconds-1), Tools.secToTicks(1));
    }

    private static void scheduleHandleTimeIn(Runnable runnable, String beginMessage, int currentSecondsLeft, int nextBroadcastWhenSecsLeft) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> handleTime(runnable, beginMessage, nextBroadcastWhenSecsLeft), Tools.secToTicks(currentSecondsLeft-nextBroadcastWhenSecsLeft));
    }

    public static void broadcastTitle(String title, String subtitle) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle);
        }
    }


    public static void debugLog(String message) {
        if (Properties.DEBUG_LOGGING) {
            System.out.println("[LANDLORD] Debug: " + message);
        }
    }

    public static void errorLog(String message) {
        System.out.println("[LANDLORD] ERROR: " + message);
    }

    public static void fatalLog(String message) {
        message = message.toUpperCase();
        System.out.println("[LANDLORD] FATAL ERROR: " + message);
    }
}
