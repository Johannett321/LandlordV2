package com.johansvartdal.landlord.stocks;

public class CakeFarmers extends Stock{

    @Override
    public String getID() {
        return "cakefarm";
    }

    @Override
    public String getDisplayName() {
        return "CakeFarm Inc";
    }

    @Override
    public int getInitialWorth() {
        return 42;
    }
}
