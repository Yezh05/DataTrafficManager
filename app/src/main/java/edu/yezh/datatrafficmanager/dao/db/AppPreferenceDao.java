package edu.yezh.datatrafficmanager.dao.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.yezh.datatrafficmanager.model.tb.Tb_AppPreference;

public class AppPreferenceDao {
    private SQLiteDatabase db;
    private DBOpenHelper helper;
    public  AppPreferenceDao(Context context){
        helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }
    public void add(Tb_AppPreference tbAppPreference){
        db.execSQL("insert into tb_apppreference(uid,pkgname,sim1IgnoreFlag,sim2IgnoreFlag) values (?,?,?,?)"
                ,new Object[]{tbAppPreference.getUid(), tbAppPreference.getPkgName(), tbAppPreference.getSim1IgnoreFlag(), tbAppPreference.getSim2IgnoreFlag()});
    }
    public void update(Tb_AppPreference tbAppPreference){
        db.execSQL("update tb_apppreference set pkgname = ?,sim1IgnoreFlag =?,sim2IgnoreFlag =? where uid = ?"
                ,new Object[]{tbAppPreference.getPkgName(), tbAppPreference.getSim1IgnoreFlag(), tbAppPreference.getSim2IgnoreFlag(), tbAppPreference.getUid()});
    }
    public Tb_AppPreference find(String uid){
        Cursor cursor = db.rawQuery("select uid,pkgname,sim1IgnoreFlag,sim2IgnoreFlag from tb_apppreference where uid = ?",new String[]{uid});
        if (cursor.moveToNext()){
            return new Tb_AppPreference( cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getString(cursor.getColumnIndex("pkgname")),
                    cursor.getInt(cursor.getColumnIndex("sim1IgnoreFlag")),
                    cursor.getInt(cursor.getColumnIndex("sim2IgnoreFlag"))
                    );
        }
        cursor.close();
        return null;
    }
    public List<Tb_AppPreference> find(){
        Cursor cursor = db.rawQuery("select * from tb_apppreference",null);
        List<Tb_AppPreference> tbAppPreferenceList = new ArrayList<>();
        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                tbAppPreferenceList.add(new Tb_AppPreference( cursor.getString(cursor.getColumnIndex("uid")),
                        cursor.getString(cursor.getColumnIndex("pkgname")),
                        cursor.getInt(cursor.getColumnIndex("sim1IgnoreFlag")),
                        cursor.getInt(cursor.getColumnIndex("sim2IgnoreFlag"))
                ));
                cursor.moveToNext();
            }
        }else {
            return null;
        }
        return tbAppPreferenceList;
    }
    public void detele(String... uids){
        if (uids.length>0){
            StringBuffer sb = new StringBuffer();
            for (int i = 0;i<uids.length;i++){
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            db.execSQL("delete from tb_apppreference where uid in ("+sb+")",(Object[]) uids);
        }
    }
    public int getCount(){
        Cursor cursor = db.rawQuery("select count(uid) from tb_apppreference",null);
        if (cursor.moveToNext()){
            return cursor.getInt(0);

        }
        cursor.close();
        return 0;
    }
}
