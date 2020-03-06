package edu.yezh.datatrafficmanager.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION =1 ;
    private static final String DB_NAME = "preference.db";

    public DBOpenHelper(Context context){
        super(context, DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tb_appbaseinfo (uid varchar(100) primary key ,pkgName varchar(100))");

        db.execSQL("create table tb_apppreference (uid varchar(100) primary key ,sim1IgnoreFlag integer,sim2IgnoreFlag integer,warningLimit integer)");

        db.execSQL("create table tb_apptransrecord (_id INTEGER PRIMARY KEY AUTOINCREMENT,uid varchar(100)  ,mobileTX integer,mobileRX integer,wifiTX integer,wifiRX integer,timeStamp integer)");

        db.execSQL("create table tb_datatrafficregulate (subscriberID varchar(100) primary key ,value integer,settime integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
