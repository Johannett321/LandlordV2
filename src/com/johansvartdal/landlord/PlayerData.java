package com.johansvartdal.landlord;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class PlayerData {

    private int currentBalance = 17000;
    private int availableChunkPoints = 1;
    private final String username;
    private Location home;
    private ArrayList<int[]> ownedChunks = new ArrayList<>();


    private final World mainWorld;

    public PlayerData(World mainWorld, Player player) {
        this.username = player.getName();
        this.mainWorld = mainWorld;
        God.speak(Main.langDict.getString("newCitizen") + player.getDisplayName());
    }

    public PlayerData(World mainWorld, String username) {
        this.username = username;
        this.mainWorld = mainWorld;
    }

    public void setHome(Location location) {
        home = location;
        save();
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

    private ArrayList<int[]> convertToOwnedChunks(JSONArray arr) {
        ArrayList<int[]> ownedChunks = new ArrayList<>();
        if (arr == null) {
            return ownedChunks;
        }

        for (int i = 0; i < arr.size(); i++) {
            JSONArray currentChunk = (JSONArray) arr.get(i);

            int[] currentOwned = new int[]{(int) currentChunk.get(0), (int) currentChunk.get(1)};
            ownedChunks.add(currentOwned);
        }
        return ownedChunks;
    }

    public void save() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Balance", currentBalance);
        jsonObject.put("AvailableChunkPoints", availableChunkPoints);
        jsonObject.put("OwnedChunks", ownedChunksAsJSONArray());

        if (home != null) {
            JSONObject homeJsonObj = new JSONObject();
            homeJsonObj.put("x", home.getX());
            homeJsonObj.put("y", home.getY());
            homeJsonObj.put("z", home.getZ());
            jsonObject.put("Home", homeJsonObj);
        }

        Tools.saveJsonToFile("players/" + username + ".json", jsonObject);
    }

    public void load() {
        JSONObject obj = Tools.loadJson("players/" + username + ".json");
        System.out.println("LOADED OBJ: " + obj.toJSONString());
        if (obj == null) {
            System.out.println("CRITICAL ERRORR!!!!! MAYDAY");
        }
        currentBalance = (int) ((long) obj.get("Balance"));
        availableChunkPoints = (int) ((long)obj.get("AvailableChunkPoints"));

        int homeX = (int) (double) ((JSONObject) obj.get("Home")).get("x");
        int homeY = (int) (double) ((JSONObject) obj.get("Home")).get("y");
        int homeZ = (int) (double) ((JSONObject) obj.get("Home")).get("z");
        home = new Location(mainWorld, homeX, homeY, homeZ);

        ownedChunks = convertToOwnedChunks((JSONArray) obj.get("OwnedChunks"));

    }

    public Location getHomeLocation() {
        return home;
    }

    public int getBalance() {
        return currentBalance;
    }
}
