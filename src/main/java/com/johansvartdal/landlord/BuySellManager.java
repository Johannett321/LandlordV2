package com.johansvartdal.landlord;

import lombok.NonNull;
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

    public static String getSellableItemsHint() {
        return "Cobblestone, Diamond, Sugar_cane, Kelp, Bamboo, Cactus, Wheat, Dirt, Logs, Leaves, Seeds, Pumpkin, MelonSlice, Potato, Eggs";
    }

    public static class AmountWorth {
        private int amountNeeded;
        private int worth;
        private final int flow;

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

    @NonNull
    public static AmountWorth getItemValue(Material material) {
        switch (material) {
            // solid underground blocks
            case COBBLESTONE: return new AmountWorth(64, 150, 1);
            case DIRT: return new AmountWorth(64, 220, 19);
            case TUFF:
            case COBBLED_DEEPSLATE: return new AmountWorth(64, 180, 1);
            case FLINT:
            case GRAVEL:
            case DIORITE:
            case GRANITE:
            case ANDESITE: return new AmountWorth(64, 200, 2);

            // farmable
            case CACTUS: return new AmountWorth(64, 450, 3);
            case SUGAR_CANE: return new AmountWorth(64, 512, 4);
            case KELP: return new AmountWorth(64, 75, 5);
            case WHEAT: return new AmountWorth(64, 1315, 6);
            case BAMBOO: return new AmountWorth(64, 95, 7);
            case BEETROOT_SEEDS:
            case WHEAT_SEEDS: return new AmountWorth(64, 129, 8);
            case PUMPKIN: return new AmountWorth(16, 225, 9);
            case MELON_SLICE:return new AmountWorth(16, 125, 10);
            case POTATO:
            case BEETROOT:
            case CARROT: return new AmountWorth(64,615, 11);
            case COCOA_BEANS: return new AmountWorth(16,240, 12);
            case APPLE: return new AmountWorth(16, 110, 12);

            // animal farmables
            case HONEYCOMB:return new AmountWorth(16, 415, 14);
            case RABBIT_HIDE:
            case LEATHER:return new AmountWorth(25, 415, 15);
            case WHITE_WOOL:return new AmountWorth(64, 449, 16);
            case FEATHER:return new AmountWorth(32, 449, 17);
            case EGG:return new AmountWorth(16, 119, 18);
            case INK_SAC:return new AmountWorth(16, 119, 3);

            // flowers
            case CORNFLOWER:
            case POPPY:
            case BLUE_ORCHID:
            case ALLIUM:
            case AZURE_BLUET:
            case RED_TULIP:
            case ORANGE_TULIP:
            case WHITE_TULIP:
            case PINK_TULIP:
            case OXEYE_DAISY:
            case LILY_OF_THE_VALLEY:
            case TORCHFLOWER:
            case PINK_PETALS:
            case SPORE_BLOSSOM:
            case DANDELION: return new AmountWorth(16, 70, 13);

            // logs
            case OAK_LOG:
            case JUNGLE_LOG:
            case SPRUCE_LOG:
            case BIRCH_LOG: return new AmountWorth(64, 619, 20);

            // saplings
            case OAK_SAPLING:
            case BIRCH_SAPLING:
            case SPRUCE_SAPLING:
            case JUNGLE_SAPLING: return new AmountWorth(64, 250, 21);

            // leaves
            case OAK_LEAVES:
            case BIRCH_LEAVES:
            case JUNGLE_LEAVES:
            case SPRUCE_LEAVES: return new AmountWorth(16, 37, 22);

            // mob drops
            case SPIDER_EYE:
            case BONE:
            case ROTTEN_FLESH: return new AmountWorth(5, 100, 23);
            case STRING: return new AmountWorth(5, 150, 23);
            case GUNPOWDER:
            case SLIME_BALL: return new AmountWorth(5, 300, 23);

            // minerals
            case DIAMOND: return new AmountWorth(1, 2000, 23);
            case GOLD_INGOT:
            case IRON_INGOT: return new AmountWorth(16, 1500, 24);
            case CHARCOAL:
            case COAL: return new AmountWorth(16, 800, 25);
            case GLOWSTONE_DUST:
            case REDSTONE:
            case LAPIS_LAZULI: return new AmountWorth(64, 800, 26);
            case COPPER_INGOT: return new AmountWorth(16, 500, 27);
            case EMERALD: return new AmountWorth(1, 300, 28);
            case QUARTZ: return new AmountWorth(64, 800, 28);
        }
        return new AmountWorth(0, 0, 1);
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
