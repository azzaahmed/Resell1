package com.app.resell;

import java.io.Serializable;

/**
 * Created by azza ahmed on 5/2/2017.
 */
public class Item implements Serializable {

    private String Description;
    private String size;
    private String price;
    private String imageUrl;
    private String userId;
    private String item_id;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String country;

    public Item() {
    }

    public Item(String description, String size, String price, String imageUrl) {
        Description = description;
        this.size = size;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {

        return Description;
    }

    public String getSize() {
        return size;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }
}

