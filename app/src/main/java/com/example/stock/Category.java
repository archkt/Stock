package com.example.stock;

public class Category {

    private boolean promoForCategory;
    private String name;
    private int categoryDelta = 0;

    public Category(String name, boolean promoForCategory){
        this.name = name;
        this.promoForCategory = promoForCategory;
    }

}
