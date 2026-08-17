package com.johansvartdal.landlord.stocks;

public class NetherExpress extends Stock{

    @Override
    public String getID() {
        return "netherexpress";
    }

    @Override
    public String getDisplayName() {
        return "The Nether Express inc";
    }

    @Override
    public int getInitialWorth() {
        return 125;
    }
}
