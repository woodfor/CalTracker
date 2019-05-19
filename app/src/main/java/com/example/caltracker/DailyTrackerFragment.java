package com.example.caltracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caltracker.API.RestClient;
import com.example.caltracker.IntentService.ScheduledIntentService;
import com.example.caltracker.RestModel.Consumption;
import com.example.caltracker.RestModel.Food;
import com.example.caltracker.RestModel.User;
import com.example.caltracker.Room.DailyInfoDatabase;
import com.example.caltracker.general.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyTrackerFragment extends Fragment {

    View vDisplayUnit;
    User user;
    private Context mContext;
    List<HashMap<String, String>> foodListArray;
    SimpleAdapter myListAdapter;
    ListView foodList;
    HashMap<String,String> map = new HashMap<String,String>();
    String[] colHEAD = new String[] {"FoodName","Calorie","Fat","Quantity"};
    int[] dataCell = new int[] {R.id.textViewLCfn,R.id.textViewcLCcal,R.id.textViewLCfat,R.id.textViewQuantity};
    List<Food> foods;
    TextView tv_goal;
    TextView tv_consume;
    TextView tv_burned;
    TextView tv_steps;
    Context appContext;
    TextView tv_basicBurned;
    DailyInfoDatabase db = null;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragmnet_dailytracker, container, false);
        ((HomeActivity) getActivity())
                .setActionBarTitle("Daily Tracker");
        user = getArguments().getParcelable("User");
        foodList = vDisplayUnit.findViewById(R.id.listView_consumption);
        foodListArray = new ArrayList<HashMap<String, String>>();
        tv_goal = vDisplayUnit.findViewById(R.id.textViewdtgoal);
        tv_steps = vDisplayUnit.findViewById(R.id.textViewdtSteps);
        tv_burned = vDisplayUnit.findViewById(R.id.textViewdtCalBurned);
        tv_consume = vDisplayUnit.findViewById(R.id.textViewdtCalConsume);
        tv_basicBurned = vDisplayUnit.findViewById(R.id.textViewBasicBurned);
        Button btn_small = vDisplayUnit.findViewById(R.id.buttonSmall);
        db = Room.databaseBuilder(appContext,
                DailyInfoDatabase.class, "dailyInfo_database")
                .fallbackToDestructiveMigration()
                .build();
        new getPersonalConsumption().execute();
        new readGoal().execute();
        new readStep().execute();
        new readCalorieConsumed().execute();
        new readBasicCalBurned().execute();
        foodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Map<String,String> map = (Map<String,String>) parent.getItemAtPosition(position);
                AlertDialog alertDialog = tools.alertDialog(mContext,"Alert","Sure delete?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new deleteConsumption().execute(Integer.parseInt(map.get("ID")));
                    }
                });
                alertDialog.show();
                return false;
            }
        });
        btn_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(appContext, ScheduledIntentService.class);
                intent.putExtras(getArguments());
                appContext.startService(intent);
                //refreshFrg();
            }
        });
        return vDisplayUnit;
    }

    private class getPersonalConsumption extends AsyncTask<Void, Void, List<Consumption>> {
        @Override
        protected List<Consumption> doInBackground(Void... params) {
           return  RestClient.getConsumptions(user.getUid());
        }
        @Override
        protected void onPostExecute(List<Consumption> tmp) {
            if (tmp == null)
                tools.toast_short(mContext,"Server error");
            else if (tmp.isEmpty())
                tools.toast_long(mContext,"Did not eat today? Eat in daily diet screen");
            else
            {
                foodListArray = new ArrayList<HashMap<String, String>>();
                for (Consumption i : tmp) {
                    map=new HashMap<String, String>();
                    map.put("FoodName",i.getFood().getName());
                    map.put("Calorie", i.getFood().getCalamount()+"");
                    map.put("Quantity", i.getQuantity()+"");
                    map.put("Fat",i.getFood().getFat()+"");
                    map.put("ID",i.getId()+"");
                    foodListArray.add(map);
                }
                myListAdapter = new
                        SimpleAdapter(mContext,foodListArray,R.layout.listview_consumption,colHEAD,dataCell);
                foodList.setAdapter(myListAdapter);
            }
        }

    }

    private class readGoal extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            String goal = "";
            try{
                FileInputStream fileInputStream= appContext.openFileInput(user.getUid().toString());
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
            return goal;
        }
        @Override
        protected void onPostExecute(String goal) {
            if (!goal.trim().isEmpty())
                tv_goal.setText("Your daily goal: " + goal+"cal");
            else
                tv_goal.setText("");
        }

    }

    private class readStep extends AsyncTask<Void,Void,Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {

            int steps = db.InfoDao().totalSteps(user.getUid());
            return  steps;
        }

        @Override
        protected void onPostExecute(Integer steps) {
            if (steps !=0)
                tv_steps.setText("Your daily steps: " + steps);
            else
                tv_steps.setText("Add entry in step screen");
        }
    }

    private class readBasicCalBurned extends AsyncTask<Void, Void,Long[]>{

        @Override
        protected Long[] doInBackground(Void... voids) {
            int basicBurned = RestClient.getBasicDailyCalorieBurned(user.getUid());
            double calPerStep = RestClient.getCalPerStep(user.getUid());
            int steps = db.InfoDao().totalSteps(user.getUid());
            Long[] tmp = {new Long(basicBurned),Math.round(calPerStep * steps)};
            return tmp;
        }

        @Override
        protected void onPostExecute(Long[] number) {
            if (number[1] != 0)
                tv_burned.setText("Your running burned:  " +  number[1] + "cal");
            tv_basicBurned.setText("Your basic burned: "+ number[0] + "cal");
        }
    }

    private class deleteConsumption extends AsyncTask<Integer,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Integer... integers) {

            return RestClient.DeleteConsumption(integers[0]);
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            if (flag == true)
            {
                refreshFrg();
            }
            else
                Toast.makeText(mContext,"Server error",Toast.LENGTH_SHORT).show();
        }
    }

    private class readCalorieConsumed extends AsyncTask<Void,Void,Integer>{

        @Override
        protected Integer doInBackground(Void... voids) {
            return RestClient.getDailyCalorieConsumed(user.getUid());
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != -1){
                tv_consume.setText("Daily Calorie consumed: " + result + "cal");
            }
            else {
                tv_consume.setText("Did not eat anything");
            }
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }
    private void refreshFrg()
    {
        String tag = this.getClass().getName();
        //remove the last two char
        if (tag.indexOf("$")!=-1)
            tag = tag.substring(0,tag.indexOf("$"));
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag(tag);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(frg).attach(frg).commit();
    }
}