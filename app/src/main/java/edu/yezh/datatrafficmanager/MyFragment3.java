package edu.yezh.datatrafficmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.tencent.mm.opensdk.modelbiz.SubscribeMessage;

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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.dao.db.DataTrafficRegulateDao;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.Sms;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.FtpFileTool;
import edu.yezh.datatrafficmanager.tools.NetWorkSpeedTestTools;
import edu.yezh.datatrafficmanager.tools.PoiTools;
import edu.yezh.datatrafficmanager.tools.floatWindowTools.FloatingService;
import edu.yezh.datatrafficmanager.tools.sms.SMSTools;


public class MyFragment3 extends Fragment {
    public MyFragment3() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.t3, container, false);
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

        Button buttonClose4G = view.findViewById(R.id.ButtonOutputDataToFTP);
        buttonClose4G.setOnClickListener(new View.OnClickListener() {
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
        ButtonCustomQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCustomQuery(context);
            }
        });

        Button ButtonResetTrafficDataRegulate = view.findViewById(R.id.ButtonResetTrafficDataRegulate);
        ButtonResetTrafficDataRegulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataTrafficRegulateDao dataTrafficRegulateDao = new DataTrafficRegulateDao(context);
                dataTrafficRegulateDao.deteleAll();
                Snackbar.make(view, "已重置", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        ButtonNetworkSpeedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("网速测试");
                builder.setMessage("网速测试功能\n测试大约需要10秒，可能会消耗流量");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                      new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                NetWorkSpeedTestTools netWorkSpeedTestTools= new NetWorkSpeedTestTools();
                                 long  speed = netWorkSpeedTestTools.checkNetSpeed(context.getExternalFilesDir("").getAbsolutePath()+"/","http://cgdl.qihucdn.com/wot/official/WoT.0.9.22.15069hd_install-1.bin");
                                System.out.println("下载速度："+speed);
                                //ProgressBar progressBar = new ProgressBar(context);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("网速测试");
                                //builder.setView(progressBar);
                                builder.setMessage("网速测试中");

                                OutputTrafficData data = new BytesFormatter().getPrintSizeByModel(speed);

                                builder.setMessage("当前网络环境网速约为"+Math.round(Double.valueOf(data.getValue())* 100D) / 100D+ data.getType() + "/s");
                                builder.setNegativeButton("确定", null);
                                builder.show();
                                Looper.loop();
                            }
                        }).start();

                    }
                });

             builder.setNegativeButton("取消",null);
             builder.show();

            }
        });

        /*Button ButtonT = view.findViewById(R.id.ButtonT);
        ButtonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int scene = random.nextInt(10000);
                SubscribeMessage.Req req = new SubscribeMessage.Req();
                req.scene = scene;
                req.templateID = templateID;
                req.reserved = reserved;
            }
        });*/

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

    private void openEditViewAlert(View view) {
        final Context context = view.getContext();

        /*final EditText editTextInputAppsUnusualTrafficDataAmount = new EditText(context);
        editTextInputAppsUnusualTrafficDataAmount.setHint("请输入APP每日流量使用提醒阀值(MB)");*/

        /*AlertDialog.Builder builderAppsUnusualTrafficDataAmount = new AlertDialog.Builder(context);*/

        /*builderAppsUnusualTrafficDataAmount.setTitle("APP每日流量使用提醒阀值").setIcon(R.mipmap.edit).setView(editTextInputAppsUnusualTrafficDataAmount).setNegativeButton("取消", null);
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
        builderAppsUnusualTrafficDataAmount.show();*/


        final View viewCustomerDialogDataInput = LayoutInflater.from(context).inflate(R.layout.customer_dialog_data_input_view,null);
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
                //OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                /*byte bytes[] = "DIU1".getBytes();
                Files.write(path,bytes);*/

                final HSSFWorkbook wb = PoiTools.getHSSFWorkbook(null, context);
                wb.write(fos);
                fos.flush();
                fos.close();
                final  String fpathString =pathString;
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
                                    intent.setDataAndType(Uri.fromFile(new File(fpathString)), "application/vnd.ms-excel");
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
        Intent intent = new Intent(context,CustomQueryActivity.class);
        startActivity(intent);
    }


}
