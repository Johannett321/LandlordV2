package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;

public class TradeCenter {

    private Location location;

    public TradeCenter() {
        load();
    }

    public void setLocation(Location location) {
        this.location = location;
        save();
    }

    public void build() {
        ChunkBuilder.createChunk(location.getWorld(), location.getChunk());
    }

    public Location getLocation() {
        return location;
    }

    public void save() {
        JSONObject tradeInfo = new JSONObject();
        tradeInfo.put("xLoc", location.getX());
        tradeInfo.put("yLoc", location.getY());
        tradeInfo.put("zLoc", location.getZ());
        tradeInfo.put("yaw", location.getYaw());
        tradeInfo.put("pitch", location.getPitch());
        Tools.saveJsonToFile("TradeCenter.json", tradeInfo);
    }

    public void load() {
        JSONObject tradeInfo = Tools.loadJson("TradeCenter.json");
        if (tradeInfo == null) {
            return;
        }

        double xLoc = (double) tradeInfo.get("xLoc");
        double yLoc = (double) tradeInfo.get("yLoc");
        double zLoc = (double) tradeInfo.get("zLoc");
        double yaw = (double) tradeInfo.get("yaw");
        float pitch = (float) ((double) tradeInfo.get("pitch"));

        location = new Location(Bukkit.getWorld("world"), xLoc, yLoc, zLoc);
        location.setY(yaw);
        location.setPitch(pitch);
    }
}
