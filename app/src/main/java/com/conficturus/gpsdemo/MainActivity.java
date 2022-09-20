package com.conficturus.gpsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Constants
    public static final int PERMISSIONS_FINE_LOCATION = 75;

    // UI Elements Reference

    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address, tv_waypoint;
    Switch sw_locationupdates, sw_gps;
    Button  btn_newwaypoint, btn_showwaypointmap;
    //Button btn_showwaypointlist;  Disabled because these variables are useless to user
    //--------------Variables----------------
    // Tells if tracking is on or off
    boolean updateOn = false;

    //Current Location
    Location currentLocation;

    //Saved Locations
    List<Location> savedLocations;

    //Location Services API
    FusedLocationProviderClient fusedLocationProviderClient;

    //LocationRequest is a config file for all settings related to FusedLocation...
    LocationRequest locationRequest;

    //LocationCallback is an event that triggers a refresh in location after the update variable has been met
    LocationCallback locationCallBack;

    //------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //----UI given variables here:---

        //TextView
        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        tv_waypoint = findViewById(R.id.tv_waypoint);

        //Switches
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);

        //Buttons
        btn_newwaypoint = findViewById(R.id.btn_newwaypoint);
        btn_showwaypointmap = findViewById(R.id.btn_showwaypointmap);
       // btn_showwaypointlist = findViewById(R.id.btn_showwaypointlist);

        //---------------------------------------------------------------

        // Properties of LocationRequest
        locationRequest = new LocationRequest();;
        locationRequest.setInterval(30000); //Normal Refresh
        locationRequest.setFastestInterval(5000); //Refresh on high power mode
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallBack = new LocationCallback(){

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Save the Location
                updateUIvalues(locationResult.getLastLocation());
            }
        };

        //onClickListeners of UI elements here:

        btn_newwaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Grabs current GPS location



                //Adds this location to the global array at class locationList
                locationList locationList = (com.conficturus.gpsdemo.locationList) getApplicationContext();
                savedLocations = locationList.getMyLocations();
                savedLocations.add(currentLocation);
            }
        });

       // btn_showwaypointlist.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
       //         Intent i = new Intent(MainActivity.this, showLocationList.class);
       //         startActivity(i);
       //     }
       // });

        btn_showwaypointmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, waypointMap.class);
                startActivity(i);
            }
        });


        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    //When sw_gps is on, it uses more power and highest accuracy
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS, Towers and WiFI");
                }
                else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers and WiFi");
                }
            }
        });
        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_locationupdates.isChecked()) {
                    //Turn on Location Tracking
                    startLocationUpdates();
                }
                else {
                    //Turn off Location Tracking
                    stopLocationUpdates();
                }
            }
        });


        updateGPS();
    } //End of the onCreate (Global Functions)


    private void stopLocationUpdates() {
        tv_updates.setText("Location is no longer tracked");
        tv_lat.setText("No values here... your location tracking is off");
        tv_lon.setText("No values here... your location tracking is off");
        tv_speed.setText("No values here... your location tracking is off");
        tv_address.setText("No values here... your location tracking is off");
        tv_altitude.setText("No values here... your location tracking is off");
        tv_sensor.setText("No values here... your location tracking is off");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);//Error suppressed here: Call requires permission which may be rejected by user: code should explicitly check to see if permission is available (with `checkPermission`) or explicitly handle a potential `SecurityException`
       //If user rejects multiple times, the app will close and will not open until reinstalled due to this Security Exception
        updateGPS();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    updateGPS();;
                }
                else {
                    Toast.makeText(this, "When an app is called GPSDemo, you'd think it needs location permissions, no?", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    //Permissions Area

    private void updateGPS() {
        //This method, assuming perms have been given by user, will get current location from fused client
        //and updates the readings (associated with the TextView items) respectively.
        ;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Location is going to be used (obviously) and app checks if it has been granted it before
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                //Perms have been (already) granted. Values of location will be put in its respective TextView variable and the current location will be saved to "currentLocation"
                    updateUIvalues(location);
                    currentLocation = location;

                }
            });
        }
        else {
            //Perms not granted yet, but the app will request it to be granted, and assuming the user isn't an idiot, they will allow it.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIvalues(Location location) {
        //Update all TextView objects with new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));

        if  (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Not Available");
        }
        if (location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_speed.setText("Not Available");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e ) {
            tv_address.setText("Can't seem to get a street address...try again later!");
        }
        locationList locationList = (com.conficturus.gpsdemo.locationList) getApplicationContext();
        savedLocations = locationList.getMyLocations();

        tv_waypoint.setText(Integer.toString(savedLocations.size()));

    }
}