package com.conficturus.gpsdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Array;
import java.util.List;

public class showLocationList extends AppCompatActivity {

    ListView lv_waypoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location_list);

        lv_waypoints = findViewById(R.id.lv_waypoints);

        locationList locationList = (com.conficturus.gpsdemo.locationList)getApplicationContext();
        List<Location> savedLocations = locationList.getMyLocations();

        lv_waypoints.setAdapter(new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, savedLocations));

    }
}