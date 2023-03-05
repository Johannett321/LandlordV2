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
    public String getDescription() {
        return "GoldDiggers is a gold mining company that only hire women for working in their mines. They are known for just walking past all diamonds they find, as they only mine gold.";
    }

    @Override
    public int getInitialWorth() {
        return 450;
    }
}
