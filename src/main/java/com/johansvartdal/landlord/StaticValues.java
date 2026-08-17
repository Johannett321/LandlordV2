package com.johansvartdal.landlord;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class StaticValues {
    public static final Location GAME_START_LOCATION = new Location(Bukkit.getWorld("lladv"), 171.5, 66, -182.5);
    public static final int MAX_PLAYERS = 8;

    public static final int CAPTURE_PRICE = 6000;
    public static final int VILLAGER_CAPTURE_PRICE = 15000;
    public static final int EMISSION_TAX = 46;
    public static final int VISIT_PRICE = 1000;
    public static final int CHUNK_TAX = 300;
    public static final int MINING_PRICE = 9990;
    public static final int FLYING_PRICE_PER_MINUTE = 1690;
    public static final int PLAYERS_STARTING_BALANCE = 500;

    public static final int TREASURY_HASTE_PRICE = 15000;
    public static final int TREASURY_WITHDRAW_PRICE = 10000;
    public static final int TREASURY_CHUNK_DISCOUNT_PRICE = 100000;
    public static final int TREASURY_DONATIONS_BASE_PRICE = 15000;
    public static final int TREASURY_MYSTERY_CHEST_PRICE = 20000;
    public static final int TREASURY_DONATIONS_PRICE_PER_UNIT = 32;

    public static final int BUSINESS_INSURANCE_NEW_INSURANCE_PRICE = 100000;

    public static final int BUY_SUPPLY_CRATE_PRICE = 150000;
    public static final int MILLIONAIRE_BUY_XP = 50000;

    public static final int VILLAGER_RENT_AMOUNT_PER_LEVEL = 500;
    public static final int VILLAGER_COST_AMOUNT = 1500;

    public static final int CABIN_PRICE = 1000000;
    public static final int SHOP_PRICE = 400000;

    public static final String VERSION_TEXT = "v2.1.0";
}
