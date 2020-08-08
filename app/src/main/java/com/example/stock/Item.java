package com.example.stock;

import java.util.Comparator;

public class Item {
    private String name;
    private int ID;
    private int price;
    private int stock;
    private String category;
    private boolean promo = false;
    private int [] promoDetail = new int[3]; // [pStock, pPrice, pDiscount]
    private String promoDescription;
    private String description;
    public int delta;

    public Item(String category, String name, int price, int stock) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.delta = 0;
        this.promoDescription = "";
    }

    public String toString() {
        return name;
    }

    public void setID(int ID){this.ID = ID;}

    public int getID(){return ID;}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }

    public void setCategory(String category){ this.category = category; }

    public String getCategory(){ return category; }

    public void setPromo(boolean promo) {this.promo = promo;}

    public boolean isPromo() {return promo;}

    public void setPromoDetail(int [] input) {
        for(int i = 0; i < input.length; i++){
            this.promoDetail[i] = input[i];
        }
    }

    public int[] getPromoDetail() {return promoDetail;}

    public void setDescription(String description) {this.description = description;}

    public String getDescription() {return description;}

    public void setDelta(int delta){ this.delta = delta; }

    public int getDelta(){ return delta; }

    public void minusOne(){
        this.delta -= 1;
    }

    public void plusOne(){ this.delta += 1; }


    public String getPromoDescription() {return promoDescription;}

    public void setPromoDescription(String promoDescription) {this.promoDescription = promoDescription;}
}

class NameComparator implements Comparator<Item>{

    @Override
    public int compare(Item o1, Item o2) {
        return o1.getCategory().compareToIgnoreCase(o2.getCategory());
    }
}

class StockComparator implements Comparator<Item>{

    @Override
    public int compare(Item o1, Item o2) {
        return o1.getStock() - o2.getStock();
    }
}

class PriceComparator implements Comparator<Item>{

    @Override
    public int compare(Item o1, Item o2) {
        return o1.getPrice() - o2.getPrice();
    }
}