package edu.yezh.datatrafficmanager.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.dao.db.DataTrafficRegulateDao;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.FtpFileTool;
import edu.yezh.datatrafficmanager.tools.NetWorkSpeedTestTools;
import edu.yezh.datatrafficmanager.tools.NowProcess;
import edu.yezh.datatrafficmanager.tools.PoiTools;
import edu.yezh.datatrafficmanager.tools.floatWindowTools.FloatingService;


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
        buttonSetAppsUnusualTrafficDataAmount.append(Html.fromHtml("<br><i><font color='#AAAAAA'>设置一个全局的应用流量使用量告警阀值</i>"));
        buttonSetAppsUnusualTrafficDataAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditViewAlert(view);
            }
        });

        Button buttonOutputDataToFile = view.findViewById(R.id.ButtonOutputDataToFile);
        buttonOutputDataToFile.append(Html.fromHtml("<br><i><font color='#AAAAAA'>将流量统计为Excel文件并保存在手机</i>"));
        buttonOutputDataToFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputDataToFile(view, context);
            }
        });

        Button ButtonOutputDataToFTP = view.findViewById(R.id.ButtonOutputDataToFTP);
        ButtonOutputDataToFTP.append(Html.fromHtml("<br><i><font color='#AAAAAA'>将流量统计为Excel文件并上传到FTP服务器</i>"));
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
        ButtonCustomQuery.append(Html.fromHtml("<br><i><font color='#AAAAAA'>查询一个时间段内全局或应用的流量使用量</i>"));
        ButtonCustomQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCustomQuery(context);
            }
        });

        Button ButtonResetTrafficDataRegulate = view.findViewById(R.id.ButtonResetTrafficDataRegulate);
        ButtonResetTrafficDataRegulate.append(Html.fromHtml("<br><i><font color='#AAAAAA'>重置流量校正,恢复为本机统计数据</i>"));
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
        final Intent serviceIntend = new Intent(getActivity(), FloatingService.class);
        Switch switchOpenFloatWindow = view.findViewById(R.id.SwitchOpenFloatWindow);
        switchOpenFloatWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (FloatingService.isStarted) {
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
                    if (FloatingService.isStarted) {
                        getActivity().stopService(serviceIntend);
                    }
                }
            }
        });

        Button ButtonNetworkSpeedTest = view.findViewById(R.id.ButtonNetworkSpeedTest);
        ButtonNetworkSpeedTest.append(Html.fromHtml("<br><i><font color='#AAAAAA'>测试当前网络的下载速度</i>"));
        ButtonNetworkSpeedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNetSpeedTest(context);
            }
        });

        Button ButtonT = view.findViewById(R.id.ButtonT);
        ButtonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           final      DateTools dateTools = new DateTools();
                //long bfRX = TrafficStats.getUidRxBytes(10162);
                try {
                   /*final TransInfo transInfo1 = bucketDao.getAppTrafficData(context,"460002911566405", ConnectivityManager.TYPE_WIFI,
                            0,dateTools.getTimesTodayEnd(),10162);
                    System.out.println("前："+ (transInfo1));*/
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            /*TransInfo transInfo2 = bucketDao.getAppTrafficData(context,"460002911566405", ConnectivityManager.TYPE_WIFI,
                                    0,dateTools.getTimesTodayEnd(),10162);
                            System.out.println("后："+ (transInfo2));
                            System.out.println("前后："+ (transInfo2.getTotal()-transInfo1.getTotal()));*/
                           //System.out.println( NowProcess.getForegroundApp(context));
                            NowProcess.hdddd(context);
                            //System.out.println(NowProcess.hdddd(context));
                                    handler.postDelayed(this,3000);
                        }
                    } ;
                    handler.postDelayed(runnable,0);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

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
                getActivity().startService(new Intent(getActivity(), FloatingService.class));
            }
        }
    }
    private void handleNetSpeedTest(final Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String msg = "网速测试功能<br/>测试大约需要10秒";



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网速测试");
        if (networkInfo==null) {
            msg = "当前未连接网络";
        } else {

            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                msg += "<br/>当前网络为<i><font color='#DB7093'>Wifi</font></i>网络";
            }else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                msg += "<br/>当前网络为<i><font color='#7B68EE'>移动</font></i>网络,需消耗网络流量";
            }
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String nowTime = formatter.format(date);

            String pathString = context.getExternalFilesDir("").getAbsolutePath() + "/TrafficData_" + nowTime + ".xls";
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


}
