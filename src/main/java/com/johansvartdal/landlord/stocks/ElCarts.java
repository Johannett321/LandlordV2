package com.johansvartdal.landlord.stocks;

public class ElCarts extends Stock{

    @Override
    public String getID() {
        return "elcarts";
    }

    @Override
    public String getDisplayName() {
        return "El-carts Inc";
    }

    @Override
    public int getInitialWorth() {
        return 1000;
    }
}
