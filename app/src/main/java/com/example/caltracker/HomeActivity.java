package com.example.caltracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caltracker.IntentService.ScheduledIntentService;
import com.example.caltracker.RestModel.User;
import com.example.caltracker.ui.login.LoginActivity;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    HomeFragment homeFragment;
    StepFragment stepFragment;
    DailyDietFragment dailyDietFragment;
    DailyTrackerFragment dailyTrackerFragment;
    ReportFragment reportFragment;
    MapFragment mapFragment;
    private AlarmManager alarmMgr;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundle = getIntent().getExtras();
        //Service and alarm
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(this, ScheduledIntentService.class);
        alarmIntent.putExtras(bundle);
        pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        alarmMgr.setRepeating(AlarmManager.RTC,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        final User user = bundle.getParcelable("User");
        getSupportActionBar().setTitle("Calorie Tracker");
        homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        stepFragment = new StepFragment();
        stepFragment.setArguments(bundle);
        dailyDietFragment = new DailyDietFragment();
        dailyDietFragment.setArguments(bundle);
        dailyTrackerFragment = new DailyTrackerFragment();
        dailyTrackerFragment.setArguments(bundle);
        reportFragment = new ReportFragment();
        reportFragment.setArguments(bundle);
        mapFragment = new MapFragment();
        mapFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, homeFragment).commit();
        View headerView = navigationView.getHeaderView(0);


        final TextView tv_Header1 = headerView.findViewById(R.id.TextViewHeader1);
        final TextView tv_Header2 = headerView.findViewById(R.id.TextViewHeader2);
        final ImageView selfe = headerView.findViewById(R.id.imageViewGender);

        if(user.getGender()=='M')
        {
            selfe.setImageResource(R.drawable.man);
        }
        else if (user.getGender() == 'F'){
            selfe.setImageResource(R.drawable.female);
        }

        tv_Header1.setText(user.getName() + user.getSurname());
        tv_Header2.setText(bundle.getString("username"));

        //goal set or not


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
            nextFragment = homeFragment;
        } else if (id == R.id.nav_display_steps) {
            nextFragment = stepFragment;
        } else if (id == R.id.nav_display_tracker) {
            nextFragment = dailyTrackerFragment;
        } else if (id == R.id.nav_display_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startService(intent);
            finish();
        }  else if (id == R.id.nav_dailyDiet){
            nextFragment = dailyDietFragment;
        }  else if (id == R.id.nav_display_report){
            nextFragment = reportFragment;
        }  else if (id == R.id.nav_display_map){
            Intent intent = new Intent(this,MapsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        if (id != R.id.nav_display_logout && id != R.id.nav_display_map)
        {
            String tag = nextFragment.getClass().getName();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content_frame,
                    nextFragment,tag).commit();
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
