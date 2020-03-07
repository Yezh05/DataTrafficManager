package edu.yezh.datatrafficmanager.tools.floatWindowTools;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.dao.db.AppTransRecordDao;
import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.model.tb.Tb_AppTransRecord;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.InstalledAppsInfoTools;
import edu.yezh.datatrafficmanager.tools.SimTools;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;

public class FloatingWindowAppMonitorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View view;
    private String uid;
    private String packageName;
    private String name;
    private Drawable appIcon;
    private long startTime;
    private  Tb_AppTransRecord transRecord;
    private String SUBSCRIBER_ID;
    private int NETWORK_TYPE;
    //private List<Tb_AppTransRecord> transRecordList;
    private TransInfo startData;
    private LineChart lineChart;
    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 720;
        layoutParams.height = 540;
        layoutParams.x = 100;
        layoutParams.y = 100;
        System.out.println("创建Service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(view);
        isStarted = false;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            view = View.inflate(getApplicationContext(), R.layout.view_app_traffic_monitor,null);
            windowManager.addView(view,layoutParams);
            view.setOnTouchListener(new FloatingOnTouchListener());
            lineChart = view.findViewById(R.id.LineChartMonitorAppChart);

            final ArrayList<Entry> values = new ArrayList<>();
            values.add(new Entry(0, 0));
            final LineDataSet lineDataSet = new LineDataSet(values, "数据");
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            lineDataSet.setMode(LineDataSet.Mode.LINEAR);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setColor(Color.parseColor("#008080"));
            lineDataSet.setFillColor(Color.parseColor("#20B2AA"));
            lineDataSet.setValueFormatter(new MyLineValueFormatter());
            lineDataSet.setValueTextSize(13);
            lineDataSet.setLineWidth(2);
            lineDataSet.setCircleColor(Color.parseColor("#2F4F4F"));
            lineDataSet.setDrawCircleHole(true);
            lineDataSet.setCircleColorHole(Color.parseColor("#FFFFFF"));
            lineDataSet.setDrawHighlightIndicators(false);
            dataSets.add(lineDataSet);

            LineData data1 = new LineData(dataSets);
            lineChart.setData(data1);
            lineChart.getLegend().setEnabled(false);

            lineChart.getXAxis().setGranularity(3);
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            lineChart.getXAxis().setDrawGridLines(false);

            lineChart.getAxisRight().setEnabled(false);
            lineChart.getAxisLeft().setDrawGridLines(true);
            lineChart.getAxisLeft().setDrawLabels(false);
            lineChart.getAxisLeft().setDrawAxisLine(false);
            lineChart.getAxis(YAxis.AxisDependency.LEFT).setStartAtZero(true);
            lineChart.setVisibleXRangeMaximum(15);
            lineChart.getDescription().setEnabled(false);
            lineChart.invalidate();

            System.out.println("开始绘制！");

            final BucketDao bucketDao = new BucketDaoImpl();
            final AppTransRecordDao appTransRecordDao = new AppTransRecordDao(getApplicationContext());
            final TextView   TextViewMonitorAppTX = view.findViewById(R.id.TextViewMonitorAppTX);
            final TextView   TextViewMonitorAppRX= view.findViewById(R.id.TextViewMonitorAppRX);
            final TextView   TextViewMonitorAppTime= view.findViewById(R.id.TextViewMonitorAppTime);
            final TextView  TextViewMonitorAppTotal= view.findViewById(R.id.TextViewMonitorAppTotal);

            final ImageView ImageViewMonitorAppIcon = view.findViewById(R.id.ImageViewMonitorAppIcon);
            final Spinner spinner = view.findViewById(R.id.SpinnerMonitorChooseApp);
            final List<AppsInfo> allInstalledAppsInfo = new InstalledAppsInfoTools().getAllInstalledAppsInfo(getApplicationContext());
            String[] mItems = new String[allInstalledAppsInfo.size()];
            for (int i=0;i<allInstalledAppsInfo.size();i++){
                mItems[i] = allInstalledAppsInfo.get(i).getName();
            }
            ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    appIcon = allInstalledAppsInfo.get(position).getAppIcon();
                    ImageViewMonitorAppIcon.setImageDrawable(appIcon);
                    uid = allInstalledAppsInfo.get(position).getUid();
                    transRecord = null;
                    try {
                        NETWORK_TYPE = SimTools.getNowActiveNetWorkType(getApplicationContext());
                        SUBSCRIBER_ID = SimTools.getNowActiveSubscriberId(getApplicationContext());
                        System.out.println("SUBSCRIBER_ID:"+ SUBSCRIBER_ID);
                        transRecord =  appTransRecordDao.findLast(uid);
                        if (transRecord==null){
                            transRecord = new Tb_AppTransRecord(uid,0,0,0,0,System.currentTimeMillis());
                            appTransRecordDao.add(transRecord);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            final int timeStep = 3;
            final int[] round = new int[1];
            final BytesFormatter bytesFormatter = new BytesFormatter();
            final DateTools dateTools = new DateTools();
            final Handler transHandle = new Handler();
            final Runnable transRunnable = new Runnable() {
                @Override
                public void run() {
                    LineData data = lineChart.getData();
                    String timeString;
                    long sec = (System.currentTimeMillis()-startTime)/1000;
                    if (sec<=60){
                        timeString = sec +"秒";
                    }else {
                        timeString = sec/60L+"分"+ sec%60L +"秒";
                    }
                    TextViewMonitorAppTime.setText("监测时间:"+ timeString);
                    transRecord = appTransRecordDao.findLast(uid);
                    TransInfo nowData = bucketDao.getAppTrafficData(getApplicationContext(),SUBSCRIBER_ID,NETWORK_TYPE,dateTools.getTimesTodayMorning(),dateTools.getTimesTodayEnd(), Integer.parseInt(uid));
                    int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
                    if (round[0] == 0 ){
                        startData = nowData;
                        //System.out.println("startData:"+startData);
                        values.clear();
                        data.addEntry(new Entry(0,0),randomDataSetIndex);
                        round[0] = 1;
                    }else {
                        //System.out.println("sec"+sec);
                        data.addEntry(new Entry(sec,(nowData.getRx()-transRecord.getWifiRX())/timeStep),randomDataSetIndex);
                    }
                    //System.out.println(transRecord);
                    //System.out.println("nowData:"+nowData);
                    OutputTrafficData RXSpeed,TXSpeed;
                    if (NETWORK_TYPE== ConnectivityManager.TYPE_WIFI){
                        System.out.println(nowData.getTotal()-transRecord.getWifiRX()-transRecord.getWifiTX());
                        RXSpeed = bytesFormatter.getPrintSizeByModel((nowData.getRx()-transRecord.getWifiRX())/timeStep);
                        TXSpeed = bytesFormatter.getPrintSizeByModel((nowData.getTx()-transRecord.getWifiTX())/timeStep);
                        TextViewMonitorAppTX.setText("上传:"+ TXSpeed.getValueWithTwoDecimalPoint()+TXSpeed.getType()+"/s");
                        TextViewMonitorAppRX.setText("下载:"+ RXSpeed.getValueWithTwoDecimalPoint()+RXSpeed.getType()+"/s");
                        transRecord = new Tb_AppTransRecord(uid,transRecord.getMobileTX(),transRecord.getMobileRX(),nowData.getTx(),nowData.getRx(),System.currentTimeMillis());
                        appTransRecordDao.add(transRecord);
                    }else if (NETWORK_TYPE==ConnectivityManager.TYPE_MOBILE){
                        //System.out.println(nowData.getTotal()-transRecord.getMobileRX()-transRecord.getMobileTX());
                        RXSpeed = bytesFormatter.getPrintSizeByModel((nowData.getRx()-transRecord.getMobileRX())/timeStep);
                        TXSpeed = bytesFormatter.getPrintSizeByModel((nowData.getTx()-transRecord.getMobileTX())/timeStep);
                        TextViewMonitorAppTX.setText("上传:"+ TXSpeed.getValueWithTwoDecimalPoint()+TXSpeed.getType()+"/s");
                        TextViewMonitorAppRX.setText("下载:"+ RXSpeed.getValueWithTwoDecimalPoint()+RXSpeed.getType()+"/s");
                        transRecord = new Tb_AppTransRecord(uid,nowData.getTx(),nowData.getRx(),transRecord.getWifiTX(),transRecord.getWifiRX(),System.currentTimeMillis());
                        appTransRecordDao.add(transRecord);
                    }
                    lineChart.notifyDataSetChanged();
                    lineChart.moveViewToX(sec);
                    lineChart.setVisibleXRangeMaximum(15);
                    OutputTrafficData total = bytesFormatter.getPrintSizeByModel(nowData.getTotal()-startData.getTotal());
                    //System.out.println(total);
                    TextViewMonitorAppTotal.setText("传输总量:"+total.getValueWithTwoDecimalPoint()+total.getType());
                    transHandle.postDelayed(this,timeStep*1000L);
                }
            };

            Switch switchInMonitor = view.findViewById(R.id.SwitchInMonitor);
            switchInMonitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    TextView TextViewMonitorStatus = view.findViewById(R.id.TextViewMonitorStatus);
                    if (isChecked){
                        TextViewMonitorStatus.setText("监测中");
                        TextViewMonitorStatus.setTextColor(Color.parseColor("#DC143C"));
                        spinner.setEnabled(false);
                        startTime = System.currentTimeMillis();
                        round[0] = 0;
                        lineChart.moveViewToX(0);
                        transHandle.postDelayed(transRunnable,0);
                    }else {
                        TextViewMonitorStatus.setText("停止监测");
                        TextViewMonitorStatus.setTextColor(Color.parseColor("#4169E1"));
                        spinner.setEnabled(true);
                        transHandle.removeCallbacks(transRunnable);
                    }
                }
            });

        }else {
            System.out.println("错误！");
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}