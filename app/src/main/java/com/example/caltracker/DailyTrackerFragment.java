package com.example.caltracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.caltracker.RestModel.Consumption;
import com.example.caltracker.RestModel.User;
import com.example.caltracker.general.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DailyTrackerFragment extends Fragment {

    View vDisplayUnit;
    User user;
    List<HashMap<String, String>> stepListArray;
    SimpleAdapter myListAdapter;
    ListView stepList;
    HashMap<String,String> map = new HashMap<String,String>();
    String[] colHEAD = new String[] {"FoodName","Calorie","Fat"};
    int[] dataCell = new int[] {R.id.textViewLCfn,R.id.textViewcLCcal,R.id.textViewLCfat};
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_step, container, false);
        ((HomeActivity) getActivity())
                .setActionBarTitle("Daily Tracker");
        user = getArguments().getParcelable("User");

        return vDisplayUnit;
    }

    private class getPersonalConsumption extends AsyncTask<Void, Void, List<Consumption>> {
        @Override
        protected List<Consumption> doInBackground(Void... params) {
                //Todo: rest client;
                return null;
        }
        @Override
        protected void onPostExecute(List<Consumption> infos) {


        }
    }
}