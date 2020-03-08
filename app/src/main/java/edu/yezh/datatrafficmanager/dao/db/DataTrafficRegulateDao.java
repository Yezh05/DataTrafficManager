package edu.yezh.datatrafficmanager.dao.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import edu.yezh.datatrafficmanager.model.tb.Tb_DataTrafficRegulate;

public class DataTrafficRegulateDao {
    private SQLiteDatabase db;
    private DBOpenHelper helper;
    public  DataTrafficRegulateDao(Context context){
        helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }
    public void add(Tb_DataTrafficRegulate tb_dataTrafficRegulate){
        db.execSQL("insert into tb_datatrafficregulate(subscriberID,value,settime) values (?,?,?) "
                ,new Object[]{tb_dataTrafficRegulate.getSubscriberID(),tb_dataTrafficRegulate.getValue(),tb_dataTrafficRegulate.getSettime()});
    }
    public void update(Tb_DataTrafficRegulate tb_dataTrafficRegulate){
        db.execSQL("update tb_datatrafficregulate set value = ?,settime = ? where subscriberID=?"
                ,new Object[]{tb_dataTrafficRegulate.getValue(),tb_dataTrafficRegulate.getSettime(),tb_dataTrafficRegulate.getSubscriberID()});
    }
    public Tb_DataTrafficRegulate find(String subscriberID ){
        Cursor cursor = db.rawQuery("select * from tb_datatrafficregulate where subscriberID = ? ",new String[]{subscriberID});
        if (cursor.moveToNext()){
            return new Tb_DataTrafficRegulate(cursor.getString(cursor.getColumnIndex("subscriberID")),cursor.getLong(cursor.getColumnIndex("value")),cursor.getLong(cursor.getColumnIndex("settime")));

        }
        cursor.close();
        return null;
    }
    public void detele(String... subscriberID ){
        if (subscriberID.length>0){
            StringBuffer sb = new StringBuffer();
                    for (int i=0;i<subscriberID.length;i++){
                        sb.append('?').append(',');
                    }
                    sb.deleteCharAt(sb.length()-1);
                    db.execSQL("delete from tb_datatrafficregulate where subscriberID in ("+subscriberID+") ",(Object[]) subscriberID);
        }
    }
    public void deteleAll(){
        db.execSQL("Delete from tb_datatrafficregulate where 1=1");
    }
    public  int getCount(){
        Cursor cursor = db.rawQuery("select count(subscriberID) from tb_datatrafficregulate",null);
        if (cursor.moveToNext()){
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }

    public void close(){
        db.close();
    }
}
