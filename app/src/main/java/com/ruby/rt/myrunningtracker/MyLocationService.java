/*
Name: Parash Chowdhury
Date: 31 Dec 2018
Project: My Poject
MyService.java
Service to continuously detect location change and broadcast the new location
 */

package com.ruby.rt.myrunningtracker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MyLocationService extends Service implements LocationListener {

    public static final String TAG = "MyRunningTrackerService";

    private final Binder mBind = new mBinder();


    public MyLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBind;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //initialise location manager and listener
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationService  locationListener = new MyLocationService();


        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5, // minimum time interval between updates
                    5, // minimum distance between updates, in metres
                    locationListener);
        } catch (SecurityException e) {
        }
        return START_STICKY;
    }

    //when location change is detected
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");

        //broadcast new location
        Intent i = new Intent("LocationBroadcastService");
        i.putExtra("loc", location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class mBinder extends Binder {
        MyLocationService getService() {
            return MyLocationService.this;
        }
    }
}
