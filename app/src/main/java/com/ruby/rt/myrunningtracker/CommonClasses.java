package com.ruby.rt.myrunningtracker;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonClasses {



    public void setMessage(Activity activityName, String msg){
        Toast.makeText(activityName,msg,Toast.LENGTH_LONG).show();
    }

    public  void showMessage(Activity activityName,String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activityName);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    public String getDateFormat(String datevalue,String oldFornat,String expFormat) {

        String output = "";

        if (datevalue != null && datevalue != "") {
            DateFormat inputFormat = new SimpleDateFormat(oldFornat);
            DateFormat outputFormat = new SimpleDateFormat(expFormat);

            Date date1 = null;
            try {
                date1 = inputFormat.parse(datevalue);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            output = outputFormat.format(date1);
        }else{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("dd-MMM-yyyy");
            output = mdformat.format(calendar.getTime());
        }

        return output;
    }

}
