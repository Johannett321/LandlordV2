package com.johansvartdal.landlord;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;

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
        File directory = new File(plugin.getDataFolder() + "/" + "players");
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
}
