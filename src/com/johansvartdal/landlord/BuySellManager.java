package com.johansvartdal.landlord;

import org.bukkit.Material;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

public class BuySellManager {

    public static String getSellableItemsString() {
        return "Cobblestone, Diamond, Sugar_cane, Kelp, Bamboo, Cactus, Wheat, Dirt, Logs, Leaves, Seeds, Pumpkin, MelonSlice";
    }

    public static class AmountWorth {
        private int amountNeeded;
        private int worth;
        private int flow;

        public AmountWorth(int amountNeeded, int worth, int flow) {
            this.amountNeeded = amountNeeded;
            this.worth = worth;
            this.flow = flow;
        }

        public int getAmountNeeded() {
            return amountNeeded;
        }

        public void setAmountNeeded(int amountNeeded) {
            this.amountNeeded = amountNeeded;
        }

        public int getWorth() {
            return (int) (worth * getMarketValuePercent(flow));
        }

        public int getWorthAtTime(long timeMillis) {
            return (int) (worth * getMarketValuePercent(flow, timeMillis));
        }

        public void setWorth(int worth) {
            this.worth = worth;
        }
    }

    public static AmountWorth getWorth (Material material) {
        switch (material) {
            case COBBLESTONE: return new AmountWorth(64, 150, 1);
            case DIAMOND: return new AmountWorth(1, 2000, 2);

            case CACTUS: return new AmountWorth(64, 610, 3);
            case SUGAR_CANE: return new AmountWorth(64, 512, 4);
            case KELP: return new AmountWorth(64, 75, 5);
            case WHEAT: return new AmountWorth(64, 1315, 6);
            case BAMBOO: return new AmountWorth(64, 95, 7);
            case BEETROOT_SEEDS:
            case WHEAT_SEEDS: return new AmountWorth(64, 129, 8);
            case PUMPKIN:return new AmountWorth(16, 225, 9);
            case MELON_SLICE:return new AmountWorth(16, 125, 10);
            case POTATO:
            case BEETROOT:
            case CARROT:return new AmountWorth(64,615, 11);


            case HONEYCOMB:return new AmountWorth(16, 415, 12);
            case LEATHER:return new AmountWorth(25, 415, 13);
            case WHITE_WOOL:return new AmountWorth(64, 449, 14);
            case FEATHER:return new AmountWorth(32, 449, 15);
            case EGG:return new AmountWorth(16, 119, 16);

            case DIRT: return new AmountWorth(64, 319, 17);
            case OAK_LOG:
            case JUNGLE_LOG:
            case SPRUCE_LOG:
            case BIRCH_LOG:
                return new AmountWorth(64, 619, 18);

            case OAK_SAPLING:
            case BIRCH_SAPLING:
            case SPRUCE_SAPLING:
            case JUNGLE_SAPLING:
                return new AmountWorth(64, 250, 19);

            case OAK_LEAVES:
            case BIRCH_LEAVES:
            case JUNGLE_LEAVES:
            case SPRUCE_LEAVES:
                return new AmountWorth(16, 37, 20);
        }
        return new AmountWorth(0, 0, 21);
    }

    public static AmountWorth getStockWorth(String displayName) {
        if (displayName.toLowerCase().contains("redstone_renegades")) {
            return new AmountWorth(1, 43, 50);
        }else if (displayName.toLowerCase().contains("iron_industries")) {
            return new AmountWorth(1, 78, 49);
        }else if (displayName.toLowerCase().contains("elcarts_inc")) {
            return new AmountWorth(1, 190, 48);
        }
        return null;
    }

    private static double getMarketValuePercent(int flowNumber) {
        return getMarketValuePercent(flowNumber, System.currentTimeMillis());
    }

    private static double getMarketValuePercent(int flowNumber, long timeMillis) {
        String flow = Tools.readInternal("marketflow/flow" + flowNumber + ".csv");
        String[] flowList = flow.split(",");

        long currentMinute = timeMillis/1000/60;
        long getIndex = currentMinute % flowList.length;

        return Double.parseDouble(flowList[(int) getIndex]);
    }
}
