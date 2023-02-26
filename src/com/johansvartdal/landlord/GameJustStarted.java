package com.johansvartdal.landlord;

import org.bukkit.World;

public class GameJustStarted {

    World mainWorld;

    public GameJustStarted(World mainWorld) {
        this.mainWorld = mainWorld;
    }

    public void doStart() {
        Main.tradeCenter.build();
        // Calculate position of chunk for each player
        // Run ChunkBuilder.createChunk() to create a chunk there
    }
}
