package com.example.caltracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caltracker.ui.login.LoginActivity;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static com.example.caltracker.RestClient.FoodList;

public class DailyDietFragment extends Fragment {
    View vDisplayUnit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_dailydiet, container, false);
        PostAsyncTask task = new  PostAsyncTask();
        task.execute();
        return vDisplayUnit;
    }


    private class PostAsyncTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params) {
            Food[] foods = FoodList();
            for(Food i : foods)
            {
                System.out.println(i.getName());
            }
            return null;
        }
        @Override
        protected void onPostExecute(String response) {


        }
    }
}
