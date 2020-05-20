package edu.yezh.datatrafficmanager.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.dao.db.AppTransRecordDao;
import edu.yezh.datatrafficmanager.dao.db.DataTrafficRegulateDao;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.FtpFileTool;
import edu.yezh.datatrafficmanager.tools.NetWorkSpeedTestTools;
import edu.yezh.datatrafficmanager.tools.PoiTools;
import edu.yezh.datatrafficmanager.tools.SimTools;
import edu.yezh.datatrafficmanager.tools.floatWindowTools.FloatingWindowAppMonitorService;
import edu.yezh.datatrafficmanager.tools.floatWindowTools.FloatingWindowNetWorkSpeedService;

import static android.content.Context.MODE_PRIVATE;


public class MyFragmentToolsPage extends Fragment {
    public MyFragmentToolsPage() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tools_page, container, false);
        Log.e("Fragment", "工具页");

        final BucketDao bucketDao = new BucketDaoImpl();
        final Context context = view.getContext();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TOD Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        }

        Button buttonSetAppsUnusualTrafficDataAmount = view.findViewById(R.id.ButtonSetAppsUnusualTrafficDataAmount);
        addButtonComment(buttonSetAppsUnusualTrafficDataAmount,"<br><i><font color='#AAAAAA'>设置一个全局的应用流量使用量告警阀值</i>");
        buttonSetAppsUnusualTrafficDataAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditViewAlert(view);
            }
        });

        Button buttonOutputDataToFile = view.findViewById(R.id.ButtonOutputDataToFile);
        addButtonComment(buttonOutputDataToFile,("<br><i><font color='#AAAAAA'>将流量统计为Excel文件并保存在手机</i>"));
        buttonOutputDataToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputDataToFile(view, context);
            }
        });

        Button ButtonOutputDataToFTP = view.findViewById(R.id.ButtonOutputDataToFTP);
        addButtonComment(ButtonOutputDataToFTP,("<br><i><font color='#AAAAAA'>将流量统计为Excel文件并上传到FTP服务器</i>"));
        ButtonOutputDataToFTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //outputDataToFTP(view,context);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        outputDataToFTP(view, context);
                    }
                }).start();
            }
        });

        Button ButtonCustomQuery = view.findViewById(R.id.ButtonCustomQuery);
        addButtonComment(ButtonCustomQuery,("<br><i><font color='#AAAAAA'>查询一个时间段内全局或应用的流量使用量</i>"));
        ButtonCustomQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCustomQuery(context);
            }
        });

        Button ButtonResetTrafficDataRegulate = view.findViewById(R.id.ButtonResetTrafficDataRegulate);
        addButtonComment(ButtonResetTrafficDataRegulate,("<br><i><font color='#AAAAAA'>重置流量校正,恢复为本机统计数据</i>"));
        ButtonResetTrafficDataRegulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataTrafficRegulateDao dataTrafficRegulateDao = new DataTrafficRegulateDao(context);
                dataTrafficRegulateDao.deteleAll();

                Snackbar.make(view, "已重置", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        TextView TextViewFloatWindow = view.findViewById(R.id.TextViewFloatWindow);
        TextViewFloatWindow.setText(Html.fromHtml("网速悬浮窗<br/><i><font color='#AAAAAA'>开关一个用于显示实时网速的悬浮窗</i>"));
        final Intent serviceIntend = new Intent(getActivity(), FloatingWindowNetWorkSpeedService.class);
        Switch switchOpenFloatWindow = view.findViewById(R.id.SwitchOpenFloatWindow);
        switchOpenFloatWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (FloatingWindowNetWorkSpeedService.isStarted) {
                        return;
                    }
                    if (!Settings.canDrawOverlays(context)) {
                        System.out.println("无权限");
                        Toast.makeText(context, "当前无权限，请授权", Toast.LENGTH_SHORT);
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName())), 0);
                    } else {
                        System.out.println("开启Service");
                        try {

                            getActivity().startService(serviceIntend);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }else {
                    if (FloatingWindowNetWorkSpeedService.isStarted) {
                        getActivity().stopService(serviceIntend);
                    }
                }
            }
        });
        Button ButtonWifiSignalTest = view.findViewById(R.id.ButtonWifiSignalTest);
        ButtonWifiSignalTest.setText(Html.fromHtml("Wifi连接信息<br><i><font color='#AAAAAA'>查看Wifi连接信息</i>"));
        ButtonWifiSignalTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("查看Wifi连接情况与信号情况");
                Intent goWifiInfoIntent = new Intent(context,ShowWifiInfoActivity.class);
                context.startActivity(goWifiInfoIntent);
            }
        });

        Button ButtonNetworkSpeedTest = view.findViewById(R.id.ButtonNetworkSpeedTest);
        addButtonComment(ButtonNetworkSpeedTest,("<br><i><font color='#AAAAAA'>测试当前网络的下载速度</i>"));
        ButtonNetworkSpeedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNetSpeedTest(context);
            }
        });

        final Intent serviceIntend2 = new Intent(getActivity(), FloatingWindowAppMonitorService.class);
        Button ButtonOpenOutputFileFolder = view.findViewById(R.id.ButtonOpenOutputFileFolder);
        addButtonComment(ButtonOpenOutputFileFolder,"<br><i><font color='#AAAAAA'>使用文件管理器打开文件保存路径</i>");
        ButtonOpenOutputFileFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri selectedUri = Uri.parse(context.getExternalFilesDir("").getAbsolutePath()+"/" );
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedUri, "*/*");
                /*if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null)
                {
                    startActivity(intent);
                }
                else
                {
                    Snackbar.make(view,"没有合适的文件管理器",Snackbar.LENGTH_LONG).show();
                }*/
                //Intent intent = new Intent();
                //intent.setAction(Intent.ACTION_VIEW);
                //Uri myUri = Uri.parse(context.getExternalFilesDir("").getAbsolutePath()+"/");
                //intent.setDataAndType(myUri, "*/*");
                startActivity(  Intent.createChooser(intent, "选择一个文件管理器"));
            }
        });

        Button ButtonCleanTransRecord = view.findViewById(R.id.ButtonCleanTransRecord);
        addButtonComment(ButtonCleanTransRecord,"<br><i><font color='#AAAAAA'>清除全部APP监测数据</i>");
        ButtonCleanTransRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppTransRecordDao appTransRecordDao = new AppTransRecordDao(context);
                appTransRecordDao.deteleAll();
                System.out.println("已全部清除");
            }
        });

        TextView TextViewOpenAppMonitorInfo = view.findViewById(R.id.TextViewOpenAppMonitorInfo);
        TextViewOpenAppMonitorInfo.setText(Html.fromHtml("APP使用监测窗<br/><i><font color='#AAAAAA'>开关一个用于监控APP流量的悬浮窗</i>"));
        Switch SwitchOpenAppMonitorInfo = view.findViewById(R.id.SwitchOpenAppMonitorInfo);
        SwitchOpenAppMonitorInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (FloatingWindowAppMonitorService.isStarted) {
                        return;
                    }
                    if (!Settings.canDrawOverlays(context)) {
                        System.out.println("无权限");
                        Toast.makeText(context, "当前无权限，请授权", Toast.LENGTH_SHORT);
                        startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName())), 0);
                    } else {
                        System.out.println("开启Service");
                        try {
                            getActivity().startService(serviceIntend2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    if (FloatingWindowAppMonitorService.isStarted) {
                        try {

                            getActivity().stopService(serviceIntend2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
        });

        /*Button BT = view.findViewById(R.id.BT11);
        BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //ContentProviderRecord{6396e5a u0 com.huawei.systemmanager/.netassistant.db.traffic.TrafficDBProvider} launchingApp=null caller pid= 9130
                    //com.huawei.netassistant.ui.NetAssistantMainActivity
                    //05-20 15:05:25.791  2292 32751 W ActivityManagerWrapperEx: getTaskThumbnailEx HwWindowManager snapshot =TaskSnapshot{ mTopActivityComponent=com.huawei.systemmanager/com.huawei.netassistant.ui.NetAssistantMainActivity

                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.netassistant.traffic.appdetail.AppDetailActivity");
                    intent.setData(Uri.parse("package:" + "edu.yezh.datatrafficmanager"));
                    intent.setComponent(componentName);

                    context.startActivity(intent);
                }catch (Exception e){
                    System.out.println(e.toString());
                }
            }
        });*/
        TextView TextViewSetMonthWarningFlagNumber = view.findViewById(R.id.TextViewSetMonthWarningFlagNumber);
        TextView TextViewSetMonthWarningFlagInfo =view.findViewById(R.id.TextViewSetMonthWarningFlagInfo);
        TextViewSetMonthWarningFlagInfo.setText(Html.fromHtml("月已用流量提醒<br/><i><font color='#AAAAAA'>当月流量消耗达到限定值时提醒</i>"));
        SharedPreferences sp = getActivity().getSharedPreferences("TrafficManager", MODE_PRIVATE);
        int monthWarningFlag = sp.getInt("monthWarningFlag",80);
        TextViewSetMonthWarningFlagNumber.setText(monthWarningFlag+"%");
        SeekBar SeekBarSetMonthWarningFlag =view.findViewById(R.id.SeekBarSetMonthWarningFlag);
        SeekBarSetMonthWarningFlag.setProgress(monthWarningFlag);
        SeekBarSetMonthWarningFlag.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextViewSetMonthWarningFlagNumber.setText(progress+"%");
                SharedPreferences.Editor editor=context.getSharedPreferences("TrafficManager", Context.MODE_PRIVATE).edit();
                editor.putInt("monthWarningFlag",progress).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TextView TextViewAbout =   view.findViewById(R.id.TextViewAbout);
        TextViewAbout.setText(Html.fromHtml("关于<br/><i><font color='#AAAAAA'>计科16404-叶重涵</i>"));
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(getContext())) {
                Toast.makeText(getContext(), "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "授权成功", Toast.LENGTH_SHORT).show();
                getActivity().startService(new Intent(getActivity(), FloatingWindowNetWorkSpeedService.class));
            }
        }
    }

    private void handleNetSpeedTest(final Context context){
        int networkInfo = SimTools.getNowActiveNetWorkType(context);
        String msg = "网速测试功能<br/>测试大约需要10秒";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网速测试");
        if (networkInfo==-1000) {
            msg = "当前未连接网络";
        } else {
            if (networkInfo == ConnectivityManager.TYPE_WIFI) {
                msg += "<br/>当前网络为<i><font color='#DB7093'>Wifi</font></i>网络";
            }else if (networkInfo == ConnectivityManager.TYPE_MOBILE){
                msg += "<br/>当前网络为<i><font color='#7B68EE'>移动</font></i>网络,需消耗网络流量";
            }
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final   ProgressDialog progressDialog = ProgressDialog.show(context, "网速测试", "正在测试中…", true, false, null);
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 9990);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            NetWorkSpeedTestTools netWorkSpeedTestTools = new NetWorkSpeedTestTools();
                            List<Long> list = netWorkSpeedTestTools.checkNetSpeed(context.getExternalFilesDir("").getAbsolutePath() + "/", "http://cgdl.qihucdn.com/wot/official/WoT.0.9.22.15069hd_install-1.bin");
                            long speed = list.get(0);
                            long time = list.get(1);
                            long downlength = list.get(2);
                            System.out.println("下载速度：" + speed);
                            //ProgressBar progressBar = new ProgressBar(context);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("网速测试");
                            //builder.setView(progressBar);
                            builder.setMessage("网速测试中");
                            OutputTrafficData data = new BytesFormatter().getPrintSizeByModel(speed);
                            OutputTrafficData downdata = new BytesFormatter().getPrintSizeByModel(downlength);
                            builder.setMessage("当前网络环境网速约为" + data.getValueWithTwoDecimalPoint() + data.getType() + "/s\n下载量消耗约为"
                                    +downdata.getValueWithTwoDecimalPoint()+downdata.getType());
                            builder.setNegativeButton("确定", null);
                            builder.show();
                            Looper.loop();
                        }
                    }).start();

                }
            });
        }
        builder.setMessage(Html.fromHtml(msg));
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void openEditViewAlert(View view) {
        final Context context = view.getContext();
        final View viewCustomerDialogDataInput = LayoutInflater.from(context).inflate(R.layout.view_customer_dialog_data_input,null);
        TextView textViewHint = viewCustomerDialogDataInput.findViewById(R.id.TextViewHint);
        textViewHint.setText("请输入APP流量限额");
        final EditText editText = viewCustomerDialogDataInput.findViewById(R.id.EditText_Traffic_Data_Value);
        final Spinner spinnerDataType = viewCustomerDialogDataInput.findViewById(R.id.Spinner_Traffic_Data_Type);
        spinnerDataType.setSelection(3);
        final AlertDialog builderDataPlanAlertDialog = new AlertDialog.Builder(context).create();
        builderDataPlanAlertDialog.setTitle("设置APP流量限额");
        builderDataPlanAlertDialog.setView(viewCustomerDialogDataInput);
        Button btnCancel = viewCustomerDialogDataInput.findViewById(R.id.ButtonCustomDialogCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderDataPlanAlertDialog.dismiss();
            }
        });
        Button btnConfirm= viewCustomerDialogDataInput.findViewById(R.id.ButtonCustomDialogConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BytesFormatter bytesFormatter = new BytesFormatter();
                String inputData = editText.getText().toString();
                long inputUse =  bytesFormatter.convertValueToLong( Double.valueOf(inputData),spinnerDataType.getSelectedItem().toString());
                if (inputUse<=0){
                    builderDataPlanAlertDialog.dismiss();
                    Toast.makeText(context,"非法的数值",Toast.LENGTH_LONG).show();
                    return;
                }
                SharedPreferences.Editor editor = context.getSharedPreferences("TrafficManager", Context.MODE_PRIVATE).edit();
                //Log.w("设置流量套餐信息", "dataPlan_" + subscriberID + " : " + Float.valueOf(inputData).toString());
                editor.putLong("AppsMAXTraffic", (inputUse));
                editor.commit();
                builderDataPlanAlertDialog.dismiss();
            }
        });
        builderDataPlanAlertDialog.show();


    }

    private void outputDataToFile(View view, final Context context) {

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String nowTime = formatter.format(date);

            String pathString = context.getExternalFilesDir("").getAbsolutePath() + "/TrafficData_" + nowTime + ".xls";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Path path = Paths.get(pathString);
            //创建文件
            if (!Files.exists(path)) {
                try {
                    Files.createFile(path);
                } catch (Exception e) {
                    Log.e("严重错误", e.toString());
                }
            }
        }else {
            File file=new File(pathString);
            if(file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                System.out.println("文件不存在");
            }
        }
            try {
                FileOutputStream fos = new FileOutputStream(pathString, false);
                final HSSFWorkbook wb = PoiTools.getHSSFWorkbook(null, context);
                wb.write(fos);
                fos.flush();
                fos.close();
                final  String fPathString =pathString;
                Snackbar.make(view, "导出成功", Snackbar.LENGTH_LONG)
                        .setAction("打开文件", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent();
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction(Intent.ACTION_VIEW);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                        StrictMode.setVmPolicy(builder.build());
                                    }
                                    intent.setDataAndType(Uri.fromFile(new File(fPathString)), "application/vnd.ms-excel");
                                    context.startActivity(intent);
                                    Intent.createChooser(intent, "请选择软件打开");
                                } catch (Exception e) {
                                    Toast.makeText(context, "文件不能打开", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        }).show();
            } catch (Exception e) {
                Log.e("严重错误", e.toString());
            }

    }

    private void outputDataToFTP(View view, Context context) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String nowTime = formatter.format(date);
        try {
            final HSSFWorkbook wb = PoiTools.getHSSFWorkbook(null, context);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            bos.flush();
            InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
            bos.close();
            boolean flag = FtpFileTool.uploadFile("119.3.181.85", 21, "Administrator", "Yezhonghan43", "/TrafficManagerData/", "TD_" + nowTime + ".xls", inputStream);
            System.out.println("FTP上传状态:" + (flag));
            if (flag == true) {
                Snackbar.make(view, "导出成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else {
                Snackbar.make(view, "导出错误", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } catch (Exception e) {
            Log.e("严重错误", e.toString());
        }
    }

    private void handleCustomQuery( Context context) {
        Intent intent = new Intent(context, CustomQueryActivity.class);
        startActivity(intent);
    }
    private void addButtonComment(Button button,String msg){
        try {
            button.append(Html.fromHtml(msg));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
