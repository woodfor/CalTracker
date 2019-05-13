package com.example.caltracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Navigation Drawer");
        /*FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new
                MainFragment()).commit();*/
        View headerView = navigationView.getHeaderView(0);

        Bundle bundle = getIntent().getExtras();
        final User user = bundle.getParcelable("User");
        //show name
        final TextView tv_Username = findViewById(R.id.textViewUserNameD);
        final EditText edt_goal = findViewById(R.id.editTextGoalStep);
        final Button btn_setGoal = findViewById(R.id.buttonSetGoal);
        final TextView tv_goal  = findViewById(R.id.textViewGoal);
        final Button btn_editGoal = findViewById(R.id.buttonAddGoal);
        final TextView tv_Header1 = headerView.findViewById(R.id.TextViewHeader1);
        final TextView tv_Header2 = headerView.findViewById(R.id.TextViewHeader2);
        final ImageView selfe = headerView.findViewById(R.id.imageViewGender);
        final ImageView background = findViewById(R.id.imageView2);

        tv_Username.setText("Welcome "+ user.getName());
        tv_Header1.setText(user.getName() + user.getSurname());
        tv_Header2.setText(bundle.getString("username"));
        background.setImageResource(R.drawable.run);
        if(user.getGender()=='M')
        {
            selfe.setImageResource(R.drawable.man);
        }
        else if (user.getGender() == 'F'){
            selfe.setImageResource(R.drawable.female);
        }
        //goal set or not
        String goal="";
        try{
            FileInputStream fileInputStream= openFileInput(user.getUid().toString());
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
                tv_goal.setText("Your Goal: " + goal+ " Steps");
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
                            FileOutputStream fileOutputStream = openFileOutput(user.getUid().toString(),
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
                        tv_goal.setText("Your Goal: " + goal + " Steps");
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



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment nextFragment = null;
        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_dailyDiet){
            nextFragment = new DailyDietFragment();
        }
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame,
                nextFragment).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
