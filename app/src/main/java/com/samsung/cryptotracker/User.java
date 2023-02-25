package com.samsung.cryptotracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    public String email;
    public List<String> favorites = new ArrayList<>();
    public Map<String, Double> portfolio = new HashMap<>();

    public User () {

    }

    public User(String email){
        this.email = email;
    }
}
