package com.example.caltracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
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

import com.example.caltracker.API.FatSecretAPI;
import com.example.caltracker.API.RestClient;
import com.example.caltracker.API.SearchGoogleAPI;
import com.example.caltracker.RestModel.Consumption;
import com.example.caltracker.RestModel.Food;
import com.example.caltracker.RestModel.User;
import com.example.caltracker.general.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.caltracker.API.RestClient.FoodList;

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
    ImageView foodImage;
    ArrayList<Consumption> consumptions;
    User user;
    private Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        vDisplayUnit = inflater.inflate(R.layout.fragment_dailydiet, container, false);
        ((HomeActivity) getActivity())
                .setActionBarTitle("Your Daily Diet");
        user = getArguments().getParcelable("User");
        foodImage = vDisplayUnit.findViewById(R.id.imageViewFood);
        tv_cal = vDisplayUnit.findViewById(R.id.textViewCal);
        tv_fn = vDisplayUnit.findViewById(R.id.textViewFN);
        tv_sa = vDisplayUnit.findViewById(R.id.textViewSA);
        tv_fat = vDisplayUnit.findViewById(R.id.textViewFat);
        final Button btn_deleteFood= vDisplayUnit.findViewById(R.id.buttonDeleteFood);
        final Button bet_addFood = vDisplayUnit.findViewById(R.id.buttonAddFood);
        spinner = vDisplayUnit.findViewById(R.id.spinnerCategory);
        foodList = vDisplayUnit.findViewById(R.id.listView_food);
        foodListArray = new ArrayList<HashMap<String, String>>();
        consumptions = new ArrayList<Consumption>();

        //init spinner
        PostAsyncTask task = new  PostAsyncTask();
        task.execute();
        foodImage.setImageResource(R.drawable.blackhole);
        tv_fn.setText("All foods are \"sucked\"");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString().trim();
                foodListArray.clear();
                if(foods == null) {
                    Toast.makeText(mContext,"Server error",Toast.LENGTH_SHORT);
                }
                else {
                    for(Food i : foods){
                        if (i.getCategory().equals(category)){
                            map=new HashMap<String, String>();
                            map.put("FoodName",i.getName());
                            map.put("ID",i.getFid()+"");
                            foodListArray.add(map);
                        }
                    }
                    myListAdapter = new
                            SimpleAdapter(mContext,foodListArray,R.layout.listview_food,colHEAD,dataCell);
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
                String ID = map.get("ID");
                for(Food i : foods)
                {
                    if (i.getFid() == Integer.parseInt(ID))
                    {
                        showFood=i;
                    }
                }
                new DownloadImageFromInternet(foodImage)
                        .execute(foodName);
                tv_cal.setText("Calories: " + showFood.getCalamount()+"cal");
                tv_fn.setText("Food Name: " + showFood.getName());
                tv_fat.setText("Fat: " + showFood.getFat()+"g");
                tv_sa.setText("Per "+ showFood.getServingamount()+ showFood.getServingunit());
                //btn_deleteFood.setVisibility(View.VISIBLE);
                //new FatSecretAsyncTask().execute(foodName);
            }
        });

        foodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> map = (Map<String,String>) parent.getItemAtPosition(position);
                String foodName = map.get("FoodName");
                String ID = map.get("ID");
                for(Food i : foods)
                {
                    if (i.getFid() == Integer.parseInt(ID))
                    {
                        showFood=i;
                    }
                }
                final EditText edt_quantity = new EditText(mContext);
                edt_quantity.setInputType(InputType.TYPE_CLASS_NUMBER);
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                        .setTitle("Alert")
                        .setMessage("Eat " + foodName +"? Edit quantity below.")
                        .setView(edt_quantity)
                        .setPositiveButton("Ok",null)
                        .setNegativeButton("No",null)
                        .show();
                Button btn_positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tmp = edt_quantity.getText().toString().trim();
                        if (!(tmp == null || tmp.isEmpty()))
                        {
                            int quantity;
                            if((quantity = Integer.parseInt(edt_quantity.getText().toString())) !=0 ){
                                Consumption consumption = new Consumption(showFood,user,quantity);
                                consumptions.add(consumption);
                                alertDialog.dismiss();
                            }
                            else
                                edt_quantity.setError("the number should not be 0");
                        }
                        else
                            edt_quantity.setError("please input a number");
                    }
                });

                return true;
            }
        } );

        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Delete " + "displaying food ?");alertDialog.setMessage("Are you sure ?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                         new deleteFood().execute();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
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
                final EditText edittext = new EditText(mContext);
                final AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("Food Name: ").setView(edittext)
                        .setPositiveButton("Ok",null)
                        .setNegativeButton("No",null)
                        .show();
                Button btn_positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String foodName = edittext.getText().toString().trim();
                        boolean flag = false;
                        for (Food i : foods)
                        {

                            if (i.getName().compareToIgnoreCase(foodName)==0)
                            {
                                flag = true;
                            }
                        }
                        if (flag == true){
                            Toast.makeText(mContext,"This food already exists",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            new FatSecretAsyncTask().execute(foodName);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return vDisplayUnit;
    }

    public void onStop() {
        super.onStop();
        new addConsumption().execute();

    }

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
                Toast.makeText(mContext,"Server error",Toast.LENGTH_SHORT).show();
            } else {
                List<String> cateList = new ArrayList<String>();
                for (Food i : foods) {
                    if (!cateList.contains(i.getCategory()))
                        cateList.add(i.getCategory());
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, cateList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

            }
        }
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            //Toast.makeText(mContext, "Please wait, picture may take a while to load...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... foodNames) {
            String foodName = foodNames[0];
            String imageURL = "";
            String[] params = {"imgColorType","imgType","num","searchType"};
            String[] values = {"color","photo","1","image"};
            String result = SearchGoogleAPI.search(foodName,params,values);
            imageURL = SearchGoogleAPI.getImageUrl(result);
            Bitmap bimage = null;
            if (imageURL != null)
            {
                try {
                    InputStream in = new java.net.URL(imageURL).openStream();
                    bimage = BitmapFactory.decodeStream(in);

                } catch (Exception e) {
                    Log.e("Error Message", e.getMessage());
                    e.printStackTrace();
                }
            }

            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            if(result!=null)
                imageView.setImageBitmap(result);
            else {
                Toast.makeText(mContext,"No image found",Toast.LENGTH_SHORT).show();
                imageView.setImageResource(R.drawable.blackhole);
            }
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
        protected void onPostExecute(final JSONObject food) {

            if (food == null) {
                Toast.makeText(mContext,"No Such food",Toast.LENGTH_SHORT).show();
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
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_food_info);
                    dialog.setTitle("Food Fetched, Please add a category");
                    final TextView dtfn = dialog.findViewById(R.id.textViewdfn);
                    final TextView dtcal = dialog.findViewById(R.id.textViewdcal);
                    final TextView dtfat = dialog.findViewById(R.id.textViewdfat);
                    final TextView dtsa = dialog.findViewById(R.id.textViewdsa);
                    final EditText edt_d = dialog.findViewById(R.id.editTextCategory);
                    final ImageView image = dialog.findViewById(R.id.imageViewdf);

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
                    new DownloadImageFromInternet(image).execute(fn);
                    dtfn.setText("Food Name: "+ fn);
                    dtcal.setText("Calorie: " + cal);
                    dtfat.setText("Fat: " + fat);
                    dtsa.setText("Per "+ sa + " " + unit);
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
                                    String findcate = null;
                                    for (Food i : foods)
                                    {
                                        if (i.getCategory().compareToIgnoreCase(edt_d.getText().toString()) == 0)
                                            findcate = i.getCategory();
                                    }
                                    if (findcate!=null)
                                    {
                                        newFood.setCategory(findcate);

                                        new addFood().execute(newFood);
                                        dialog.dismiss();
                                    }
                                    else {
                                        //food name can be same,but all info can not be same.
                                        newFood.setCategory(edt_d.getText().toString().trim());
                                        if (!foods.contains(newFood)){
                                            new addFood().execute(newFood);
                                            edt_d.setText("");
                                        }
                                        else {
                                            tools.toast_short(mContext,"Food contained");
                                        }
                                        dialog.dismiss();
                                    }
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

    private class addFood extends AsyncTask<Food, Void, Food>
    {
        @Override
        protected Food doInBackground(Food... params) {
            //create successfully
            if (RestClient.createFood(params[0])==true)
            {
                foods.add(params[0]);
                return params[0];
            }
            else
                return null;

        }
        @Override
        protected void onPostExecute(Food food) {
            if (food!=null)
            {
                Toast.makeText(mContext,"Food added",Toast.LENGTH_SHORT).show();
                new DownloadImageFromInternet(foodImage).execute(food.getName());
                refreshFrg();
            }
            else
                Toast.makeText(mContext,"backend error",Toast.LENGTH_SHORT).show();
        }
    }

    private class deleteFood extends AsyncTask<Integer,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Integer... integers) {
            return RestClient.DeleteFood(showFood.getFid());
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

    private class addConsumption extends AsyncTask<Void,Void,Boolean>
    {

        @Override
        protected Boolean doInBackground(Void... voids) {
            List<Consumption> foodDistinct = new ArrayList<>();
            if (!consumptions.isEmpty())
            {
                Iterator<Consumption> iterator = consumptions.iterator();
                boolean flag;
                while (iterator.hasNext())
                {
                    flag = false;
                    Consumption i = iterator.next();
                    for (Consumption c : foodDistinct)
                    {
                        if (c.getFood().getName().equals(i.getFood().getName()))
                        {
                            c.setQuantity(c.getQuantity()+i.getQuantity());
                            flag = true;
                        }
                    }
                    if (!flag)
                        foodDistinct.add(i);
                }
                return  RestClient.createConsumptions(foodDistinct);
            }
           return false;
        }
        protected void onPostExecute(Boolean flag)
        {
            if (flag)
            {
                tools.toast_long(mContext,"Your consumptions are added into backend database");
            }

        }
    }



    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
