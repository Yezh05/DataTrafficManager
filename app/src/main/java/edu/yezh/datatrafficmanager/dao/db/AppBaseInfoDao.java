package edu.yezh.datatrafficmanager.dao.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.yezh.datatrafficmanager.model.tb.Tb_AppBaseInfo;

public class AppBaseInfoDao {
    private SQLiteDatabase db;
    private DBOpenHelper helper;

    public AppBaseInfoDao(Context context) {
        helper = new DBOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public void add(Tb_AppBaseInfo tb_appBaseInfo){
        db.execSQL("insert into tb_appbaseinfo(uid,pkgName) values(?,?)",new Object[]{
                tb_appBaseInfo.getUid(),tb_appBaseInfo.getPkgName()
        });

    }
    public void update(Tb_AppBaseInfo tb_appBaseInfo){
        db.execSQL("update tb_appbaseinfo set pkgName = ? where uid = ?",new Object[]{
                tb_appBaseInfo.getPkgName() ,    tb_appBaseInfo.getUid()
        });
    }

    public Tb_AppBaseInfo find(String uid){
        Cursor cursor = db.rawQuery("select * from tb_appbaseinfo where uid= ?"
                ,new String[]{uid});
        if (cursor.moveToNext()){
            return new Tb_AppBaseInfo(cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getString(cursor.getColumnIndex("pkgName")));
        }
        cursor.close();
        return null;
    }
    public List<Tb_AppBaseInfo> find(){
        Cursor cursor = db.rawQuery("select * from tb_appbaseinfo",null);

        List<Tb_AppBaseInfo> tb_appBaseInfoList = new ArrayList<>();
        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                tb_appBaseInfoList.add(new Tb_AppBaseInfo(cursor.getString(cursor.getColumnIndex("uid")),
                        cursor.getString(cursor.getColumnIndex("pkgName"))));
                cursor.moveToNext();
            }
        }else {
            return null;
        }
        return  tb_appBaseInfoList;
    }
    public void delete(String... uids){
        if (uids.length>0){
            StringBuffer sb = new StringBuffer();
            for (int i = 0;i<uids.length;i++){
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            db.execSQL("delete from tb_appbaseinfo where uid in ("+sb+")",(Object[]) uids);
        }
    }
    public int getCount(){
        Cursor cursor = db.rawQuery("select count(uid) from tb_appbaseinfo",null);
        if (cursor.moveToNext()){
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }
}
