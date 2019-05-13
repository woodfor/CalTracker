package com.example.caltracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.caltracker.ui.login.LoginActivity;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.example.caltracker.RestClient.FoodList;

public class DailyDietFragment extends Fragment {
    View vDisplayUnit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_dailydiet, container, false);
        ((HomeActivity) getActivity())
                .setActionBarTitle("Your Daily Diet");

        PostAsyncTask task = new  PostAsyncTask();
        task.execute();
        return vDisplayUnit;
    }


    private class PostAsyncTask extends AsyncTask<Void, Void, Food[]>
    {
        @Override
        protected Food[] doInBackground(Void... params) {
            Food[] foods = FoodList();
            return foods;
        }
        @Override
        protected void onPostExecute(Food[] foods) {

            List<String> cateList = new ArrayList<String>();
            for (Food i : foods){
                if (!cateList.contains(i.getCategory()))
                    cateList.add(i.getCategory());
            }
            final Spinner spinner =vDisplayUnit.findViewById(R.id.spinnerCategory);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,cateList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

        }
    }
}
