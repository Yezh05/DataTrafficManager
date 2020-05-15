package edu.yezh.datatrafficmanager.dao.sp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import edu.yezh.datatrafficmanager.model.sp.Sp_NetworkPlan;

import static android.content.Context.MODE_PRIVATE;

public class NetworkPlanDao {
    final static String SP_NAME="TrafficManager";
    Context context;
    String subscriberID;
    SharedPreferences.Editor editor;
    SharedPreferences sp;
    public NetworkPlanDao(Context context,String subscriberID) {
        this.context = context;
        this.subscriberID =subscriberID;
        Activity activity = (Activity)this.context;
        this.sp = activity.getSharedPreferences("TrafficManager", MODE_PRIVATE);
        this.editor = this.context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
    }
    public Sp_NetworkPlan getPlanData(){
        long dataPlanLong;
        int dataPlanStartDay;
        try {
             dataPlanLong = sp.getLong("dataPlan_" + subscriberID, -1L);
             dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID, 1);
        }catch (Exception e){
             e.printStackTrace();
             dataPlanLong = -1;
             dataPlanStartDay = 1;
        }
        return  new Sp_NetworkPlan(dataPlanLong,dataPlanStartDay);
    }
    public boolean setPlanData(Sp_NetworkPlan sp_networkPlan){
        try {
            editor.putLong("dataPlan_" + subscriberID, sp_networkPlan.getDataPlanLong());
            editor.putInt("dataPlanStartDay_" + subscriberID, sp_networkPlan.getDataPlanStartDay());
            editor.apply();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
