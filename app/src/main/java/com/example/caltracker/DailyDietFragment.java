package com.example.caltracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caltracker.ui.login.LoginActivity;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.caltracker.RestClient.FoodList;

public class DailyDietFragment extends Fragment {
    List<HashMap<String, String>> foodListArray;
    SimpleAdapter myListAdapter;
    ListView foodList;
    HashMap<String,String> map = new HashMap<String,String>();
    String[] colHEAD = new String[] {"FoodName"};
    int[] dataCell = new int[] {R.id.textViewFoodName};
    ArrayList<Food> foods;
    Food showFood;
    Spinner spinner;
    View vDisplayUnit;
    TextView tv_fn;
    TextView tv_cal;
    TextView tv_sa;
    TextView tv_fat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_dailydiet, container, false);
        ((HomeActivity) getActivity())
                .setActionBarTitle("Your Daily Diet");
        final ImageView foodImage = vDisplayUnit.findViewById(R.id.imageViewFood);
        tv_cal = vDisplayUnit.findViewById(R.id.textViewCal);
        tv_fn = vDisplayUnit.findViewById(R.id.textViewFN);
        tv_sa = vDisplayUnit.findViewById(R.id.textViewSA);
        tv_fat = vDisplayUnit.findViewById(R.id.textViewFat);
        final Button btn_deleteFood= vDisplayUnit.findViewById(R.id.buttonDeleteFood);
        final Button bet_addFood = vDisplayUnit.findViewById(R.id.buttonAddFood);
        spinner = vDisplayUnit.findViewById(R.id.spinnerCategory);
        foodList = vDisplayUnit.findViewById(R.id.listView_food);
        foodListArray = new ArrayList<HashMap<String, String>>();
        PostAsyncTask task = new  PostAsyncTask();
        task.execute();
        foodImage.setImageResource(R.drawable.blackhole);
        tv_fn.setText("All food is \"sucked\"");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString().trim();
                foodListArray.clear();
                if(foods == null) {
                    Toast.makeText(getContext(),"Server error",Toast.LENGTH_SHORT);
                }
                else {
                    for(Food i : foods){
                        if (i.getCategory().equals(category)){
                            map=new HashMap<String, String>();
                            map.put("FoodName",i.getName());
                            foodListArray.add(map);
                        }
                    }
                    myListAdapter = new
                            SimpleAdapter(getContext(),foodListArray,R.layout.listview_food,colHEAD,dataCell);
                    foodList.setAdapter(myListAdapter);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Another interface callback
            }

        });

        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map = (Map<String,String>) parent.getItemAtPosition(position);
                String foodName = map.get("FoodName");

                for(Food i : foods)
                {
                    if (i.getName().equals(foodName))
                    {
                        showFood=i;
                    }
                }

                new DownloadImageFromInternet(foodImage)
                        .execute(foodName);
                tv_cal.setText("Calories: " + showFood.getCalamount()+"kcal");
                tv_fn.setText("Food Name: " + showFood.getName());
                tv_fat.setText("Fat: " + showFood.getFat()+"g");
                tv_sa.setText("Per "+ showFood.getServingamount()+ showFood.getServingunit());
                btn_deleteFood.setVisibility(View.VISIBLE);
                //new FatSecretAsyncTask().execute(foodName);
            }
        });

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Alert");alertDialog.setMessage("Are you sure ?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                RestClient.DeleteFood(showFood.getFid());
                                foodListArray.remove(ImmutableMap.of("FoodName", showFood.getName()));
                                foods.remove(showFood);
                                myListAdapter = new
                                        SimpleAdapter(getContext(),foodListArray,R.layout.listview_food,colHEAD,dataCell);
                                foodList.setAdapter(myListAdapter);
                                foodImage.setImageResource(R.drawable.blackhole);
                                tv_fn.setText("All food is \"sucked\"");
                                tv_cal.setText("");
                                tv_fat.setText("");
                                tv_sa.setText("");
                                btn_deleteFood.setVisibility(View.GONE);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                });

        btn_deleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });


        bet_addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edittext = new EditText(getContext());
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Food").setView(edittext).setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String foodName = edittext.getText().toString().trim();
                        boolean flag = false;
                        for (Food i : foods)
                        {
                            if (i.getName().equals(foodName))
                            {
                                flag = true;
                            }
                        }
                        if (flag == true){
                            Toast.makeText(getContext(),"This food already exists",Toast.LENGTH_SHORT);
                        }
                        else{
                           new FatSecretAsyncTask().execute(foodName);
                           new DownloadImageFromInternet(foodImage).execute(foodName);
                        }
                    }
                })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).show();
            }
        });

        return vDisplayUnit;
    }

    //Todo: check food name repeat when add


    private class PostAsyncTask extends AsyncTask<Void, Void, ArrayList<Food>>
    {
        @Override
        protected ArrayList<Food> doInBackground(Void... params) {
            foods = FoodList();
            return foods;
        }
        @Override
        protected void onPostExecute(ArrayList<Food> foods) {

            if (foods == null) {
                Toast.makeText(getContext(),"Server error",Toast.LENGTH_SHORT).show();
            } else {
                List<String> cateList = new ArrayList<String>();
                for (Food i : foods) {
                    if (!cateList.contains(i.getCategory()))
                        cateList.add(i.getCategory());
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, cateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            }
        }
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... foodNames) {
            String foodName = foodNames[0];
            String imageURL = "";
            String[] params = {"imgColorType","imgSize","imgType","num","searchType"};
            String[] values = {"color","small","photo","1","image"};
            String result = SearchGoogleAPI.search(foodName,params,values);
            imageURL = SearchGoogleAPI.getImageUrl(result);
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
    private class FatSecretAsyncTask extends AsyncTask<String, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject food = FatSecretAPI.searchFood(params[0],1);
            return food;
        }
        @Override
        protected void onPostExecute(JSONObject food) {

            if (food == null) {
                Toast.makeText(getContext(),"No Such food",Toast.LENGTH_SHORT).show();
            } else {

                JSONObject servings = null;
                try {
                    servings = food.getJSONObject("servings").getJSONObject("serving");
                } catch (JSONException e) {
                    try {
                        servings = food.getJSONObject("servings").getJSONArray("serving").getJSONObject(0);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                    final Dialog dialog = new Dialog(getContext());
                    dialog.setContentView(R.layout.dialog_food_info);
                    dialog.setTitle("Food Fetched, Please add a category");
                    final TextView dtfn = dialog.findViewById(R.id.textViewdfn);
                    final TextView dtcal = dialog.findViewById(R.id.textViewdcal);
                    final TextView dtfat = dialog.findViewById(R.id.textViewdfat);
                    final TextView dtsa = dialog.findViewById(R.id.textViewdsa);
                    final EditText edt_d = dialog.findViewById(R.id.editTextCategory);
                    String fn = "";
                    String cal = "";
                    String fat = "";
                    String sa = "";
                    String unit ="";
                    try {
                            fn = food.getString("food_name");
                            cal = servings.getString("calories");
                            fat =  servings.getString("fat");
                            sa = servings.getString("metric_serving_amount");
                            unit = servings.getString("metric_serving_unit");
                    } catch (JSONException e) {
                        try {
                            String[] tmp = servings.getString("serving_description").split(" ");
                            sa = tmp[0];unit = tmp[1];
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                    dtfn.setText("Food Name: "+ fn);
                    dtcal.setText("Calorie: " + cal);
                    dtfat.setText("Fat: " + fat);
                    dtsa.setText("Per "+ sa + unit);
                    Button btn_ok = dialog.findViewById(R.id.buttonOK);
                    Button btn_cancel = dialog.findViewById(R.id.buttonCancel);
                    final Food newFood = new Food(fn,Integer.parseInt(cal),unit,sa,new BigDecimal(fat));
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edt_d.getText().toString().trim().isEmpty())
                            {
                                edt_d.setError("Please enter category");
                            }
                            else {
                                newFood.setCategory(edt_d.getText().toString().trim());
                                //Todo: textview in main page should also change.
                                new addFood().execute(newFood);
                                dialog.dismiss();
                            }
                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();



            }

        }
    }

    private class addFood extends AsyncTask<Food, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Food... params) {

            boolean flag =  RestClient.createFood(params[0]);
            if (flag ==true)
                foods.add(params[0]);
            return flag;
        }
        @Override
        protected void onPostExecute(Boolean flag) {
            if (flag==true)
            {
                Toast.makeText(getContext(),"Food added",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"backend error",Toast.LENGTH_SHORT).show();
        }
    }
}