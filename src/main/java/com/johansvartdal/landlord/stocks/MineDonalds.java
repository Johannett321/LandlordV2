package com.johansvartdal.landlord.stocks;

public class MineDonalds extends Stock{

    @Override
    public String getID() {
        return "minedonalds";
    }

    @Override
    public String getDisplayName() {
        return "MineDonald's Inc";
    }

    @Override
    public int getInitialWorth() {
        return 350;
    }
}
