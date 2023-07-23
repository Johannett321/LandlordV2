package com.johansvartdal.landlord.stocks;

public class BlockBNB extends Stock{

    @Override
    public String getID() {
        return "blockbnb";
    }

    @Override
    public String getDisplayName() {
        return "BlockBnB";
    }

    @Override
    public int getInitialWorth() {
        return 750;
    }
}
