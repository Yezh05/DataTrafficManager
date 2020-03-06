package edu.yezh.datatrafficmanager.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.yezh.datatrafficmanager.model.SimInfo;

import static android.content.Context.TELEPHONY_SERVICE;

public class SimTools {
    public static int getNowActiveNetWorkType(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo==null) {
            return -1000;
        }
        return networkInfo.getType();
    }
    public static String getNowActiveSubscriberId(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions((Activity)context, new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        }
        String imsi = mTelephonyMgr.getSubscriberId();
        if (getNowActiveNetWorkType(context)==ConnectivityManager.TYPE_WIFI){
            return "";
        }
        return  imsi;
    }

    /*
     * 获取Sim信息列表
     */
    public List<SimInfo> getSubscriptionInfoList(Context context){
        //SubscriptionManager sub = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return null;
        }
        List<SimInfo> list = new ArrayList<>();
        List<SubscriptionInfo> subscriptionInfolist = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
        for (SubscriptionInfo info : subscriptionInfolist) {
                /*Log.d("Q_M", "ICCID-->" + info.getIccId());
                Log.d("Q_M", "subId-->" + info.getSubscriptionId());
                Log.d("Q_M", "DisplayName-->" + info.getDisplayName());
                Log.d("Q_M", "CarrierName-->" + info.getCarrierName());
                Log.d("Q_M", "---------------------------------");*/
            String subscriberId = getSubscriberId(context,info.getSubscriptionId());
            SimInfo simInfo = new SimInfo(info,subscriberId);
            list.add(simInfo);
        }
        //Log.d("Sim卡信息","Sim卡数量:"+subscriptionInfolist.size());
        return list;
    }

    /*
     * 通过SimId获取IMSI
     */
    public String getSubscriberId(Context context,int subId) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);// 取得相关系统服务
        Class<?> telephonyManagerClass = null;
        String imsi = null;
        try {
            telephonyManagerClass = Class.forName("android.telephony.TelephonyManager");

            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
                Method method = telephonyManagerClass.getMethod("getSubscriberId", int.class);
                imsi = (String) method.invoke(telephonyManager, subId);
            } else if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP) {
                Method method = telephonyManagerClass.getMethod("getSubscriberId", long.class);
                imsi = (String) method.invoke(telephonyManager, (long) subId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.e("Q_M", "IMSI--" + imsi);
        return imsi;
    }
    public int getCount(Context context){
        return getSubscriptionInfoList(context).size();
    }
}
