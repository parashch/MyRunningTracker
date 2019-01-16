package com.ruby.rt.myrunningtracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ShowLog extends AppCompatActivity {
    DatabaseHelper myDB;
    public  static  final String DATABASE_NAME = "my_running_tracker.db";
    public  static  final String TABLE_NAME = "running_info";

    CommonClasses common = new CommonClasses();


    EditText eText;
    DatePickerDialog picker;
    ImageButton sBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_log);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        myDB = new DatabaseHelper(this);
        TableLayout tableLayout = findViewById(R.id.tablelayout);

        Context context = this;
        TableRow rowHeader = new TableRow(context);
        rowHeader.setBackgroundColor(Color.parseColor("#0f8221"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        String[] headerText = {"Date", "Time", "Distance"};
        for (String c : headerText) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(5, 8, 5, 8);
            tv.setText(c);
            rowHeader.addView(tv);
        }
        tableLayout.addView(rowHeader);



        String strDate = common.getDateFormat(null,null,null);

//        common.showMessage(ShowLog.this,"sdfsd",strDate);
        loadTblData(strDate);




        //--------------------------------datepicker


        eText=(EditText) findViewById(R.id.txt_date);
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ShowLog.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText( "00".substring(Integer.toString(dayOfMonth).length()) + dayOfMonth + "/" + "00".substring(Integer.toString((monthOfYear + 1)).length()) + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        sBtn=(ImageButton) findViewById(R.id.btn_search);
        sBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputDateStr= eText.getText().toString();
                if(inputDateStr != null) {

                    String searchDate = common.getDateFormat(inputDateStr,"dd/mm/yyyy","dd-MMM-yyyy");

                    loadTblData(searchDate);

                }else {

                    common.setMessage(ShowLog.this, "Please select a date !");
                }


            }
        });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(ShowLog.this, MainActivity.class);
                    startActivity(i);

                    return true;
                case R.id.navigation_dashboard:

                    Intent i2 = new Intent(ShowLog.this, Run.class);
                    startActivity(i2);


                    // showPopup();
                    return true;
                case R.id.navigation_notifications:

                    Intent ii = new Intent(ShowLog.this, ShowLog.class);
                    startActivity(ii);

                    return true;
            }
            return false;
        }
    };


    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }

public  void loadTblData(String DT){



    TableLayout tableLayout = findViewById(R.id.tablelayout);

    cleanTable(tableLayout);

    try {
        Cursor c = myDB.getBestTime(DT);
        Cursor b = myDB.getBadTime(DT);

        SimpleDateFormat SDF = new SimpleDateFormat("MMM-yyyy");
        String date = SDF.format(new Date()); //format date and save as string

        Cursor cursor  = myDB.getAlldata(DT);

        TextView txt_count = (TextView) findViewById(R.id.txt_count);

        txt_count.setText(Integer.toString(cursor.getCount()));


        if (cursor.getCount() > 0) {
            //get best time
            c.moveToNext();
            String bestTime_o = c.getString(c.getColumnIndex("time"));
            long bestTime = Long.parseLong(bestTime_o);

            b.moveToNext();
            String badTime_o = b.getString(c.getColumnIndex("time"));
            long badTime = Long.parseLong(badTime_o);

            while (cursor.moveToNext()) {

                // Read columns data
                String runDateTime = cursor.getString(cursor.getColumnIndex("datetime"));
                String runDistance = cursor.getString(cursor.getColumnIndex("distance"));
                String runTime_o = cursor.getString(cursor.getColumnIndex("time"));
                long runTime =  Long.parseLong(runTime_o);




//                    DateFormat inputFormat = new SimpleDateFormat("HH:mm dd-MMM-yyyy");
//                    DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
//                    String inputDateStr= runDateTime;
//                    Date date1 = inputFormat.parse(inputDateStr);
//                    String outputDateStr = outputFormat.format(date1);
//
//                    runDateTime =outputDateStr;


                //format time to string to be displayed
                long Seconds = (int) (runTime / 1000);
                long Minutes = Seconds / 60;
                Seconds = Seconds % 60;
                long MilliSeconds = (int) (runTime % 1000);
                String runTimetxt = "" + Minutes + ":" + String.format("%02d", Seconds) + ":" + String.format("%03d", MilliSeconds);

                // dara rows
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                //if log contains best time, highlight in green color
                if (runTime == bestTime) {
                    row.setBackgroundColor(Color.parseColor("#4cc844"));
                }

                if (runTime == badTime) {
                    row.setBackgroundColor(Color.parseColor("#c43569"));
                }



                String[] colText = {runDateTime , runTimetxt, runDistance};
                for (String text : colText) {
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(16);
                    tv.setPadding(5, 5, 5, 5);
                    tv.setText(text);
                    row.addView(tv);
                }
                tableLayout.addView(row);
            }
        }else{
            common.showMessage(ShowLog.this,"Sorry !", "No Data Found.");
        }
    } catch (Exception e) {
        //   e.printStackTrace();
        common.showMessage(ShowLog.this,"Exception!", e.getMessage());
    }


}






}
