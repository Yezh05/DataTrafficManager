package edu.yezh.datatrafficmanager.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.dao.db.AppBaseInfoDao;
import edu.yezh.datatrafficmanager.dao.db.AppPreferenceDao;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.model.tb.Tb_AppBaseInfo;
import edu.yezh.datatrafficmanager.model.tb.Tb_AppPreference;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.InstalledAppsInfoTools;
import edu.yezh.datatrafficmanager.tools.SimTools;
import edu.yezh.datatrafficmanager.tools.chartTools.CustomMarkerView;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;

public class ShowAppDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        View view = this.getWindow().getDecorView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
         int uid=Integer.valueOf(bundle.getString("uid"));
         String name=bundle.getString("name");
         String packageName=bundle.getString("packageName");
        /*long rxBytes = bundle.getLong("rx");
        long txBytes=bundle.getLong("tx");*/
        String subscriberID = bundle.getString("subscriberID");
        int networkType =bundle.getInt("networkType");
        System.out.println("name="+name+",uid="+uid);
        init(view,uid,name,packageName,subscriberID,networkType);
    }
    private void init(View view,int uid,String name,String packageName,String subscriberID,int networkType){
        Drawable appIcon = new InstalledAppsInfoTools().getAppIconByPackageName(this,packageName);
        TextView appDetailsName =  findViewById(R.id.AppDetailsName);
        appDetailsName.setText(name);
        /*TextView appDetailsPackageName =  findViewById(R.id.AppDetailsPackageName);
        appDetailsPackageName.setText(packageName);*/
        ImageView ImageViewDetailAppIcon = findViewById(R.id.ImageViewDetailAppIcon);
        ImageViewDetailAppIcon.setImageDrawable(appIcon);

        if (networkType == ConnectivityManager.TYPE_WIFI){
            LinearLayout linearLayoutSetSIMIgnore = view.findViewById(R.id.LinearLayoutSetSIMIgnore);
            linearLayoutSetSIMIgnore.setVisibility(View.GONE);
        }else {
           int simAmount =   new SimTools().getCount(view.getContext());
            if (simAmount==1){
                LinearLayout linearLayoutSetBarSIM2Ignore = view.findViewById(R.id.LinearLayoutSetBarSIM2Ignore);
                linearLayoutSetBarSIM2Ignore.setVisibility(View.GONE);
            }
        }
        setSwitches( view, String.valueOf(uid),packageName);

        DateTools dateTools =  new DateTools();
        BytesFormatter bytesFormatter = new BytesFormatter();
        BucketDao bucketDao = new BucketDaoImpl();

        TransInfo appThisMonthTrafficData = bucketDao.getAppTrafficData(this,subscriberID,networkType,dateTools.getTimesMonthMorning(),System.currentTimeMillis(),uid);

        /*System.out.println("subscriberID="+subscriberID);
        System.out.println("networkType="+networkType);
        System.out.println("getTimesTodayMorning="+dateTools.getTimesTodayMorning());
        System.out.println("uid="+uid);*/

        TransInfo appTodayTrafficData=bucketDao.getAppTrafficData(this,subscriberID,networkType,dateTools.getTimesTodayMorning(),dateTools.getTimesTodayEnd(),uid);

        //System.out.println(appTodayTrafficData);

        TextView TextViewAppThisMonthInfo = findViewById(R.id.TextViewAppThisMonthInfo);
        OutputTrafficData appThisMonthTrafficDataRx = bytesFormatter.getPrintSizeByModel(appThisMonthTrafficData.getRx());
        OutputTrafficData appThisMonthTrafficDataTx = bytesFormatter.getPrintSizeByModel(appThisMonthTrafficData.getTx());
        OutputTrafficData appThisMonthTrafficDataTotal = bytesFormatter.getPrintSizeByModel(appThisMonthTrafficData.getTotal());
        TextViewAppThisMonthInfo.setText("上传流量:" +appThisMonthTrafficDataTx.getValueWithTwoDecimalPoint()+appThisMonthTrafficDataTx.getType()+
                "  下载流量:" +appThisMonthTrafficDataRx.getValueWithTwoDecimalPoint()+appThisMonthTrafficDataRx.getType()+
                "  总量:"+appThisMonthTrafficDataTotal.getValueWithTwoDecimalPoint()+appThisMonthTrafficDataTotal.getType());

        TextView TextViewAppTodayInfo = findViewById(R.id.TextViewAppTodayInfo);
        OutputTrafficData appTodayTrafficDataRx = bytesFormatter.getPrintSizeByModel(appTodayTrafficData.getRx());
        OutputTrafficData appTodayTrafficDataTx = bytesFormatter.getPrintSizeByModel(appTodayTrafficData.getTx());
        OutputTrafficData appTodayTrafficDataTotal = bytesFormatter.getPrintSizeByModel(appTodayTrafficData.getTotal());
        TextViewAppTodayInfo.setText("上传流量:" +appTodayTrafficDataTx.getValueWithTwoDecimalPoint()+appTodayTrafficDataTx.getType()+
                "  下载流量:" +appTodayTrafficDataRx.getValueWithTwoDecimalPoint()+appTodayTrafficDataRx.getType()+
                "  总量:"+appTodayTrafficDataTotal.getValueWithTwoDecimalPoint()+appTodayTrafficDataTotal.getType());

        Map<String,List<Long>> LastThirtyDaysMap = dateTools. getLastThirtyDaysMap();
        List<TransInfo> lastThirtyDaysTrafficData = bucketDao.getAppTrafficDataOfPeriod(this,subscriberID,networkType,LastThirtyDaysMap,uid);
        List<Long> LastThirtyDaysNoList = LastThirtyDaysMap.get("No");
        Collections.reverse(LastThirtyDaysNoList);
        Collections.reverse(lastThirtyDaysTrafficData);
        showChart(view, lastThirtyDaysTrafficData, LastThirtyDaysNoList,R.id.LineChartAppLastThirtyDaysTrafficData,"日");

        Map<String,List<Long>> LastTwentyFourHoursMap = dateTools.getLastTwentyFourHoursPerThreeHourMap();
        List<TransInfo> lastTwentyFourHoursTrafficData = bucketDao.getAppTrafficDataOfPeriod(this,subscriberID,networkType,LastTwentyFourHoursMap,uid);
        List<Long> LastTwentyFourHoursNoList = LastTwentyFourHoursMap.get("No");
        Collections.reverse(lastTwentyFourHoursTrafficData);
        Collections.reverse(LastTwentyFourHoursNoList);
        //System.out.println(lastTwentyFourHoursTrafficData);
        showChart(view, lastTwentyFourHoursTrafficData, LastTwentyFourHoursNoList,R.id.LineChartAppLastTwentyFourHoursTrafficData,"时");
    }
    private void showChart(View view, List<TransInfo> valueDataList, List<Long> xLineNoList,int chartId,String unit){

        LineChart lineChart = view.findViewById(chartId);
        final ArrayList<Entry> values = new ArrayList<>();
        /*values.add(new Entry(100, 30));*/

        final String[] Xvalues = new String[xLineNoList.size()];
        for (int i=0;i<xLineNoList.size();i++){
            values.add(new Entry(i,valueDataList.get(i).getTotal()));
            if (unit.equals("时")){
                Xvalues[i]= xLineNoList.get(i) +"-"+(xLineNoList.get(i)+3) +unit;
            }else {
            Xvalues[i]= xLineNoList.get(i) +unit;
            }
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float xvalue, AxisBase axis) {
                return Xvalues[(int) xvalue];
            }

        };

        LineDataSet lineDataSet = new LineDataSet(values,"数据");
        //lineDataSet.setValues(values);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setColor(Color.parseColor("#008080"));
        lineDataSet.setFillColor(Color.parseColor("#20B2AA"));
        lineDataSet.setValueFormatter(new MyLineValueFormatter());
        lineDataSet.setValueTextSize(13);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleColor(Color.parseColor("#2F4F4F"));
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawHighlightIndicators(false);
        if (unit.equals("时")){
          lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        }


        dataSets.add(lineDataSet);
        LineData data1 = new LineData(dataSets);
        lineChart.setData(data1);
        lineChart.getLegend().setEnabled(false);

        lineChart.getXAxis().setGranularity(1);
        lineChart.getXAxis().setValueFormatter(formatter);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setAvoidFirstLastClipping(true);

        lineChart.getAxisRight().setEnabled(false);
        //lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.animateXY(0,1200);
        lineChart.setVisibleXRangeMaximum(6);
        lineChart.setViewPortOffsets(50,50,50,50);
        lineChart.moveViewToX(valueDataList.size()-1);
        lineChart.getDescription().setEnabled(false);

        CustomMarkerView mv = new CustomMarkerView(view.getContext(),
                R.layout.view_customer_marker);
        lineChart.setMarkerView(mv);


        lineChart.invalidate();
    }

    private void setSwitches(final View view, String uid, String packageName){
        AppBaseInfoDao appBaseInfoDao = new AppBaseInfoDao(view.getContext());
        Tb_AppBaseInfo appBaseInfo = appBaseInfoDao.find(uid);
        if (appBaseInfo==null){
            appBaseInfoDao.add(new Tb_AppBaseInfo(uid,packageName));
        }
        final AppPreferenceDao appPreferenceDao = new AppPreferenceDao(view.getContext());
        Tb_AppPreference tempTbAppPreference = appPreferenceDao.find(uid);
        final Tb_AppPreference tbAppPreference;
        if (tempTbAppPreference ==null){
            tbAppPreference = new Tb_AppPreference(uid,packageName,0,0,-1L);
            appPreferenceDao.add(tbAppPreference);
        }else{
            tbAppPreference = tempTbAppPreference;
        }
        int sim1IgnoreFlag  = tbAppPreference.getSim1IgnoreFlag();
        int sim2IgnoreFlag  = tbAppPreference.getSim2IgnoreFlag();
        try {
            Switch switchSetSIM1Ignore = view.findViewById(R.id.SwitchSetSIM1Ignore);
            if (sim1IgnoreFlag != 0){
                switchSetSIM1Ignore.setChecked(true);
            }
            switchSetSIM1Ignore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true){
                        tbAppPreference.setSim1IgnoreFlag(1);
                        appPreferenceDao.update(tbAppPreference);
                    }else {
                        tbAppPreference.setSim1IgnoreFlag(0);
                        appPreferenceDao.update(tbAppPreference);
                    }
                }
            });

        }catch (Exception e){
            System.out.println("严重错误："+e.toString());
        }

        try {
            Switch switchSetSIM2Ignore = view.findViewById(R.id.SwitchSetSIM2Ignore);
            if (sim2IgnoreFlag != 0){
                switchSetSIM2Ignore.setChecked(true);
            }
            switchSetSIM2Ignore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true){
                        tbAppPreference.setSim2IgnoreFlag(1);
                        appPreferenceDao.update(tbAppPreference);
                    }else {
                        tbAppPreference.setSim2IgnoreFlag(0);
                        appPreferenceDao.update(tbAppPreference);
                    }
                }
            });
        }catch (Exception e){
            System.out.println("严重错误："+e.toString());
        }

        final Switch switchWarningLimit = view.findViewById(R.id.SwitchSetAppWarningLimit);
        if (tbAppPreference.getWarningLimit()<0){
            TextView textView = view.findViewById(R.id.TextViewAppWarningPrompt);
            textView.setText("依照全局设置");
        }else {
            switchWarningLimit.setChecked(true);
            BytesFormatter bytesFormatter = new BytesFormatter();
            OutputTrafficData data = bytesFormatter.getPrintSizeByModel(tbAppPreference.getWarningLimit());
            TextView textView = view.findViewById(R.id.TextViewAppWarningPrompt);
            textView.setText("限额:"+data.getValueWithTwoDecimalPoint()+data.getType());
        }
        switchWarningLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true){
                    final View viewCustomerDialogDataInput = LayoutInflater.from(view.getContext()).inflate(R.layout.view_customer_dialog_data_input, null);
                    TextView textViewHint = viewCustomerDialogDataInput.findViewById(R.id.TextViewHint);
                    textViewHint.setText("请输入APP流量告警限额");
                    final EditText editText = viewCustomerDialogDataInput.findViewById(R.id.EditText_Traffic_Data_Value);
                    final Spinner spinnerDataType = viewCustomerDialogDataInput.findViewById(R.id.Spinner_Traffic_Data_Type);
                    spinnerDataType.setSelection(2);
                    final AlertDialog builderDataPlanAlertDialog = new AlertDialog.Builder(view.getContext()).create();
                    builderDataPlanAlertDialog.setTitle("设置套餐流量额度");
                    builderDataPlanAlertDialog.setView(viewCustomerDialogDataInput);
                    Button btnCancel = viewCustomerDialogDataInput.findViewById(R.id.ButtonCustomDialogCancel);
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switchWarningLimit.setChecked(false);
                            builderDataPlanAlertDialog.dismiss();
                        }
                    });
                    Button btnConfirm = viewCustomerDialogDataInput.findViewById(R.id.ButtonCustomDialogConfirm);
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BytesFormatter bytesFormatter = new BytesFormatter();
                            String inputData = editText.getText().toString();
                            long inputUse = bytesFormatter.convertValueToLong(Double.valueOf(inputData), spinnerDataType.getSelectedItem().toString());
                            if (inputUse <= 0) {
                                builderDataPlanAlertDialog.dismiss();
                                Toast.makeText(view.getContext(), "非法的数值", Toast.LENGTH_LONG).show();
                                return;
                            }
                            tbAppPreference.setWarningLimit(inputUse);
                            appPreferenceDao.update(tbAppPreference);
                            OutputTrafficData data = bytesFormatter.getPrintSizeByModel(inputUse);
                            TextView textView = view.findViewById(R.id.TextViewAppWarningPrompt);
                            textView.setText("限额:"+data.getValueWithTwoDecimalPoint()+data.getType());
                            builderDataPlanAlertDialog.dismiss();
                        }
                    });
                    builderDataPlanAlertDialog.show();
                }else {
                    tbAppPreference.setWarningLimit(-1L);
                    appPreferenceDao.update(tbAppPreference);
                    TextView textView = view.findViewById(R.id.TextViewAppWarningPrompt);
                    textView.setText("依照全局设置");
                }
            }
        });
    }
}
