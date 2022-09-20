package com.conficturus.gpsdemo;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class locationList extends Application {

    private static locationList singleton;

    private List<Location> myLocations;

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }
    public  locationList  getInstance(){
        return singleton;
    }

    //---Locations stored here---
    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
    }
    //-------------------------------


}
