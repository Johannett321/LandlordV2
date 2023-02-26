package com.johansvartdal.landlord;

import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class PlayerData {

    private int currentBalance = 17000;
    private int availableChunkPoints = 1;
    private final String username;
    private ArrayList<int[]> ownedChunks = new ArrayList<>();

    public PlayerData(Player player) {
        this.username = player.getName();
        God.speak(Main.langDict.getString("newCitizen") + player.getDisplayName());
    }

    public void addOwnedChunk(int chunkX, int chunkZ) {
        ownedChunks.add(new int[]{chunkX,chunkZ});
        System.out.println("Player now owns: " + chunkX + ":" + chunkZ);
        save();
    }

    public boolean canAfford(int price) {
        if (currentBalance >= price) {
            return true;
        }
        return false;
    }

    public void withdrawBalance(int amount) {
        currentBalance -= amount;
        save();
    }

    public void depositBalance(int amount) {
        currentBalance += amount;
        save();
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<int[]> getOwnedChunks() {
        return ownedChunks;
    }

    public boolean ownsChunkAtLocation(int chunkX, int chunkZ) {
        for (int[] ownChunk : ownedChunks) {
            if (ownChunk[0] == chunkX && ownChunk[1] == chunkZ) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChunkPoints() {
        return availableChunkPoints > 0;
    }

    private JSONArray ownedChunksAsJSONArray() {
        JSONArray ownedChunksArray = new JSONArray();
        for (int[] intArray : ownedChunks) {
            JSONArray innerArray = new JSONArray();
            for (int value : intArray) {
                innerArray.add(value);
            }
            ownedChunksArray.add(innerArray);
        }
        return ownedChunksArray;
    }

    public void save() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Balance", currentBalance);
        jsonObject.put("AvailableChunkPoints", availableChunkPoints);
        jsonObject.put("OwnedChunks", ownedChunksAsJSONArray());

        Tools.saveJsonToFile("players/" + username + ".json", jsonObject);
    }
}
