package edu.yezh.datatrafficmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.yezh.datatrafficmanager.Dao.BucketDao;
import edu.yezh.datatrafficmanager.Dao.BucketDaoImpl;


public class MyFragment3 extends Fragment {
    public MyFragment3() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        final View view = inflater.inflate(R.layout.t3, container, false);
//        TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        //      txt_content.setText("第一个Fragment");
        Log.e("HEHE", "3日狗");

        final BucketDao bucketDao = new BucketDaoImpl();
        final Context context = view.getContext();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        }


        Button buttonSHowAppsTrafficData = (Button) view.findViewById(R.id.ButtonSHowAppsTrafficData);
        buttonSHowAppsTrafficData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bucketDao.getInstalledAppsTrafficData(context,"",1, ConnectivityManager.TYPE_WIFI);
            }
        });
        Button buttonSetAppsUnusualTrafficDataAmount = (Button) view.findViewById(R.id.ButtonSetAppsUnusualTrafficDataAmount);
        buttonSetAppsUnusualTrafficDataAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditViewAlert(view);
            }
        });

        Button buttonOutputDataToFile = (Button) view.findViewById(R.id.ButtonOutputDataToFile);
        buttonOutputDataToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputDataToFile(view,context);
            }
        });

        return view;
    }

    public void openEditViewAlert(View view){
        final Context context = view.getContext();
        final EditText editTextInputAppsUnusualTrafficDataAmount = new EditText(context);
        editTextInputAppsUnusualTrafficDataAmount.setHint("请输入APP每日流量使用提醒阀值(MB)");
        AlertDialog.Builder builderAppsUnusualTrafficDataAmount = new AlertDialog.Builder(context);
        builderAppsUnusualTrafficDataAmount.setTitle("APP每日流量使用提醒阀值").setIcon(R.mipmap.edit).setView(editTextInputAppsUnusualTrafficDataAmount).setNegativeButton("取消", null);
        builderAppsUnusualTrafficDataAmount.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String inputData = editTextInputAppsUnusualTrafficDataAmount.getText().toString();
                Toast.makeText(getActivity(), inputData, Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = context.getSharedPreferences("TrafficManager", Context.MODE_PRIVATE).edit();
                //Log.w("设置流量套餐信息", "dataPlan_" + subscriberID + " : " + Float.valueOf(inputData).toString());
                editor.putInt( "AppsMAXTraffic",Integer.valueOf(inputData));
                editor.commit();
            }
        });
        builderAppsUnusualTrafficDataAmount.show();
    }

    public void outputDataToFile(View view,Context context){

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Calendar dayCal = Calendar.getInstance();
                String nowtime = ""+dayCal.get(Calendar.YEAR)+(dayCal.get(Calendar.MONTH)+1)+dayCal.get(Calendar.DATE)+dayCal.get(Calendar.HOUR_OF_DAY)+dayCal.get(Calendar.MINUTE)+dayCal.get(Calendar.SECOND);
                Path path = Paths.get(context.getExternalFilesDir("").getAbsolutePath()+"/a"+nowtime+".txt");
                //创建文件
                if(!Files.exists(path)) {
                    try {
                        Files.createFile(path);
                    } catch (Exception e) {
                        Log.e("严重错误",e.toString());
                    }
                }
                //创建BufferedWriter
                /*try {
                    BufferedWriter bfw=Files.newBufferedWriter(fpath);
                    bfw.write("Files类的API:newBufferedWriter");
                    bfw.flush();
                    bfw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                byte[] bytes = "diu1".getBytes();
                try {
                    Files.write(path,bytes);
                    //Toast.makeText(context,)
                } catch (Exception e) {
                    Log.e("严重错误",e.toString());
                }
            }



    }
    /*public static String getUid(Context context) {
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
    }*/

    /*public List getUids(Context context) {
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
    }*/
}
