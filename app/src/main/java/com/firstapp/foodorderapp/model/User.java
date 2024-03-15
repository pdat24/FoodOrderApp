package com.firstapp.foodorderapp.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String uid;
    public String email;
    public String password;
    public List<CartItem> cart;

    public User(String uid, String email, String password) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        cart = new ArrayList<>();
    }
}
