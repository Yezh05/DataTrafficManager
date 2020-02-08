package edu.yezh.datatrafficmanager.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.R;

public class InstalledAppsInfoTools {
    public List<Map<String,String>> getAllInstalledAppsInfo(Context context) {
        List<Map<String,String>> allInstalledAppsInfo = new ArrayList<>();

        //List<String> uidList = new ArrayList<String>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packinfos) {
            String[] premissions = info.requestedPermissions;
            if (premissions != null && premissions.length > 0) {
                for (String premission : premissions) {
                    if ("android.permission.INTERNET".equals(premission)) {
                        int uid = info.applicationInfo.uid;
                        String packageName = pm.getNameForUid(uid);
                        if (packageName.indexOf("system")!=-1||packageName.indexOf("android")!=-1||packageName.indexOf("huawei")!=-1){
                            continue;
                        }
                        Map<String,String> singleAppInfo = new HashMap<>();
                        singleAppInfo.put("uid",String.valueOf(uid));
                        singleAppInfo.put("pkgname",packageName);
                        //System.out.println("uid = " + uid);
                        //System.out.println("pkgname = " + name);
                        try {
                            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                            String applicationLabel = (pm.getApplicationLabel(ai)).toString();
                            //System.out.println("name = " + applicationLabel);
                            singleAppInfo.put("name",applicationLabel);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            singleAppInfo.put("name",packageName);
                        }
                        //uidList.add(String.valueOf(uid));
                        allInstalledAppsInfo.add(singleAppInfo);
                    }
                }
            }

        }
        //System.out.println(allInstalledAppsInfo);
        return allInstalledAppsInfo;
    }

    public Drawable getAppIconByPackageName(Context context,String ApkTempPackageName){
        Drawable drawable;
        try{
            drawable = context.getPackageManager().getApplicationIcon(ApkTempPackageName);
        }
        catch (PackageManager.NameNotFoundException e){
            Log.e("错误", "getAppIconByPackageName: 无法获取图标" );
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
        }
        return drawable;
    }

}
