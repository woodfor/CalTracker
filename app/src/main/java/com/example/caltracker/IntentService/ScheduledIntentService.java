package com.example.caltracker.IntentService;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.caltracker.API.RestClient;
import com.example.caltracker.RestModel.Report;
import com.example.caltracker.RestModel.User;
import com.example.caltracker.Room.DailyInfoDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduledIntentService extends IntentService {
    static int counter=0;
    DailyInfoDatabase db = null;
    public ScheduledIntentService() {
        super("ScheduledIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        db = Room.databaseBuilder(getApplicationContext(),
                DailyInfoDatabase.class, "dailyInfo_database")
                .fallbackToDestructiveMigration()
                .build();
        final User user = intent.getExtras().getParcelable("User");
        int steps = db.InfoDao().totalSteps(user.getUid());

        int basicBurned = RestClient.getBasicDailyCalorieBurned(user.getUid());
        double calPerStep = RestClient.getCalPerStep(user.getUid());
        Long burned = Math.round(calPerStep * steps + basicBurned);

        int consumed =  RestClient.getDailyCalorieConsumed(user.getUid());

        String goal="";
        try{
            FileInputStream fileInputStream= getApplicationContext().openFileInput(user.getUid().toString());
            if (fileInputStream!=null){
                BufferedReader bufferedReader= new BufferedReader(new
                        InputStreamReader(fileInputStream));
                String tmp = "";
                while ((tmp =bufferedReader.readLine()) != null){
                    goal+=tmp ;
                }
                fileInputStream.close();
            }
        }catch (IOException io){
            io.printStackTrace();
        }

        Report report = new Report(new Date(),consumed == -1 ? 0 : consumed,burned.intValue(),steps, goal.trim().isEmpty() ? 0 : Integer.parseInt(goal),user);
        RestClient.createReport(report);

        try{
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(user.getUid().toString(),
                    Context.MODE_PRIVATE);
            BufferedWriter bufferedWriter = new BufferedWriter(new
                    OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(" ");
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileOutputStream.close();
        }catch (IOException io){
            io.printStackTrace();
        }
        db.InfoDao().deleteAll();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }
}
