package com.example.inventoryapp.models;

public class ApiProduct {

    String title;
    String price;
    String image;

    public ApiProduct(String title, String price, String image) {
        this.title = title;
        this.price = price;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}