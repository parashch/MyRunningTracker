package com.ruby.rt.myrunningtracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Service;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.TooltipCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;



public class Run extends AppCompatActivity implements OnMapReadyCallback {
    public  static  final String TABLE_NAME = "running_info";

    TextView tv_Distance, tv_Time;
    Button startFAB, stopFAB, pauseFAB, listFAB;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Boolean isBound;
    Boolean tracking, active = false;
    MyLocationService ms;
    Location location, oLocation;
    SupportMapFragment supportMapFragment;
    GoogleMap gmap;
    Intent intent;
    Context cont = this;
    float dis, tot_dist = (float) 0.00;
    Handler handler;
    int Seconds, Minutes, MilliSeconds;
    private NotificationManager notificationManager;
    DatabaseHelper myDB;

    SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    String date = SDF.format(new Date());
    public static final String TAG = "MyRunningTrackerService";
    CommonClasses common = new CommonClasses();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        tv_Distance = findViewById(R.id.tv_Distance);
        tv_Time = findViewById(R.id.tv_Time);
        stopFAB = findViewById(R.id.tracking_stopFAB);
        startFAB = findViewById(R.id.tracking_startFAB);
        pauseFAB = findViewById(R.id.tracking_pauseFAB);
        listFAB = findViewById(R.id.tracking_listFAB);

        myDB = new DatabaseHelper(Run.this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class); //assign notification manager to system
        }

        handler = new Handler();
        startFAB.setVisibility(View.VISIBLE);
        pauseFAB.setVisibility(View.INVISIBLE);
        stopFAB.setVisibility(View.INVISIBLE);
        listFAB.setVisibility(View.VISIBLE);


       // onStart11();

        startFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    //  myDB.deleteAllData(TABLE_NAME);

                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    tracking = true;

                    startFAB.setVisibility(View.INVISIBLE);
                    pauseFAB.setVisibility(View.VISIBLE);
                    stopFAB.setVisibility(View.VISIBLE);
                    listFAB.setVisibility(View.INVISIBLE);

                    if (!active) {
                        SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                        date = SDF.format(new Date());
                        tv_Distance.setText("0.00m");
                        active = true;
                    }
                    Log.d(TAG, "Tracking");
                   // sendNotif();
                    oLocation = location;

                }catch (Exception e){

                    common.showMessage(Run.this, "dd",e.getMessage());

                }

            }
        });

//pause button listener
        pauseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);

                tracking = false;
              //  sendNotif();

                startFAB.setVisibility(View.VISIBLE);
                pauseFAB.setVisibility(View.INVISIBLE);
                listFAB.setVisibility(View.VISIBLE);

                Log.d(TAG, "Paused");
            }
        });




        //stop button listener
        stopFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(runnable); //stop runnable

                try {

                    common.setMessage(Run.this, "Stopped");
                    //---------------------- save data to DB   // tv_Time.getText().toString(),
                    Boolean save_data = myDB.insertData(date, tv_Distance.getText().toString(), UpdateTime, "", "");

                    if (save_data == true) {
                        common.setMessage(Run.this, "Run Data saved successfully");
                    } else {
                        common.setMessage(Run.this, "Data Not Inserted.");
                    }
                    //---------------------- save data to DB


//                Cursor c = null;
//                c.moveToNext();
                    String bestDateTime = "112233";
                    String dis = "distance";
                    long time = 11112222;

//                //if the current log is the new best time, show a dialog box
                    if ((date.equals(bestDateTime) && dis.equals(tv_Distance.getText().toString()) && (time == UpdateTime))) {
                        new AlertDialog.Builder(cont)
                                .setTitle("New Run Time")
                                .setMessage("Your Run Status. Distance: " + tv_Distance.getText().toString() + " Time: " + tv_Time.getText().toString())
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .create()
                                .show();
                        Log.d(TAG, "New best time");
                    }

                startFAB.setVisibility(View.VISIBLE);
                pauseFAB.setVisibility(View.INVISIBLE);
                stopFAB.setVisibility(View.INVISIBLE);
                listFAB.setVisibility(View.VISIBLE);

               // notificationManager.cancel(1); //clear notification

                tv_Distance.setText("");
                tv_Time.setText("");

                tot_dist = (float) 0.00;
                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Seconds = 0;
                Minutes = 0;
                MilliSeconds = 0;

                //set status to NOT TRACKING and NOT ACTIVE
                tracking = false;
                active = false;
                }catch (Exception ee){
                    common.showMessage(Run.this,"Error !", ee.getMessage());

                }


            }
        });

        listFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Run.this, ShowLog.class);
                startActivity(i);

            }
        });
    }




   @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "Starting service");

        //bind service
        intent = new Intent(this, MyLocationService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        if (checkLocationPermission()) {
            supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync((OnMapReadyCallback) this);
            Log.d(TAG, "Setting up Google Map view");

            //Broadcast receiver everytime location is updated
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            location = intent.getExtras().getParcelable("loc");
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            moveCamera(latLng, 18.5f);
                            try {
                                //if status is TRACKING, calculate new distance and display
                                if (tracking) {
                                    dis = oLocation.distanceTo(location);
                                    oLocation = location;
                                    tot_dist = dis + tot_dist;
                                    String distance = String.format("%.2f", tot_dist);
                                    tv_Distance.setText(distance + "m");
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                    , new IntentFilter("LocationBroadcastService"));
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //when app is destroyed, stop service
        handler.removeCallbacks(runnable);
        stopService(intent);
        Log.d(TAG, "Stopping service");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //do not destroy app when back button is pressed
        moveTaskToBack(true);
    }

    @SuppressLint("MissingPermission")
    //@Override
    public void onMapReady(GoogleMap googleMap) {
        //configure Google Map view
        gmap = googleMap;
        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setZoomControlsEnabled(true);
        stopService(intent);
    }

    public void moveCamera(LatLng latLng, float zoom) {
        //Google Map view animation when location is changed
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        gmap.animateCamera(cameraUpdate);
    }


    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyLocationService.mBinder binder = (MyLocationService.mBinder) service;
            ms = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //show explanation to user and ask permission again
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("Location services is needed for this app to work properly. Please allow it")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Run.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                            }
                        })
                        .create()
                        .show();
                Log.d("RunningTracker", "request permission");
            } else {
                // If first time user is being asked permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }
            return false;
        } else {
            //permission already granted
            return true;
        }
    }

    public Runnable runnable = new Runnable() {

        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime; //update time
            UpdateTime = TimeBuff + MillisecondTime; //update total time
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);

            //update time textView
            tv_Time.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };

    public void sendNotif() {
        Intent intent = getIntent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //notification channel created to support android SDK 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("runningTracker", "RunningTracker", notificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Notification notif = new NotificationCompat.Builder(this, "runningTracker")
                .setSmallIcon(R.drawable.ic_run)
                .setContentTitle("RunningTracker")
                .setContentText(tracking ? "Tracking" : "Paused")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        notificationManager.notify(1, notif); //send notification

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(Run.this, MainActivity.class);
                    startActivity(i);

                    return true;
                case R.id.navigation_dashboard:

                    Intent i2 = new Intent(Run.this, Run.class);
                    startActivity(i2);


                    // showPopup();
                    return true;
                case R.id.navigation_notifications:

                    Intent ii = new Intent(Run.this, ShowLog.class);
                    startActivity(ii);

                    return true;
            }
            return false;
        }
    };

}
