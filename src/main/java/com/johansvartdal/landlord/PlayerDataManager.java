package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

import static com.johansvartdal.landlord.Tools.debugLog;

public class PlayerDataManager {

    private final Main plugin;
    private final World mainWorld;
    private final ArrayList<PlayerData> playerDataList;

    public PlayerDataManager(World mainWorld, Main plugin) {
        this.playerDataList = new ArrayList<>();
        this.plugin = plugin;
        this.mainWorld = mainWorld;
    }

    public void loadData() {
        File directory = new File(plugin.getDataFolder() + "/players");

        debugLog("Attempting to load playerdata...");
        if (!directory.exists()) debugLog("IF THIS IS NOT A NEW GAME, AN ERROR HAS OCCURED. NO PLAYERSAVES WERE FOUND");

        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                String filename = file.getName();
                String filenameWE = filename.split("\\.")[0];
                PlayerData playerData = new PlayerData(mainWorld, filenameWE);
                playerData.load();
                playerDataList.add(playerData);
            }
        }
    }

    public void addNewPlayer(PlayerData playerData) {
        playerDataList.add(playerData);
    }

    public boolean playerExists(Player player) {
        return playerExists(player.getName());
    }

    public boolean playerExists(String username) {
        return getPlayerData(username) != null;
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getName());
    }

    public PlayerData getPlayerData(String username) {
        for (PlayerData pd: playerDataList) {
            if (pd.getUsername().equals(username)) {
                return pd;
            }
        }
        return null;
    }

    public void addChunkToPlayer(Player player, int chunkX, int chunkZ) {
        getPlayerData(player).addOwnedChunk(chunkX, chunkZ);
    }

    public ArrayList<PlayerData> getPlayerDataList() {
        return playerDataList;
    }

    public void giveEveryoneChunkPoints(int amount) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            givePlayerChunkPoints(player, amount);
        }
    }

    public void givePlayerChunkPoints(Player player, int amount) {
        getPlayerData(player).addChunkPoints(amount);
    }
}
