package com.example.caltracker.IntentService;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class ScheduledIntentService extends IntentService {
    static int counter=0;
    public ScheduledIntentService() {
        super("ScheduledIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        counter++;
        Date currentTime = Calendar.getInstance().getTime();
        String strTime=currentTime.toString();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                "service", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString("service", " " + counter + " " + strTime);
        spEditor.apply();

        Log.i("message ", "The number of runs: " + counter + " times" + " " + strTime);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }
}
