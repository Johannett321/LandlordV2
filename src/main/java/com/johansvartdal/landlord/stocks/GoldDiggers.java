package com.johansvartdal.landlord.stocks;

public class GoldDiggers extends Stock{

    @Override
    public String getID() {
        return "golddiggers";
    }

    @Override
    public String getDisplayName() {
        return "The GoldDiggers Inc";
    }

    @Override
    public int getInitialWorth() {
        return 450;
    }
}
