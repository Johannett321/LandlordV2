package com.johansvartdal.landlord;

import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;

public class TradeCenter {

    int xLoc = 0;
    int zLoc = 0;
    int yLoc = 0;
    World world;

    public TradeCenter(World world) {
        this.world = world;
        load();
    }

    public void setLocation(World world, int x, int y, int z) {
        this.xLoc = x;
        this.yLoc = y;
        this.zLoc = z;
        this.world = world;
        save();
    }

    public void build() {
        ChunkBuilder.createChunk(world, getLocation().getChunk());
    }

    public Location getLocation() {
        return new Location(world, xLoc, yLoc, zLoc);
    }

    public void save() {
        JSONObject tradeInfo = new JSONObject();
        tradeInfo.put("xLoc", xLoc);
        tradeInfo.put("yLoc", yLoc);
        tradeInfo.put("zLoc", zLoc);
        Tools.saveJsonToFile("TradeCenter.json", tradeInfo);
    }

    public void load() {
        JSONObject tradeInfo = Tools.loadJson("TradeCenter.json");
        if (tradeInfo == null) {
            return;
        }
        xLoc = (int) (long) tradeInfo.get("xLoc");
        xLoc = (int) (long) tradeInfo.get("yLoc");
        xLoc = (int) (long) tradeInfo.get("zLoc");
    }
}
