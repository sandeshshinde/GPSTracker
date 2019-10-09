package com.badboy.gpstracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.badboy.gpstracker.model.LocationObj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Bad Boy on 1/22/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gps_tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String LOCATION_TABLE = "location_table";
    private static final String LOCATION_ID = "id";
    private static final String LOCATION_LATITUDE = "latitude";
    private static final String LOCATION_LONGITUDE = "longitude";
    private static final String LOCATION_SPEED = "speed";
    private static final String LOCATION_ACCURACY = "accuracy";
    private static final String LOCATION_DATE = "date";
    private static final String LOCATION_CREATED_DATE = "created_date";

    static final Double ACCURACY_LIMIT = 110d;


    private static final String CREATE_LOCATION_TABLE = "create table " + LOCATION_TABLE + " (id text,latitude text,longitude text,speed text,accuracy text,date text,created_date text)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertLocationData(LocationObj locationObj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String id = UUID.randomUUID().toString();
        contentValues.put(LOCATION_ID, id);
        contentValues.put(LOCATION_LATITUDE, locationObj.getLatitude());
        contentValues.put(LOCATION_LONGITUDE, locationObj.getLongitude());
        contentValues.put(LOCATION_SPEED, locationObj.getSpeed());
        contentValues.put(LOCATION_ACCURACY, locationObj.getAccuracy());
        contentValues.put(LOCATION_DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        contentValues.put(LOCATION_CREATED_DATE, new Date() + "");


        return db.insert(LOCATION_TABLE, null, contentValues);
    }

    public List<LocationObj> getAllData(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LocationObj> locationObjs = new ArrayList<LocationObj>();
        Cursor res;
        if (date.equalsIgnoreCase("")) {
            res = db.rawQuery("select * from " + LOCATION_TABLE, null);
        } else {
            res = db.rawQuery("select * from " + LOCATION_TABLE + " where " + LOCATION_DATE + " = '" + date + "' ", null);
        }
        res.moveToFirst();

        while (!res.isAfterLast()) {
            if(Double.parseDouble(res.getString(res.getColumnIndex(LOCATION_ACCURACY))) < ACCURACY_LIMIT) {
                LocationObj locationObj = new LocationObj();
                locationObj.setId(res.getString(res.getColumnIndex(LOCATION_ID)));
                locationObj.setLatitude(res.getString(res.getColumnIndex(LOCATION_LATITUDE)));
                locationObj.setLongitude(res.getString(res.getColumnIndex(LOCATION_LONGITUDE)));
                locationObj.setSpeed(res.getString(res.getColumnIndex(LOCATION_SPEED)));
                locationObj.setAccuracy(res.getString(res.getColumnIndex(LOCATION_ACCURACY)));
                locationObj.setDate(res.getString(res.getColumnIndex(LOCATION_DATE)));
                locationObj.setCreatedDate(res.getString(res.getColumnIndex(LOCATION_CREATED_DATE)));
                locationObjs.add(locationObj);
            }
            res.moveToNext();
        }
        if (!res.isClosed()) {
            res.close();
        }
        return locationObjs;
    }

    public List<LocationObj> getDataByDate() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LocationObj> locationObjs = new ArrayList<LocationObj>();
        Cursor res = db.rawQuery("select " + LOCATION_DATE + ",count(*) as points from " + LOCATION_TABLE + " group by " + LOCATION_DATE, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            LocationObj locationObj = new LocationObj();

            locationObj.setDate(res.getString(res.getColumnIndex(LOCATION_DATE)));
            locationObj.setPoints(res.getString(res.getColumnIndex("points")));
            locationObjs.add(locationObj);

            res.moveToNext();
        }
        if (!res.isClosed()) {
            res.close();
        }
        return locationObjs;
    }

    public boolean deleteByDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(LOCATION_TABLE, LOCATION_DATE + "='" + date + "'", null) > 0;
    }
}
