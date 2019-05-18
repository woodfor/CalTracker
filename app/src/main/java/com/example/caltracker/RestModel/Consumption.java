package com.example.caltracker.RestModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Consumption {
    Food fid;
    User uid;
    Date consumedate;
    int quantity;
    int id;
    public Consumption(Food food,User user,int quantity)
    {
        this.fid = food;
        this.uid = user;
        this.quantity = quantity;
       // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        consumedate = new Date();
    }

    public Food getFood() {
        return fid;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getId() {
        return id;
    }
}
