package com.example.caltracker;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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

            Log.i("Status",new Integer(conn.getResponseCode()).toString());

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
        return  textResult.trim().equals("[]")? null : new ArrayList<Food>(Arrays.asList(gson.fromJson(textResult,Food[].class)));
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
            Log.i("error",new Integer(conn.getResponseCode()).toString());
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag =false;
        } finally {
            conn.disconnect();
            return  flag;
        }
        //return  textResult.trim().equals("T") ? true : false;
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
}






