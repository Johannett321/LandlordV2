package com.johansvartdal.landlord.stocks;

import com.johansvartdal.landlord.LangDict;
import com.johansvartdal.landlord.Main;
import com.johansvartdal.landlord.Tools;

public abstract class Stock {

    public abstract String getID();
    public abstract String getDisplayName();

    public String getDescription() {
        return LangDict.getString("stocks." + getID());
    }

    public abstract int getInitialWorth();

    public int getCurrentPrice() {
        long millis = System.currentTimeMillis();
        return (int) (getInitialWorth() * getMarketValuePercent(getID(), millis));
    }

    public int getPriceAtMillis(long millis) {
        return (int) (getInitialWorth() * getMarketValuePercent(getID(), millis));
    }

    private static double getMarketValuePercent(String flowName, long timeMillis) {
        String flow = Tools.readInternal("marketflow/" + flowName + ".csv");
        String[] flowList = flow.split(",");

        long currentMillis = timeMillis - Main.properties.getGameInitiallyStartedMillis();
        long currentMinute = currentMillis/1000/60;
        long getIndex = currentMinute % flowList.length;

        return Double.parseDouble(flowList[(int) getIndex]);
    }
}
