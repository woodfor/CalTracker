package com.example.caltracker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.caltracker.API.SearchGoogleAPI;
import com.example.caltracker.RestModel.User;
import com.example.caltracker.general.tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    User user;
    Geocoder gc;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            return;
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        user = getIntent().getExtras().getParcelable("User");
        gc = new Geocoder(this);
        mContext = this;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new searchByLocation().execute();

    }

    private class searchByLocation extends AsyncTask<Void,Void,Double[]> {

        @Override
        protected Double[] doInBackground(Void... voids) {
            List<Address> list = null;
            double lat = -34;
            double lng = 151;
            Double[] location = {lat, lng, -1.0};
            if (gc.isPresent()) {
                try {
                    list = gc.getFromLocationName(user.getAddress(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    location[0] = address.getLatitude();
                    location[1] = address.getLongitude();
                    location[2] = 0.0;

                }
            }
            return location;
        }

        @Override
        protected void onPostExecute(Double[] doubles) {
            LatLng myHome = new LatLng(doubles[0], doubles[1]);
            if (doubles[2] == 0.0) {
                mMap.addMarker(new MarkerOptions().position(myHome).title("Marker in Your Home"));
            } else {
                tools.toast_short(mContext, "Unable to get your Address");
                mMap.addMarker(new MarkerOptions().position(myHome).title("Marker in Sydney"));
            }
            new markNearPark().execute(doubles);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myHome));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        }
    }

        private class markNearPark extends AsyncTask<Double,Void,List<Map<String,String>>>{

            @Override
            protected List<Map<String, String>> doInBackground(Double... doubles) {
                String result = SearchGoogleAPI.searchNearBy(doubles[0],doubles[1],"Park",5000);
                if (!result.isEmpty())
                {
                    return SearchGoogleAPI.parse(result);
                }
                else
                    return  null;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> maps) {
                if (maps!=null)
                {
                    for (Map<String, String> map : maps)
                    {
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(Double.parseDouble(map.get("lat")),Double.parseDouble(map.get("lng")));
                        markerOptions.position(latLng);
                        markerOptions.title(map.get("place_name")+": "+ map.get("vicinity"));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        mMap.addMarker(markerOptions);
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                }

            }
        }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,9000 ).show();
            } else {
                Log.i("error", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
