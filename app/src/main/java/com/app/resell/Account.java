package com.app.resell;

import java.io.Serializable;

/**
 * Created by azza ahmed on 4/30/2017.
 */

//users table structure
public class Account implements Serializable {
    private String name;
    private String age;
    private String image_url;
    private String email;
    private String gender;
    private String mobile;
    private String country;

    private String id;

    //need empty constructor for saving in firebase
    public Account() {
    }

    public Account(String name, String image_url, String email) {
        this.name = name;
        this.image_url = image_url;
        this.email = email;

    }

    public String getCountry() {
        return country;
    }

    public Account(String name, String age, String mobile, String gender, String email, String image_url, String country) {
        this.name = name;
        this.age = age;
        this.mobile = mobile;
        this.gender = gender;
        this.email = email;
        this.image_url = image_url;
        this.country = country;


    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile() {
        return mobile;
    }


    public String getId() {
        return id;
    }

}

