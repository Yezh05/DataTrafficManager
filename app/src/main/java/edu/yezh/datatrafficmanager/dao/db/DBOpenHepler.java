package edu.yezh.datatrafficmanager.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHepler extends SQLiteOpenHelper {
    private static final int VERSION =1 ;
    private static final String DBNAME = "preference.db";

    public DBOpenHepler(Context context){
        super(context,DBNAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tb_apppreference (uid varchar(100) primary key ,pkgname varchar(100),sim1IgnoreFlag integer,sim2IgnoreFlag integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
