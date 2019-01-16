package com.ruby.rt.myrunningtracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private ImageView logo_imageView;
    private PopupWindow pw;
     Button Close;

    CommonClasses common = new CommonClasses();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        logo_imageView = (ImageView) findViewById(R.id.iv_home_logo);
        logo_imageView.setVisibility(View.VISIBLE);


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            logo_imageView = (ImageView) findViewById(R.id.iv_home_logo);

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.HomeText);
                    logo_imageView.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:

                    Intent i = new Intent(MainActivity.this, Run.class);
                    startActivity(i);


                   // showPopup();
                    return true;
                case R.id.navigation_notifications:

                    Intent ii = new Intent(MainActivity.this, ShowLog.class);
                    startActivity(ii);

                    return true;
            }
            return false;
        }
    };


    public void showPopup() {
        try {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            int size_x = size.x - ((size.x*10)/100);
            int size_y = size.y- ((size.y*25)/100);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.activity_run, (ViewGroup) findViewById(R.id.id_run));
            pw = new PopupWindow(layout,size_x , size_y, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

          //  Close = (Button) layout.findViewById(R.id.btn_close_dialog);
            Close.setOnClickListener(cancel_button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancel_button = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };


}
