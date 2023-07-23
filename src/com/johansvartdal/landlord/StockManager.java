package com.johansvartdal.landlord;

import com.johansvartdal.landlord.stocks.*;

import java.util.ArrayList;

public class StockManager {

    public static ArrayList<Stock> stockList = new ArrayList<>();

    public static void loadStocks() {
        stockList.add(new ElCarts());
        stockList.add(new CakeFarmers());
        stockList.add(new GoldDiggers());
        stockList.add(new BlockBNB());
        stockList.add(new MineDonalds());
        stockList.add(new NetherExpress());
    }

    public static Stock getStockByID(String id) {
        for (Stock stock : stockList) {
            if (stock.getID().equals(id)) {
                return stock;
            }
        }
        return null;
    }

    public static ArrayList<Stock> getAllStocks() {
        return stockList;
    }
}
