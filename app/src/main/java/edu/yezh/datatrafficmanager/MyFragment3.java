package edu.yezh.datatrafficmanager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


public class MyFragment3 extends Fragment {
    public MyFragment3() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.t3, container, false);
//        TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        //      txt_content.setText("第一个Fragment");
        Log.e("HEHE", "3日狗");

        final Context context = view.getContext();
        Button buttonSHowAppsTrafficData = (Button) view.findViewById(R.id.ButtonSHowAppsTrafficData);
        buttonSHowAppsTrafficData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUid(context);
                getUids(context);
            }
        });


        return view;
    }

    public static String getUid(Context context) {
        String uid = "";
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo("tv.danmaku.bili", PackageManager.GET_META_DATA);
            uid = String.valueOf(ai.uid);
            System.out.println("biliUID:" + uid);
            // Log.i("ai.uid: " , ai.uid);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return uid;
    }

    public List getUids(Context context) {
        List<Integer> uidList = new ArrayList<Integer>();
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

                        uidList.add(uid);
                    }
                }
            }
        }
        return uidList;
    }
}
