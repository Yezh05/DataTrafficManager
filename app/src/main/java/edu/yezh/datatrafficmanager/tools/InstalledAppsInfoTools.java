package edu.yezh.datatrafficmanager.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstalledAppsInfoTools {
    public List<Map<String,String>> getAllInstalledAppsUids(Context context) {
        List<Map<String,String>> allInstalledAppsUids = new ArrayList<>();
        List<String> uidList = new ArrayList<String>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packinfos) {
            String[] premissions = info.requestedPermissions;
            if (premissions != null && premissions.length > 0) {
                for (String premission : premissions) {
                    if ("android.permission.INTERNET".equals(premission)) {

                        int uid = info.applicationInfo.uid;
                        String name = pm.getNameForUid(uid);
                        if (name.indexOf("system")!=-1||name.indexOf("android")!=-1){
                            continue;
                        }
                        System.out.println("uid = " + uid);
                        System.out.println("pkgname = " + name);
                        try {
                            ApplicationInfo ai = pm.getApplicationInfo(name, PackageManager.GET_META_DATA);
                            String applicationLabel = (pm.getApplicationLabel(ai)).toString();
                            System.out.println("name = " + applicationLabel);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        uidList.add(String.valueOf(uid));
                    }
                }
            }
        }
        return null;
    }
}
