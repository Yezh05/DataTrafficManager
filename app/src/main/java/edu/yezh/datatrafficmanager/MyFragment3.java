package edu.yezh.datatrafficmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.tools.PoiTools;

import static android.content.Context.NOTIFICATION_SERVICE;


public class MyFragment3 extends Fragment {
    public MyFragment3() {
    }
    int i =0 ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.t3, container, false);
        Log.e("Fragment", "工具页");

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


        /*Button buttonSHowAppsTrafficData = view.findViewById(R.id.ButtonSHowAppsTrafficData);
        buttonSHowAppsTrafficData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bucketDao.getInstalledAppsTrafficData(context,"",1, ConnectivityManager.TYPE_WIFI);
            }
        });*/
        Button buttonSetAppsUnusualTrafficDataAmount = view.findViewById(R.id.ButtonSetAppsUnusualTrafficDataAmount);
        buttonSetAppsUnusualTrafficDataAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditViewAlert(view);
            }
        });

        Button buttonOutputDataToFile = view.findViewById(R.id.ButtonOutputDataToFile);
        buttonOutputDataToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputDataToFile(view, context);
            }
        });

        /*Button buttonClose4G = view.findViewById(R.id.ButtonClose4G);
        buttonClose4G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMobileDataState(context,false);
            }
        });*/

        /*Button ButtonNotice = view.findViewById(R.id.ButtonNotice);
        ButtonNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotification(context,String.valueOf(i),"message");
                i++;
            }
        });*/

        return view;
    }

    private void openEditViewAlert(View view) {
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
                editor.putInt("AppsMAXTraffic", Integer.valueOf(inputData));
                editor.commit();
            }
        });
        builderAppsUnusualTrafficDataAmount.show();
    }

    private void outputDataToFile(View view, Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Calendar dayCal = Calendar.getInstance();
            String nowtime = "" + dayCal.get(Calendar.YEAR) + (dayCal.get(Calendar.MONTH) + 1) + dayCal.get(Calendar.DATE) + dayCal.get(Calendar.HOUR_OF_DAY) + dayCal.get(Calendar.MINUTE) + dayCal.get(Calendar.SECOND);
            String pathString = context.getExternalFilesDir("").getAbsolutePath() + "/a" + nowtime + ".xls";
            Path path = Paths.get(pathString);
            //创建文件
            if (!Files.exists(path)) {
                try {
                    Files.createFile(path);
                } catch (Exception e) {
                    Log.e("严重错误", e.toString());
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(pathString,false);
                //OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                /*byte bytes[] = "DIU1".getBytes();
                Files.write(path,bytes);*/

                HSSFWorkbook wb = PoiTools.getHSSFWorkbook(null,context);
                wb.write(fos);
                fos.flush();
                fos.close();
                Snackbar.make(view, "导出成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } catch (Exception e) {
                Log.e("严重错误", e.toString());
            }
        }
    }


}
