package edu.yezh.datatrafficmanager.dao.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.yezh.datatrafficmanager.model.tb.Tb_AppTransRecord;

public class AppTransRecordDao {
    private SQLiteDatabase db;
    private DBOpenHelper helper;

    public AppTransRecordDao(Context context) {
        helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void add(Tb_AppTransRecord tb_appTransRecord){
        db.execSQL("insert into tb_apptransrecord(uid,mobileTX,mobileRX,wifiTX,wifiRX,timeStamp) values(?,?,?,?,?,?)",
                new Object[]{tb_appTransRecord.getUid(),tb_appTransRecord.getMobileTX(),tb_appTransRecord.getMobileRX(),tb_appTransRecord.getWifiTX(),tb_appTransRecord.getWifiRX(),tb_appTransRecord.getTimeStamp()});
    }
    public void update(Tb_AppTransRecord tb_appTransRecord){
        db.execSQL("update tb_apptransrecord set mobileTX=?,mobileRX=?,wifiTX=?,wifiRX=?,timeStamp=? where uid = ? ",
                new Object[]{tb_appTransRecord.getMobileTX(),tb_appTransRecord.getMobileRX(),tb_appTransRecord.getWifiTX(),tb_appTransRecord.getWifiRX(),tb_appTransRecord.getTimeStamp(),tb_appTransRecord.getUid()});
    }
    public Tb_AppTransRecord findLast(String uid){
        Cursor cursor = db.rawQuery("select * from tb_apptransrecord where uid= ? ORDER BY timeStamp DESC"
                ,new String[]{uid});
        if (cursor.moveToNext()){
            return new Tb_AppTransRecord(
                        cursor.getString(cursor.getColumnIndex("uid")),
                        cursor.getLong(cursor.getColumnIndex("mobileTX")),
                        cursor.getLong(cursor.getColumnIndex("mobileRX")),
                        cursor.getLong(cursor.getColumnIndex("wifiTX")),
                        cursor.getLong(cursor.getColumnIndex("wifiRX")),
                        cursor.getLong(cursor.getColumnIndex("timeStamp"))
                        );
        }
        cursor.close();
        return null;
    }
    public List<Tb_AppTransRecord> find(String uid){
        Cursor cursor = db.rawQuery("select * from tb_apptransrecord where uid = ? ORDER BY timeStamp DESC",new String[]{uid});
        List<Tb_AppTransRecord> tb_appTransRecordList = new ArrayList<>();
        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                tb_appTransRecordList.add(new Tb_AppTransRecord(
                        cursor.getString(cursor.getColumnIndex("uid")),
                        cursor.getLong(cursor.getColumnIndex("mobileRX")),
                        cursor.getLong(cursor.getColumnIndex("mobileTX")),
                        cursor.getLong(cursor.getColumnIndex("wifiRX")),
                        cursor.getLong(cursor.getColumnIndex("wifiTX")),
                        cursor.getLong(cursor.getColumnIndex("timeStamp"))
                ));
                cursor.moveToNext();
            }
        }else {
            return null;
        }
        return  tb_appTransRecordList;
    }
    public void delete(String... uids){
        if (uids.length>0){
            StringBuffer sb = new StringBuffer();
            for (int i = 0;i<uids.length;i++){
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            db.execSQL("delete from tb_apptransrecord where uid in ("+sb+")",(Object[]) uids);
        }
    }
    public int count(String uid){
        Cursor cursor = db.rawQuery("select count(uid) from tb_apptransrecord where uid = ?",new String[]{uid});
        if (cursor.moveToNext()){
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }
}
