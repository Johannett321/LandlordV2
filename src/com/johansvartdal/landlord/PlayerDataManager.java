package com.johansvartdal.landlord;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerDataManager {
    private ArrayList<PlayerData> playerDataList;

    public PlayerDataManager() {
        this.playerDataList = new ArrayList<>();
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
