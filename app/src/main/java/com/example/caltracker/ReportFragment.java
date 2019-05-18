package com.example.caltracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.caltracker.API.RestClient;
import com.example.caltracker.RestModel.Report;
import com.example.caltracker.RestModel.User;
import com.example.caltracker.general.Chart.MyBarChart;
import com.example.caltracker.general.Chart.MyPieChart;
import com.example.caltracker.general.tools;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportFragment extends Fragment {
    View vDisplayUnit;
    User user;
    Context mContext;
    Context appContext;
    BarChart barChart;
    PieChart pieChart;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_report, container, false);
        ((HomeActivity) getActivity())
                .setActionBarTitle("Report");
        user = getArguments().getParcelable("User");
        final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "yyyy-MM-dd";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        barChart = vDisplayUnit.findViewById(R.id.RpBarChart);
        pieChart = vDisplayUnit.findViewById(R.id.RpPieChart);

        final EditText edt_pieDate=  vDisplayUnit.findViewById(R.id.editTextRpPiedate);
        final EditText edt_barFromDate = vDisplayUnit.findViewById(R.id.editTextRpBarFromDate);
        final EditText edt_barEndDate = vDisplayUnit.findViewById(R.id.editTextRpBarEndDate);


        final DatePickerDialog.OnDateSetListener date  = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edt_pieDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                new addPieChart().execute(s.toString());
            }
        };

        edt_pieDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog =new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        edt_pieDate.addTextChangedListener(afterTextChangedListener);

        edt_barFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        edt_barFromDate.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                try {
                    dialog.getDatePicker().setMaxDate(new SimpleDateFormat("yyyy-MM-dd").parse(edt_barEndDate.getText().toString()).getTime());
                } catch (ParseException e) {
                    dialog.getDatePicker().setMaxDate(new Date().getTime());
                }
                dialog.show();
            }
        });
        edt_barEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        edt_barEndDate.setText(sdf.format(myCalendar.getTime()));
                    }
                }, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                try {
                    dialog.getDatePicker().setMinDate(new SimpleDateFormat("yyyy-MM-dd").parse(edt_barFromDate.getText().toString()).getTime());
                } catch (ParseException e) {

                }
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        TextWatcher afterTextChangedListenerForFromAndEnd = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                String from = edt_barFromDate.getText().toString();
                String end = edt_barEndDate.getText().toString();
                if (!(from.isEmpty()|| end.isEmpty()))
                {
                    String[] dates = {from,end};
                    new addGraphChart().execute(from,end);
                }
            }
        };
        edt_barFromDate.addTextChangedListener(afterTextChangedListenerForFromAndEnd);
        edt_barEndDate.addTextChangedListener(afterTextChangedListenerForFromAndEnd);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
        //pieChart.setOnChartValueSelectedListener(this);
        return vDisplayUnit;
    }

    private class addPieChart extends AsyncTask<String,Void, Map<String,Integer>>{

        @Override
        protected Map<String,Integer> doInBackground(String... strings) {

            return RestClient.getTCCTCBRC(user.getUid(),strings[0]);
        }

        @Override
        protected void onPostExecute(Map<String,Integer> result) {
            if (result!=null)
                new MyPieChart().setChart(pieChart,"Calorie Tracker",result,"Label(cal)");
            else
                tools.toast_short(mContext,"No consumptions that day");
            pieChart.invalidate();
        }
    }
    private class addGraphChart extends AsyncTask<String,Void, Integer>{

        @Override
        protected Integer doInBackground(String... strings) {
            int code = -1;
            List<Report> reportList =  RestClient.getReportsByPeriod(user.getUid(),strings[0],strings[1]);
            int basicBurned = RestClient.getBasicDailyCalorieBurned(user.getUid());
            if (reportList == null || basicBurned == -1)
                return -1;
            else if (reportList.isEmpty()){
                return  -2;
            }
            else {
                new MyBarChart().setGraphChart(mContext, barChart,reportList,basicBurned);
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == -1)
            {
                tools.toast_short(mContext,"No records found");
            }
            else if(result == -2)
            {
                tools.toast_short(mContext,"Sever Error");
            }
        }
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        appContext = getActivity().getApplicationContext();
    }
}
