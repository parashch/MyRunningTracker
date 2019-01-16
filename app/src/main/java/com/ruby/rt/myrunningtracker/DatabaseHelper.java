package com.ruby.rt.myrunningtracker;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parash on 12:12 24/12/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public  static  final String DATABASE_NAME = "my_running_tracker.db";
    public  static  final String TABLE_NAME = "running_info";
    public  static  final String col_1 = "id";
    public  static  final String col_2 = "datetime";
    public  static  final String col_3 = "distance";
    public  static  final String col_4 = "time";
    public  static  final String col_5 = "latitude";
    public  static  final String col_6 = "longitude";

    //SQLiteDatabase DB = this.getReadableDatabase();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
       // DB.execSQL("drop table if exists "+TABLE_NAME);
        DB.execSQL("create table "+TABLE_NAME+" ("+col_1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+col_2+" DATETIME,"+col_3+" TEXT,"+col_4+" TEXT,"+col_5+" TEXT,"+col_6+" TEXT)");

    }


    public boolean alterTable() {
       SQLiteDatabase DB = this.getReadableDatabase();
        DB.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN sn INTEGER DEFAULT 0");
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(DB);
    }


    public Boolean insertData(String now_date,String distance, Long time,String latitude,String longitude){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValue = new ContentValues();
        contentValue.put(col_2,now_date);
        contentValue.put(col_3,distance);
        contentValue.put(col_4,time);
        contentValue.put(col_5,latitude);
        contentValue.put(col_6,longitude);

        long result = db.insert(TABLE_NAME, null, contentValue);

       if(result == -1){
           return false;
       }else{
           return true;
       }
    }

    public Cursor getAlldata(String DT){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from "+TABLE_NAME+" where (substr(datetime, 0, coalesce(instr(datetime, ' '), length(datetime))) = '"+DT+"' or '"+DT+"' ='')" +
                "ORDER BY datetime desc",null);
      //  Cursor result = db.rawQuery("select station_from, station_to from "+TABLE_NAME,null);
        return result;
    }


    public   String getMyString(){
       // SQLiteDatabase db = this.getReadableDatabase();

        String ss = "select time from TABLE_NAME ORDER BY time ASC LIMIT 1";



       // Cursor result = db.rawQuery("select time from "+TABLE_NAME+ " ORDER BY time ASC LIMIT 1",null);

        return ss;
    }

    public  Cursor getBestTime(String DT){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select time from "+TABLE_NAME+ " where (substr(datetime, 0, coalesce(instr(datetime, ' '), length(datetime))) = '"+DT+"' or '"+DT+"' ='') ORDER BY CAST(time AS INTEGER) DESC LIMIT 1",null);

        return result;
    }

    public  Cursor getBadTime(String DT){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select time from "+TABLE_NAME+ " where (substr(datetime, 0, coalesce(instr(datetime, ' '), length(datetime))) = '"+DT+"' or '"+DT+"' ='') ORDER BY CAST(time AS INTEGER) LIMIT 1",null);

        return result;
    }



    public boolean  updateData(String id, String title,String instructions,String Rating){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValue = new ContentValues();

        contentValue.put(col_2,title);
        contentValue.put(col_3,instructions);

        try {
            db.update(TABLE_NAME, contentValue, "id=?", new String[]{id});

            return true;
        }catch (Exception ee){
            return false;
        }

    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValue = new ContentValues();
        Integer result =  db.delete(TABLE_NAME,"id=?",new String[]{id});
      //  Integer result =  db.delete(TABLE_NAME,null, null);
        db.execSQL("VACUUM");
      return result;

    }

    public Integer deleteAllData(String NEW_TABLE_NAME){
        SQLiteDatabase db = this.getReadableDatabase();
        Integer result =  db.delete(NEW_TABLE_NAME,null,null);
        db.execSQL("VACUUM");
        return result;

    }


    public List<String> getRecipeInfo(String ID, String TITLE,String shortBy){
        List<String> labels = new ArrayList<String>();

        String Short;

        if(shortBy == "By Title"){
                Short = "1";
        }else if(shortBy == "Top Rated"){
            Short = "6 desc";
        }else if(shortBy == "Down Rated"){
            Short = "6 ";
        }else{
            Short = "1";
        }

        // Select All Query
        String selectQuery = "SELECT upper(title),instructions,param1,param2,param3,rating,id FROM "+ TABLE_NAME+" i where (upper(i.title) like upper('%"+TITLE+"%') or upper('"+TITLE+"') = '0') ORDER BY "+Short;
        // String selectQuery = "SELECT * FROM " ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return labels;
    }




    public Cursor getRecipeInfoData(String TITLE){
        List<String> labels = new ArrayList<String>();

        String selectQuery ="SELECT upper(title),instructions,param1,param2,param3,rating,id FROM "+ TABLE_NAME+" i where upper(i.title) = upper('"+TITLE+"')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                labels.add("\n  CLASS: "+cursor.getString(0)+" ("+cursor.getString(1)+".00 TAKA)\n");
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
       // db.close();

        return  cursor;
       // return labels.toString().replace("[", "").replace("]", "").replace(",", "")+"\n ";
    }








}


