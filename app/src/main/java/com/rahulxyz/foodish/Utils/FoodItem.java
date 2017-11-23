package com.rahulxyz.foodish.Utils;

/**
 * Created by raul_Will on 10/19/2017.
 */

public class FoodItem {

    String name, imageUrl;
    Integer price, quantity = 0;
    Boolean scrimToggle = false;

    public FoodItem() {
    }

    public FoodItem(String name, Integer price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public FoodItem(String name, String imageUrl, Integer price, Integer quantity) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer val) {
        this.quantity = val;
    }

    public Boolean getScrimToggle() {
        return scrimToggle;
    }

    public void setScrimToggle(Boolean scrimToggle) {
        this.scrimToggle = scrimToggle;
    }
}
