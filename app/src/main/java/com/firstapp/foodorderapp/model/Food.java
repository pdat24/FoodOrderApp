package com.firstapp.foodorderapp.model;

public class Food {

    private String id;
    private long categoryId;
    private String description;
    private double price;
    private double star;
    private boolean bestFood;
    private String imagePath;
    private int timeId;
    private String title;
    private long timeValue;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long value) {
        this.categoryId = value;
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

    public boolean getBestFood() {
        return bestFood;
    }

    public void setBestFood(boolean value) {
        this.bestFood = value;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String value) {
        this.imagePath = value;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int value) {
        this.timeId = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public String getId() {
        return id;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(long value) {
        this.timeValue = value;
    }
}
