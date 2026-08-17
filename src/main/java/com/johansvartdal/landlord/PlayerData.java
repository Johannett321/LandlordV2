package com.johansvartdal.landlord;

import com.johansvartdal.landlord.levels.LevelManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.johansvartdal.landlord.Tools.*;

public class PlayerData {

    @Getter @Setter
    private String status;
    @Getter private Location homeLocation;
    @Getter private Location cabinLocation;
    @Getter private Location shopLocation;
    @Getter private final String username;
    @Getter private int chunkGuardCoins = 0;
    @Getter private int chunkPoints = 0;
    @Getter private int streakMultiplier = 0;
    @Getter private ArrayList<int[]> ownedChunks = new ArrayList<>();
    @Getter private ArrayList<int[]> guardedChunks = new ArrayList<>();
    @Getter private long streakCollectDeadline = 0;
    @Getter private long streakCollectOpens = 0;

    private final World mainWorld;

    /**
     * WARNING: Only use this method for the first time the user joins the server. It sets a starting balance.
     * @param mainWorld The main world
     * @param player The player that just joined
     */
    public PlayerData(World mainWorld, Player player) {
        if (Properties.DEBUG_LOGGING) System.out.println("Creating playerdata for " + player.getDisplayName());
        
        this.username = player.getName();
        this.mainWorld = mainWorld;
        Bank.depositPlayer(player, StaticValues.PLAYERS_STARTING_BALANCE);
        if (Properties.DEV_CHEAT_MODE) {
            Bank.depositPlayer(player, 10000000);
            chunkPoints = 100;
            chunkGuardCoins = 10000;
        }
        status = LangDict.getString("playerStatus.home");
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

        status = LangDict.getString("playerStatus.offline");
    }

    public void setHomeLocation(Location location) {
        homeLocation = location;
        save();
    }

    public void setCabinLocation(Location location) {
        cabinLocation = location;
        save();
    }

    public void setShopLocation(Location location) {
        shopLocation = location;
        save();
    }

    public void addOwnedChunk(int chunkX, int chunkZ) {
        ownedChunks.add(new int[]{chunkX,chunkZ});
        System.out.println("Player now owns: " + chunkX + ":" + chunkZ);
        save();
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
        return chunkPoints > 0;
    }

    private JSONArray chunksAsJSONArray(ArrayList<int[]> chunks) {
        JSONArray ownedChunksArray = new JSONArray();
        for (int[] intArray : chunks) {
            JSONArray innerArray = new JSONArray();
            for (int value : intArray) {
                innerArray.add(value);
            }
            ownedChunksArray.add(innerArray);
        }
        return ownedChunksArray;
    }

    private ArrayList<int[]> JSONToChunkArray(JSONArray arr) {
        ArrayList<int[]> chunks = new ArrayList<>();
        if (arr == null) {
            return chunks;
        }

        for (int i = 0; i < arr.size(); i++) {
            JSONArray currentChunk = (JSONArray) arr.get(i);

            int[] currentOwned = new int[]{(int)(long) currentChunk.get(0), (int)(long) currentChunk.get(1)};
            chunks.add(currentOwned);
        }
        return chunks;
    }

    public void save() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("AvailableChunkPoints", chunkPoints);
        jsonObject.put("OwnedChunks", chunksAsJSONArray(ownedChunks));
        jsonObject.put("GuardedChunks", chunksAsJSONArray(guardedChunks));
        jsonObject.put("ChunkGuardCoins", chunkGuardCoins);
        jsonObject.put("streakMultiplier", streakMultiplier);
        jsonObject.put("streakCollectDeadline", streakCollectDeadline);
        jsonObject.put("streakCollectOpens", streakCollectOpens);

        if (homeLocation != null) {
            jsonObject.put("Home", createJSONObjectFromLocation(homeLocation));
        }

        if (cabinLocation != null) {
            jsonObject.put("Cabin", createJSONObjectFromLocation(cabinLocation));
        }

        if (shopLocation != null) {
            jsonObject.put("Shop", createJSONObjectFromLocation(shopLocation));
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
        chunkPoints = (int) ((long)obj.get("AvailableChunkPoints"));

        // Home location
        if (obj.containsKey("Home")) {
            homeLocation = getLocationFromJsonObject((JSONObject) obj.get("Home"));
        }else {
            debugLog("No Home data found for player. Must be a new player");
        }

        // Cabin location
        if (obj.containsKey("Cabin")) {
            cabinLocation = getLocationFromJsonObject((JSONObject) obj.get("Cabin"));
        }

        // Shop location
        if (obj.containsKey("Shop")) {
            shopLocation = getLocationFromJsonObject((JSONObject) obj.get("Shop"));
        }

        // Day streak
        streakMultiplier = (int) ((long) obj.get("streakMultiplier"));
        streakCollectDeadline = (long) obj.get("streakCollectDeadline");
        streakCollectOpens = (long) obj.get("streakCollectOpens");

        // Chunks owned
        ownedChunks = JSONToChunkArray((JSONArray) obj.get("OwnedChunks"));
        guardedChunks = JSONToChunkArray((JSONArray) obj.get("GuardedChunks"));
        if (obj.containsKey("ChunkGuardCoins")) {
            chunkGuardCoins = (int) ((long) obj.get("ChunkGuardCoins"));
        }
    }

    private Location getLocationFromJsonObject(JSONObject obj) {
        double x = (double) obj.get("x");
        double y = (double) obj.get("y");
        double z = (double) obj.get("z");
        double yaw = (double) obj.get("yaw");
        double pitch = (double) obj.get("pitch");
        float yawFloat = (float) yaw;
        float pitchFloat = (float) pitch;

        Location location = new Location(mainWorld, x, y, z);
        location.setYaw(yawFloat);
        location.setPitch(pitchFloat);
        return location;
    }

    private Object createJSONObjectFromLocation(Location location) {
        JSONObject locationJson = new JSONObject();
        locationJson.put("x", location.getX());
        locationJson.put("y", location.getY());
        locationJson.put("z", location.getZ());
        locationJson.put("yaw", location.getYaw());
        locationJson.put("pitch", location.getPitch());
        return locationJson;
    }

    public int getChunkPurchasePrice() {
        return ownedChunks.size()*5000;
    }

    public void updateStreak(long streakCollectOpens, long deadline, int multiplier) {
        this.streakMultiplier = multiplier;
        this.streakCollectDeadline = deadline;
        this.streakCollectOpens = streakCollectOpens;
        save();
    }

    public void addChunkPoints(int amount) {
        chunkPoints += amount;
        save();
    }

    public void withdrawChunkPoint() {
        chunkPoints--;
        save();
    }

    /**
     * Save a chunk as a force loaded chunk. This method will NOT force load it. Just save it as one.
     * @param chunk
     */
    public void chunkGuardWatchChunk(Chunk chunk) {
        guardedChunks.add(new int[]{chunk.getX(),chunk.getZ()});
        save();
    }

    /**
     * Stop watching a chunk. The chunk will no saved as a force loaded chunk. It does not unload it however
     * @param chunk
     */
    public void chunkGuardStopWatchingChunk(Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        guardedChunks.removeIf(chunkPos -> chunkPos[0] == chunkX && chunkPos[1] == chunkZ);
        save();
    }

    public void withdrawChunkGuardCoins(int amount) {
        chunkGuardCoins -= amount;
        save();
    }

    public void depositChunkGuardCoins(int amount) {
        chunkGuardCoins += amount;
        save();
    }
}
