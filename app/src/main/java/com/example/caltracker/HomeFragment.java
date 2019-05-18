package com.example.caltracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caltracker.RestModel.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class HomeFragment extends Fragment {
    View vDisplayUnit;
    private Context mContext;
    private Context appContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView tv_Username = vDisplayUnit.findViewById(R.id.textViewUserNameD);
        final EditText edt_goal = vDisplayUnit.findViewById(R.id.editTextGoalStep);
        final Button btn_setGoal = vDisplayUnit.findViewById(R.id.buttonSetGoal);
        final TextView tv_goal  = vDisplayUnit.findViewById(R.id.textViewGoal);
        final Button btn_editGoal =vDisplayUnit.findViewById(R.id.buttonAddGoal);
        final ImageView background = vDisplayUnit.findViewById(R.id.imageView2);
        final User user = getArguments().getParcelable("User");
        tv_Username.setText("Welcome "+ user.getName());
        background.setImageResource(R.drawable.run);

        ((HomeActivity) getActivity())
                .setActionBarTitle("Calorie tracker");
        String goal="";

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
        if(goal == null || goal.trim().isEmpty())
        {
            edt_goal.setVisibility(View.VISIBLE);
            btn_setGoal.setVisibility(View.VISIBLE);
            tv_goal.setVisibility(View.GONE);
            btn_editGoal.setVisibility(View.GONE);
        }
        else {
            tv_goal.setText("Your Goal: " + goal+ " Calories");
        }

        btn_setGoal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String goal = edt_goal.getText().toString().trim();
                if (goal==null|| goal.isEmpty())
                {
                    edt_goal.setError("Input and Run !");
                }
                else {
                    try{
                        FileOutputStream fileOutputStream = appContext.openFileOutput(user.getUid().toString(),
                                Context.MODE_PRIVATE);
                        BufferedWriter bufferedWriter = new BufferedWriter(new
                                OutputStreamWriter(fileOutputStream));
                        bufferedWriter.write(edt_goal.getText().toString());
                        bufferedWriter.newLine();
                        bufferedWriter.close();
                        fileOutputStream.close();
                    }catch (IOException io){
                        io.printStackTrace();
                    }

                    edt_goal.setVisibility(View.GONE);
                    btn_setGoal.setVisibility(View.GONE);
                    tv_goal.setVisibility(View.VISIBLE);
                    btn_editGoal.setVisibility(View.VISIBLE);
                    tv_goal.setText("Your Goal: " + goal + " Calories");
                }

            }
        });

        btn_editGoal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                edt_goal.setVisibility(View.VISIBLE);
                btn_setGoal.setVisibility(View.VISIBLE);
                tv_goal.setVisibility(View.GONE);
                btn_editGoal.setVisibility(View.GONE);

            }
        });


        // DailyDietFragment.PostAsyncTask task = new DailyDietFragment.PostAsyncTask();
        //task.execute();
        return vDisplayUnit;
    }

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }
}
