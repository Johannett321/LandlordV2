package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class StaticValues {
    public static final Location GAME_START_LOCATION = new Location(Bukkit.getWorld("lladv"), 171.5, 66, -182.5);

    public static final int CAPTURE_PRICE = 6000;
    public static final int VILLAGER_CAPTURE_PRICE = 15000;
    public static final int EMISSION_TAX = 46;
    public static final int VISIT_PRICE = 1000;
    public static final int CHUNK_TAX = 300;
    public static final int MINING_PRICE = 9990;
    public static final int FLYING_PRICE_PER_MINUTE = 10; //TODO sett en bedre pris per min
    public static final int MAX_PLAYERS = 8;
}
