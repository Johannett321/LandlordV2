package com.johansvartdal.landlord;

import org.bukkit.Location;
import org.bukkit.World;

public class TradeCenter {

    int xLoc = 0;
    int zLoc = 0;
    int yLoc = 0;
    World world;

    public void setLocation(World world, int x, int y, int z) {
        this.xLoc = x;
        this.yLoc = y;
        this.zLoc = z;
        this.world = world;
    }

    public void build() {
        ChunkBuilder.createChunk(world, getLocation().getChunk());
    }

    public Location getLocation() {
        Location location = new Location(world, xLoc, yLoc, zLoc);
        return location;
    }
}
