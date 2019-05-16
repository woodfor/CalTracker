package com.example.caltracker.RestModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Consumption {
    Food fid;
    User uid;
    Date consumedate;
    public Consumption(Food food,User user)
    {
        this.fid = food;
        this.uid = user;
       // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        consumedate = new Date();
    }
}
