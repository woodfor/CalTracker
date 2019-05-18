package com.example.caltracker.API;
import android.util.Log;

import com.example.caltracker.RestModel.Consumption;
import com.example.caltracker.RestModel.Credential;
import com.example.caltracker.RestModel.Food;
import com.example.caltracker.RestModel.Report;
import com.example.caltracker.RestModel.User;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RestClient {

    private static final String BASE_URL =
            "http://192.168.0.213:8080/Food/webresources/";

    public static Boolean userExist(String username) {
        final String methodPath = "food.credential/userExist/"+username;
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
//Making HTTP request
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return  textResult.trim().equals("T") ? true : false;
    }

    public static Map<String, Integer> createUser(User user) {
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath = "food.usercal";
        Map<String, Integer> tmpMap = null;
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String stringCourseJson = gson.toJson(user);
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
//set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringCourseJson.getBytes().length);
//add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
//Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringCourseJson);
            out.close();
            Scanner inStream = new Scanner(conn.getInputStream());
            String result = "";
            while (inStream.hasNextLine()) {
                result += inStream.nextLine();
            }
            Log.i("due", result);
            tmpMap = new HashMap<String, Integer>();
            tmpMap.put("ID", Integer.parseInt(result.trim()));
            tmpMap.put("Status", conn.getResponseCode());

            //Log.i("error",new Integer(conn.getResponseCode()));
            //Log.i("error",stringCourseJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return tmpMap;
        }

    }

    public static Map<String, Integer> createCredential(Credential user){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="food.credential";
        Map<String,Integer> tmpMap = null;
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String stringCourseJson=gson.toJson(user);
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
//set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringCourseJson.getBytes().length);
//add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
//Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringCourseJson);
            out.close();
            tmpMap = new HashMap<String,Integer>();
            //tmpMap.put("ID",Integer.parseInt(result));
            tmpMap.put("Status",conn.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return tmpMap;
        }
    }

    public static User UserAuth(String username, String password){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String sha256hex = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        final String methodPath="food.credential/findUserByUsernameAndPassword/"+ username +"/"+ sha256hex;
        String textResult = " ";
        //Making HTTP request
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        Log.i("Json",textResult.trim());
        return  textResult.trim().equals("[]")? null : gson.fromJson(textResult,User.class);
    }

    public static ArrayList<Food> FoodList(){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath="food.food";
        String textResult = " ";
        //Making HTTP request
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        Log.i("Json",textResult.trim());
        return  textResult.trim().equals("[]")|| textResult.trim().isEmpty() ? null : new ArrayList<Food>(Arrays.asList(gson.fromJson(textResult,Food[].class)));
    }

    public static boolean DeleteFood(int id){
        final String methodPath = "food.food/"+id;
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        boolean flag = false;
//Making HTTP request
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            int responseCode =  new Integer(conn.getResponseCode());
            if (responseCode == 204)
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag =false;
        } finally {
            conn.disconnect();
            return  flag;
        }

    }

    public static boolean createFood(Food food) {
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath = "food.food";
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String stringCourseJson = gson.toJson(food);
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
//set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringCourseJson.getBytes().length);
//add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
//Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringCourseJson);
            out.close();
            Log.i("Status", String.valueOf(conn.getResponseCode()));
            Log.i("json",stringCourseJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return true;
        }

    }

    public static Boolean createConsumptions(List<Consumption> consumptions){
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        Boolean flag = false;
        final String methodPath="food.consumption";
        Map<String,Integer> tmpMap = null;
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String stringCourseJson=gson.toJson(consumptions);
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
//set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringCourseJson.getBytes().length);
//add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
//Send the POST out
            PrintWriter out= new PrintWriter(conn.getOutputStream());
            out.print(stringCourseJson);
            out.close();
            tmpMap = new HashMap<String,Integer>();
            //tmpMap.put("ID",Integer.parseInt(result));
            tmpMap.put("Status",conn.getResponseCode());
            if(conn.getResponseCode() == 204)
            {
                flag = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return flag;
        }
    }
    public static List<Consumption> getConsumptions(int uid) {

        final String methodPath = "food.consumption/getConsumptionByUidAndDate/"+uid+"/"+ new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
//Making HTTP request
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd").create();
        Log.i("Json",textResult.trim());
        return  textResult.trim().isEmpty() ? null : (textResult.trim().equals("[]") ? new ArrayList<Consumption>() : new ArrayList<Consumption>(Arrays.asList(gson.fromJson(textResult,Consumption[].class))));
    }

    public static boolean DeleteConsumption(int id){
        final String methodPath = "food.consumption/"+id;
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        boolean flag = false;
//Making HTTP request
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            int responseCode =  new Integer(conn.getResponseCode());
            if (responseCode == 204)
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag =false;
        } finally {
            conn.disconnect();
            return  flag;
        }

    }

    public static Integer getDailyCalorieConsumed(int uid) {
        //initialise
        URL url = null;
        Integer calorie = -1;
        HttpURLConnection conn = null;
        final String methodPath = "food.consumption/getCalorieConsumedByUidAndDate/"+uid+"/"+ new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String textResult = "";
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            if (!(conn.getResponseCode() != 200 || textResult.trim().equals(null)))
            {
                calorie =  Integer.parseInt(textResult.trim());
            }
            //Log.i("error",new Integer(conn.getResponseCode()));
            //Log.i("error",stringCourseJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return calorie;
        }

    }

    public static Integer getBasicDailyCalorieBurned(int uid) {
        //initialise
        URL url = null;
        Integer calorie = -1;
        HttpURLConnection conn = null;
        final String methodPath = "food.usercal/getDailyCalorieBurnedByUid/"+uid;
        String textResult = "";
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            if (conn.getResponseCode() == 200)
            {
                calorie =  Integer.parseInt(textResult.trim());
            }
            //Log.i("error",new Integer(conn.getResponseCode()));
            //Log.i("error",stringCourseJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return calorie;
        }

    }

    public static double getCalPerStep(int uid) {
        //initialise
        URL url = null;
        double calorie = -1;
        HttpURLConnection conn = null;
        final String methodPath = "food.usercal/getCalorieBurnedPerStepByUid/"+uid;
        String textResult = "";
        try {
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            if (conn.getResponseCode() == 200)
            {
                calorie =  Double.parseDouble(textResult.trim());
            }
            //Log.i("error",new Integer(conn.getResponseCode()));
            //Log.i("error",stringCourseJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return calorie;
        }

    }

    public static boolean createReport(Report report) {
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath = "food.report";
        try {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
            String stringCourseJson = gson.toJson(report);
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to POST
            conn.setRequestMethod("POST");
            //set the output to true
            conn.setDoOutput(true);
//set length of the data you want to send
            conn.setFixedLengthStreamingMode(stringCourseJson.getBytes().length);
//add HTTP headers
            conn.setRequestProperty("Content-Type", "application/json");
//Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringCourseJson);
            out.close();
            Log.i("Status", String.valueOf(conn.getResponseCode()));
            Log.i("json",stringCourseJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return true;
        }
    }

    public static Map<String,Integer>  getTCCTCBRC(int uid,String date) {
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath;
        String textResult = "";
        Map<String,Integer> map = null;
        try {
            methodPath = "food.report/getTCCTCBRCByUidAndDate/"+uid+"/"+date;
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            if (conn.getResponseCode() == 200)
            {
                map = new HashMap<>();
                JSONObject jsonObject = new JSONObject(textResult);
                int totalBurned = jsonObject.getInt("burned");
                int remained = jsonObject.getInt("remained");
                int consumed = jsonObject.getInt("consumed");
                map.put("Consumed",consumed);
                int tmp;
                int basicBurned = getBasicDailyCalorieBurned(uid);
                if (basicBurned != -1)
                {
                    map.put("Basic burned",basicBurned);
                    map.put("Other burned", totalBurned-basicBurned);;
                    map.put("Remained",(tmp=(remained - basicBurned))<0 ? 0:tmp);
                }
                else
                {
                    map.put("Total Burned",totalBurned);
                    map.put("Remained",remained);
                }

            }
            //Log.i("error",new Integer(conn.getResponseCode()));
            //Log.i("error",stringCourseJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            return map;
        }

    }

    public static List<Report> getReportsByPeriod(int uid,String fromDate,String endDate) {
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath;
        String textResult = "";
        try {
            methodPath = "food.report/getReportsByUidAndDates/"+uid+"/"+fromDate+"/"+endDate;
            url = new URL(BASE_URL + methodPath);
//open the connection
            conn = (HttpURLConnection) url.openConnection();
//set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
//set the connection method to GET
            conn.setRequestMethod("GET");
//add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
//Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
//read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }

            if (conn.getResponseCode() == 200)
            {
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd").create();
                return  textResult.trim().isEmpty() ? null : (textResult.trim().equals("[]") ? new ArrayList<Report>() : new ArrayList<Report>(Arrays.asList(gson.fromJson(textResult,Report[].class))));
            }
            //Log.i("error",new Integer(conn.getResponseCode()));
            //Log.i("error",stringCourseJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();

        }
        return null;
    }

}






