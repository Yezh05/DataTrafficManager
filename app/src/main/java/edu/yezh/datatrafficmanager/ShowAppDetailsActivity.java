package edu.yezh.datatrafficmanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.InstalledAppsInfoTools;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;

public class ShowAppDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
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
        long rxBytes = bundle.getLong("rx");
        long txBytes=bundle.getLong("tx");
        String subscriberID = bundle.getString("subscriberID");
        int networkType =bundle.getInt("networkType");
        Drawable appIcon = new InstalledAppsInfoTools().getAppIconByPackageName(this,packageName);

        TextView appDetailsName =  findViewById(R.id.AppDetailsName);
        appDetailsName.setText(name);
        TextView appDetailsPackageName =  findViewById(R.id.AppDetailsPackageName);
        appDetailsPackageName.setText(packageName);
       ImageView ImageViewDetailAppIcon = findViewById(R.id.ImageViewDetailAppIcon);
       ImageViewDetailAppIcon.setImageDrawable(appIcon);
       DateTools dateTools =  new DateTools();
       BytesFormatter bytesFormatter = new BytesFormatter();
       dateTools.getLastThirtyDaysMap();
       dateTools.getLastTwentyFourHoursMap();

        BucketDao bucketDao = new BucketDaoImpl();

        TransInfo appThisMonthTrafficData = bucketDao.getTrafficDataOfApp(this,subscriberID,networkType,dateTools.getTimesMonthmorning(),System.currentTimeMillis(),uid);
        TransInfo appTodayTrafficData=bucketDao.getTrafficDataOfApp(this,subscriberID,networkType,dateTools.getTimesTodayMorning(),System.currentTimeMillis(),uid);

        TextView TextViewAppThisMonthInfo = findViewById(R.id.TextViewAppThisMonthInfo);
        OutputTrafficData appThisMonthTrafficDataRx = bytesFormatter.getPrintSizebyModel(appThisMonthTrafficData.getRx());
        OutputTrafficData appThisMonthTrafficDataTx = bytesFormatter.getPrintSizebyModel(appThisMonthTrafficData.getTx());
        OutputTrafficData appThisMonthTrafficDataTotal = bytesFormatter.getPrintSizebyModel(appThisMonthTrafficData.getTotal());
        TextViewAppThisMonthInfo.setText("上传流量:" +Math.round(Double.valueOf(appThisMonthTrafficDataTx.getValue())*100D )/100D+appThisMonthTrafficDataTx.getType()+
                "  下载流量:" +Math.round(Double.valueOf(appThisMonthTrafficDataRx.getValue())*100D )/100D+appThisMonthTrafficDataRx.getType()+
                "  总量:"+Math.round(Double.valueOf(appThisMonthTrafficDataTotal.getValue())*100D )/100D+appThisMonthTrafficDataTotal.getType());

        TextView TextViewAppTodayInfo = findViewById(R.id.TextViewAppTodayInfo);
        OutputTrafficData appTodayTrafficDataRx = bytesFormatter.getPrintSizebyModel(appTodayTrafficData.getRx());
        OutputTrafficData appTodayTrafficDataTx = bytesFormatter.getPrintSizebyModel(appTodayTrafficData.getTx());
        OutputTrafficData appTodayTrafficDataTotal = bytesFormatter.getPrintSizebyModel(appTodayTrafficData.getTotal());
        TextViewAppTodayInfo.setText("上传流量:" +Math.round(Double.valueOf(appTodayTrafficDataTx.getValue())*100D )/100D+appTodayTrafficDataTx.getType()+
                "  下载流量:" +Math.round(Double.valueOf(appTodayTrafficDataRx.getValue())*100D )/100D+appTodayTrafficDataRx.getType()+
                "  总量:"+Math.round(Double.valueOf(appTodayTrafficDataTotal.getValue())*100D )/100D+appTodayTrafficDataTotal.getType());



       // List<TransInfo> lastThirtyDaysTrafficData =
    }
    private void showChart(View view, List<TransInfo> lastSevenDaysTrafficData, List<Integer> lastSevenDays){
        BytesFormatter bytesFormatter = new BytesFormatter();

        LineChart lineChart = view.findViewById(R.id.LineChartAppLastThirtyDaysTrafficData);
        final ArrayList<Entry> values = new ArrayList<>();
        /*values.add(new Entry(100, 30));*/

        final String[] Xvalues = new String[lastSevenDays.size()];
        for (int i=0;i<lastSevenDays.size();i++){
            values.add(new Entry(i,lastSevenDaysTrafficData.get(i).getTotal()));
            Xvalues[i]= lastSevenDays.get(i) + "日" ;
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
        dataSets.add(lineDataSet);
        LineData data1 = new LineData(dataSets);
        lineChart.setData(data1);
        lineChart.getLegend().setEnabled(false);

        lineChart.getXAxis().setGranularity(1);
        lineChart.getXAxis().setValueFormatter(formatter);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);
        //lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.animateXY(0,1200);

        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

}
