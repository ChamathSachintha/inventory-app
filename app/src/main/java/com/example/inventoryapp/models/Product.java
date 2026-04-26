package com.example.inventoryapp.models;

public class Product {

    private int id;
    private String name;
    private int quantity;
    private double price;
    private String imageUri; // NEW

    // Constructor (FULL)
    public Product(int id, String name, int quantity, double price, String imageUri) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUri = imageUri;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUri() {
        return imageUri;
    }

    // Setters (optional but good for edit feature)
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}