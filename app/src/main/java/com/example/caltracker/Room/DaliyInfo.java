package com.example.caltracker.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity
public class DaliyInfo {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "uid")
    public int uid;
    @ColumnInfo(name = "steps")
    public int steps;
    @ColumnInfo(name = "time")
    public String time;

    public DaliyInfo(int uid, int steps ) {
        this.uid=uid;
        this.steps=steps;
        Date today = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        time =  dateFormat.format(today);
    }

    public int getId() {
        return id;
    }

    public int getSteps() {
        return steps;
    }

    public String getTime() {
        return time;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }



    public  void setSteps(int steps){
        this.steps = steps;
    }



}
