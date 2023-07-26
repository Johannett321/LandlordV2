package com.johansvartdal.landlord;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.johansvartdal.landlord.Tools.*;

public class PlayerData {

    private int currentBalance = 0;
    private int availableChunkPoints = 0;
    private int streakMultiplier = 0;
    private long streakCollectDeadline = 0;
    private long streakCollectOpens = 0;
    private final String username;
    private Location home;
    private ArrayList<int[]> ownedChunks = new ArrayList<>();

    private final World mainWorld;


    /**
     * WARNING: Only use this method for the first time the user joins the server. It sets a starting balance.
     * @param mainWorld The main world
     * @param player The player that just joined
     */
    public PlayerData(World mainWorld, Player player) {
        this.username = player.getName();
        this.mainWorld = mainWorld;
        currentBalance = StaticValues.PLAYERS_STARTING_BALANCE;
        if (Properties.DEBUG_MODE) {
            currentBalance = 10000000;
            availableChunkPoints = 100;
        }
        save();
    }

    /**
     * This method should be used each time the server starts to load a user.
     * @param mainWorld The main world
     * @param username The username of the player that just joined
     */
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

    public boolean ownsChunk(Chunk chunk) {
        return ownsChunkAtLocation(chunk.getX(), chunk.getZ());
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

            int[] currentOwned = new int[]{(int)(long) currentChunk.get(0), (int)(long) currentChunk.get(1)};
            ownedChunks.add(currentOwned);
        }
        return ownedChunks;
    }

    public void save() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("Balance", currentBalance);
        jsonObject.put("AvailableChunkPoints", availableChunkPoints);
        jsonObject.put("OwnedChunks", ownedChunksAsJSONArray());
        jsonObject.put("streakMultiplier", streakMultiplier);
        jsonObject.put("streakCollectDeadline", streakCollectDeadline);
        jsonObject.put("streakCollectOpens", streakCollectOpens);

        if (home != null) {
            JSONObject homeJsonObj = new JSONObject();
            homeJsonObj.put("x", home.getX());
            homeJsonObj.put("y", home.getY());
            homeJsonObj.put("z", home.getZ());
            homeJsonObj.put("yaw", home.getYaw());
            homeJsonObj.put("pitch", home.getPitch());
            jsonObject.put("Home", homeJsonObj);
        }

        Tools.saveJsonToFile("players/" + username + ".json", jsonObject);
    }

    public void load() {
        debugLog("Loading playerdata for " + username + "...");

        JSONObject obj = Tools.loadJson("players/" + username + ".json");
        if (obj == null) {
            fatalLog("Failed to load " + username + "'s data");
            return;
        }

        // Balance
        currentBalance = (int) ((long) obj.get("Balance"));
        availableChunkPoints = (int) ((long)obj.get("AvailableChunkPoints"));

        // Home location
        if (obj.containsKey("Home")) {
            double homeX = (double) ((JSONObject) obj.get("Home")).get("x");
            double homeY = (double) ((JSONObject) obj.get("Home")).get("y");
            double homeZ = (double) ((JSONObject) obj.get("Home")).get("z");
            double homeYawD = (double) ((JSONObject) obj.get("Home")).get("yaw");
            double homePitchD = (double) ((JSONObject) obj.get("Home")).get("pitch");
            float homeYaw = (float) homeYawD;
            float homePitch = (float) homePitchD;
            debugLog(username + "'s home address: " + homeX + ":" + homeY + ":" + homeZ + ":" + homeYawD + ":" + homePitchD + ":" + homeYaw + ":" + homePitch);

            home = new Location(mainWorld, homeX, homeY, homeZ);
            home.setYaw(homeYaw);
            home.setPitch(homePitch);
        }else {
            debugLog("No Home data found for player. Must be a new player");
        }

        // Day streak
        streakMultiplier = (int) ((long) obj.get("streakMultiplier"));
        streakCollectDeadline = (long) obj.get("streakCollectDeadline");
        streakCollectOpens = (long) obj.get("streakCollectOpens");

        // Chunks owned
        ownedChunks = convertToOwnedChunks((JSONArray) obj.get("OwnedChunks"));
    }

    public Location getHomeLocation() {
        return home;
    }

    public int getBalance() {
        return currentBalance;
    }

    public int getChunkPoints() {
        return availableChunkPoints;
    }

    public int getChunkPurchasePrice() {
        return ownedChunks.size()*5000;
    }

    public int getStreakMultiplier() {
        return streakMultiplier;
    }

    public long getStreakCollectDeadline() {
        return streakCollectDeadline;
    }

    public long getStreakCollectOpens() {
        return streakCollectOpens;
    }

    public void updateStreak(long streakCollectOpens, long deadline, int multiplier) {
        this.streakMultiplier = multiplier;
        this.streakCollectDeadline = deadline;
        this.streakCollectOpens = streakCollectOpens;
        save();
    }

    public void addChunkPoints(int amount) {
        availableChunkPoints += amount;
        save();
    }

    public void withdrawChunkPoint() {
        availableChunkPoints--;
        save();
    }

    /**
     * Checks if the player is seen as a high end player. That is players with a balance above 50.000kr.
     * @return True if they are VIP.
     */
    public boolean isHighEnd() {
        if (Properties.DEBUG_MODE) {
            return true;
        }
        return currentBalance >= 50000;
    }
}
