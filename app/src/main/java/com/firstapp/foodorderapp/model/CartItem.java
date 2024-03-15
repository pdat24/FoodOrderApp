package com.firstapp.foodorderapp.model;

public class CartItem {
    private String description;
    private double price;
    private double star;
    private String imagePath;
    private long timeId;
    private String title;
    private int amount;

    public CartItem(String description, double price, double star, String imagePath, long timeId, String title, int amount) {
        this.description = description;
        this.price = price;
        this.star = star;
        this.imagePath = imagePath;
        this.timeId = timeId;
        this.title = title;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double value) {
        this.price = value;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double value) {
        this.star = value;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String value) {
        this.imagePath = value;
    }

    public long getTimeId() {
        return timeId;
    }

    public void setTimeId(long value) {
        this.timeId = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
