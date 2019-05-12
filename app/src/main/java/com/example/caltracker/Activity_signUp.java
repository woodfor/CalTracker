package com.example.caltracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.example.caltracker.ui.login.LoginActivity;
import com.google.common.collect.Range;
import com.google.common.hash.Hashing;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.example.caltracker.RestClient.userExist;
import static java.util.Arrays.asList;

public class Activity_signUp extends AppCompatActivity {

    final Calendar myCalendar = Calendar.getInstance();
    Boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText edt_email = findViewById(R.id.editTextEmail);
        final EditText edt_dob = findViewById(R.id.editTextDOB);
        final EditText edt_address = findViewById(R.id.editTextAddress);
        final EditText edt_height = findViewById(R.id.editTextHeight);
        final EditText edt_passwd = findViewById(R.id.editTextPasswd);
        final EditText edt_SPM = findViewById(R.id.editTextSPM);
        final EditText edt_username = findViewById(R.id.editTextUserName);
        final EditText edt_weight = findViewById(R.id.editTextWeight);
        final EditText edt_postcode = findViewById(R.id.editText_postcode);
        final EditText edt_surname = findViewById(R.id.editTextUserSurname);
        final EditText edt_name = findViewById(R.id.editTextRealName);
        final RadioButton male = findViewById(R.id.radioButtonM);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
        StrictMode.setThreadPolicy(policy);
            //your codes here



        List<Integer> tmp =asList(1,2,3,4,5);
        final Spinner spinner = findViewById(R.id.spinnerLOA);
        final ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,tmp);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final EditText editTextDOB=  findViewById(R.id.editTextDOB);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                editTextDOB.setText(sdf.format(myCalendar.getTime()));
            }

        };

        editTextDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Activity_signUp.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        //validation

        final Button submitBtn = findViewById(R.id.buttonSignUp);
        //final Button clearBtn = findViewById(R.id.buttonClear);
        final AwesomeValidation validation = new AwesomeValidation(COLORATION);
        validation.setColor(Color.YELLOW);
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        validation.addValidation(edt_email,VALID_EMAIL_ADDRESS_REGEX,"Input email is invalid");
        validation.addValidation(edt_address,"^(?=\\s*\\S).*$","Address should not be empty");
        validation.addValidation(edt_passwd,"^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{6,}$","Number, letters and at least 6 length. white space is not allowed.");
        validation.addValidation(edt_dob,"^(?=\\s*\\S).*$","Birthday should not be empty");
        validation.addValidation(edt_postcode, "^(?=\\s*\\S).*$","Postcode should not be empty");
        validation.addValidation(edt_height, Range.closed(0.0f, 2.72f),"Height is empty or invalid");
        validation.addValidation(edt_weight, Range.closed(0.0f, 600.00f),"Weight is empty or invalid, should be less than 600 lbs");
        validation.addValidation(edt_SPM, Range.closed(0, 10000),"Steps per mile is empty or invalid, should be less than 10000");
        validation.addValidation(edt_username, "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{4,}$","At least 1 number,letter and 4 length. white space is not allowed");
        validation.addValidation(edt_name, "[A-Z][a-z]*","First letter should be in UpperCase");
        validation.addValidation(edt_surname,  "[A-Z]+([ '-][a-zA-Z]+)*","First letter should be in UpperCase");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
               if (validation.validate())
                {
                    final AwesomeValidation nameExist = new AwesomeValidation(COLORATION);
                    nameExist.addValidation(edt_username,new SimpleCustomValidation() {
                        @Override
                        public boolean compare(String s) {
                            return !userExist(s);
                        }
                    },"Username exists");
                    if (nameExist.validate())
                    {
                        PostAsyncTask postAsyncTask=new PostAsyncTask();
                        try {
                            User user = new User(edt_name.getText().toString(),
                                    edt_surname.getText().toString(),
                                    edt_email.getText().toString(),
                                    new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ssZ").parse(edt_dob.getText().toString()+"T00:00:00+10:00"),
                                    new BigDecimal(edt_height.getText().toString()),
                                    new BigDecimal(edt_weight.getText().toString()),
                                    male.isChecked() ? 'M' : 'F',
                                    edt_address.getText().toString(),
                                    edt_postcode.getText().toString(),
                                    spinner.getSelectedItem().toString().charAt(0),
                                    Integer.parseInt(edt_SPM.getText().toString())

                            );


                            postAsyncTask.execute(user);

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

        /*clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation.clear();



            }
        });*/

    }

    private class PostAsyncTask extends AsyncTask<User, Void, String>
    {
        EditText username = findViewById(R.id.editTextUserName);
        //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(User... params) {
            User user = params[0];
            Map<String,Integer> tmp = RestClient.createUser(user);
            String result = "";
            if (tmp == null) {
                result = "Program failure";
            } else {
               if (tmp.get("Status")==200){

                   EditText ep = findViewById(R.id.editTextPasswd);
                   String sha256hex = Hashing.sha256()
                           .hashString(ep.getText().toString(), StandardCharsets.UTF_8)
                           .toString();
                   user.setUid(tmp.get("ID"));
                   Credential credential = new Credential(username.getText().toString(),sha256hex, new Date(),user);
                   Map<String,Integer> tmpCre = RestClient.createCredential(credential);

                   if (tmpCre.get("Status")==200)
                   {
                       result = "Welcome!!"+" "+ username.getText().toString();

                   }
                   else
                   {
                       result = "Server error";
                   }

               }
               else
                   result = "Server error";
            }
            return result ;
        }
        @Override
        protected void onPostExecute(String response) {
            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
            if (response.equals("Welcome!!"+" "+ username.getText().toString()))
            {
                Intent intent = new Intent(Activity_signUp.this, LoginActivity.class);
                startActivity(intent);
            }

        }
    }




}
