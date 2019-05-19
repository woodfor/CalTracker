package com.example.caltracker.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SearchGoogleAPI {
    private static final String API_KEY = "AIzaSyBe86ConIneErz7X_MJmUfTedRVF-PxmK8";
    private static final String API_KEY_MAP = "AIzaSyC__K7wqJSxa1iXsAP6CjW5nlaiZm3TGSg";
    private static final String SEARCH_ID_cx = "004173148851599770503:ffj8beetmg0";
    public static String search(String keyword, String[] params, String[] values) {
        keyword = keyword.replace(" ", "+");
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        String query_parameter = "";
        if (params != null && values != null) {
            for (int i = 0; i < params.length; i++) {
                query_parameter += "&";
                query_parameter += params[i];
                query_parameter += "=";
                query_parameter += values[i];
            }
        }
        //keyword  = keyword.replace(" ","%20");
        try {
            url = new URL("https://www.googleapis.com/customsearch/v1?key=" +
                    API_KEY + "&cx=" + SEARCH_ID_cx + "&q=" + keyword + query_parameter);

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult;
    }

    public static String searchNearBy(double lat, double lng, String type, int distance) {
        URL url = null;
        HttpURLConnection connection = null;
        String textResult = "";
        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    lat+","+lng + "&radius=" + distance+ "&type="+ type + "&key=" + API_KEY_MAP);

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult += scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult;
    }

    public static String getSnippet(String result){
        String snippet = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if(jsonArray != null && jsonArray.length() > 0) {
                snippet =jsonArray.getJSONObject(0).getString("snippet");
            }
        }catch (Exception e){
            e.printStackTrace();
            snippet = "NO INFO FOUND";
        }
        return snippet;
    }

    public static String getImageUrl(String result){
        String url = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if(jsonArray != null && jsonArray.length() > 0) {
               url =  jsonArray.getJSONObject(0).getJSONObject("image").getString("thumbnailLink");
            }
        }catch (Exception e){
            e.printStackTrace();
            url = null;
        }
        return url;
    }

    public static Map<String,String> getSinglePlace(JSONObject result)
    {
        Map<String,String> map = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String lat = "";
        String lng = "";
        String reference="";

        try {
            JSONObject jsonObject = result;
            if (!jsonObject.isNull("name")){
                placeName = jsonObject.getString("name");
            }
            if (!jsonObject.isNull("vicinity")){
                vicinity = jsonObject.getString("vicinity");
            }
            lat= jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lat");
            lng= jsonObject.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = jsonObject.getString("reference");
            map.put("place_name",placeName);
            map.put("vicinity",vicinity);
            map.put("lat",lat);
            map.put("lng",lng);
            map.put("reference",reference);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    return map;
    }
    public static List<Map<String,String>> getPlaces(JSONArray result)
    {
        List<Map<String,String>> mapList = new ArrayList<>();
        Map<String,String> map = new HashMap<>();

            JSONArray jsonArray = result;
            for (int i =0; i<jsonArray.length();i++)
            {
                try
                {
                    map = getSinglePlace((JSONObject) jsonArray.get(i));
                    mapList.add(map);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
       return mapList;
    }

    public static List<Map<String,String>> parse (String data)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(data);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

}
