package edu.yezh.datatrafficmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.ui.adapter.RecyclerViewAppsTrafficDataAdapter;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.chartTools.CustomMarkerView;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;

public class MyFragmentWifiPage extends Fragment {
    private final int networkType = ConnectivityManager.TYPE_WIFI;
    public MyFragmentWifiPage() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_wifi_page, container, false);
        Log.e("Fragment", "WLAN页面");
        final Context context = this.getContext();
        final Handler handlerRefresh = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    initialPage(view,context);
                    handlerRefresh.postDelayed(this,300000);
                }catch (Exception e){
                    e.printStackTrace();
                    handlerRefresh.postDelayed(this,1000);
                }
            }
        };
        handlerRefresh.postDelayed(runnable,00000);
        return view;
    }

    private void initialPage(final View view, final Context context){
        final int dataPlanStartDay = 1;
        final String subscriberID = "";
        BytesFormatter bytesFormatter = new BytesFormatter();
        final BucketDao bucketDao = new BucketDaoImpl();
        final DateTools dateTools = new DateTools();

        try {
            TransInfo ThisMonthUsageData = bucketDao.getTrafficDataOfThisMonth(context,subscriberID,networkType);
            OutputTrafficData ThisMonthUsageDataRx = bytesFormatter.getPrintSizeByModel(ThisMonthUsageData.getRx());
            TextView textViewDataWLANThisMonthRx = view.findViewById(R.id.DataWLANThisMonthRx);
            textViewDataWLANThisMonthRx.setText(ThisMonthUsageDataRx.getValueWithTwoDecimalPoint() + ThisMonthUsageDataRx.getType());

            OutputTrafficData ThisMonthUsageDataTx = bytesFormatter.getPrintSizeByModel(ThisMonthUsageData.getTx());
            TextView textViewDataWLANThisMonthTx = view.findViewById(R.id.DataWLANThisMonthTx);
            textViewDataWLANThisMonthTx.setText(ThisMonthUsageDataTx.getValueWithTwoDecimalPoint() + ThisMonthUsageDataTx.getType());

            OutputTrafficData ThisMonthUsageDataTotal = bytesFormatter.getPrintSizeByModel(ThisMonthUsageData.getTotal());
            TextView textViewDataWLANThisMonthTotal = view.findViewById(R.id.DataWLANThisMonthTotal);
            textViewDataWLANThisMonthTotal.setText(ThisMonthUsageDataTotal.getValueWithTwoDecimalPoint() + ThisMonthUsageDataTotal.getType());

            TransInfo TodayUsageData = bucketDao.getTrafficDataOfToday(context,subscriberID,networkType);
            OutputTrafficData TodayUsageDataRx = bytesFormatter.getPrintSizeByModel(TodayUsageData.getRx());
            TextView textViewDataWLANTodayRx = view.findViewById(R.id.DataWLANTodayRx);
            textViewDataWLANTodayRx.setText(TodayUsageDataRx.getValueWithTwoDecimalPoint() + TodayUsageDataRx.getType());

            OutputTrafficData TodayUsageDataTx = bytesFormatter.getPrintSizeByModel(TodayUsageData.getTx());
            TextView textViewDataWLANTodayTx = view.findViewById(R.id.DataWLANTodayTx);
            textViewDataWLANTodayTx.setText(TodayUsageDataTx.getValueWithTwoDecimalPoint() + TodayUsageDataTx.getType());

            OutputTrafficData TodayUsageDataTotal = bytesFormatter.getPrintSizeByModel(TodayUsageData.getTotal());
            TextView textViewDataWLANTodayTotal = view.findViewById(R.id.DataWLANTodayTotal);
            textViewDataWLANTodayTotal.setText(TodayUsageDataTotal.getValueWithTwoDecimalPoint() + TodayUsageDataTotal.getType());

        } catch (Exception e) {
            Log.e("严重错误", e.toString() );
        }

        MyFragmentMobilePage myFragmentMobilePage = new MyFragmentMobilePage();
        myFragmentMobilePage.showRealTimeNetSpeed(view);
        myFragmentMobilePage.showMoreAppsTrafficInfo(view,subscriberID,networkType);

        RecyclerViewAppsTrafficDataAdapter recyclerViewAppsTrafficDataAdapter = new RecyclerViewAppsTrafficDataAdapter(bucketDao.getAllInstalledAppsTrafficData(context,subscriberID,networkType,dateTools.getTimesStartDayMorning(dataPlanStartDay),System.currentTimeMillis()),context,subscriberID,networkType);
        RecyclerView RecyclerViewAppsTrafficData = view.findViewById(R.id.RecyclerViewAppsTrafficData);
        RecyclerViewAppsTrafficData.setAdapter(recyclerViewAppsTrafficDataAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewAppsTrafficData.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        RecyclerViewAppsTrafficData.setLayoutManager(layoutManager);

        final List<TransInfo> lastSevenDaysTrafficData = bucketDao.getTrafficDataOfLastThirtyDays(context, subscriberID, networkType);
        final List<Long> DaysNoList = dateTools.getLastThirtyDaysMap().get("No");
        Collections.reverse(lastSevenDaysTrafficData);
        Collections.reverse(DaysNoList);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showChart(view, lastSevenDaysTrafficData,DaysNoList);
            }
        },1000L);

        Button buttonShowLastSixMonthTrafficData =  view.findViewById(R.id.ButtonShowLastSixMonthTrafficData);
        buttonShowLastSixMonthTrafficData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowDataListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subscriberID",subscriberID);
                bundle.putInt("dataPlanStartDay",dataPlanStartDay);
                bundle.putInt("networkType",networkType);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void showChart(View view,List<TransInfo> valueDataList, List<Long> DaysNoList){
        System.out.println("lastSevenDaysTrafficData:"+valueDataList.size());

        LineChart lineChart = view.findViewById(R.id.LineChartLastSevenDaysTrafficData);

        final ArrayList<Entry> values = new ArrayList<>();
        final String[] Xvalues = new String[DaysNoList.size()];
        for (int i=0;i<DaysNoList.size();i++){
            values.add(new Entry(i,valueDataList.get(i).getTotal()));
            Xvalues[i]= DaysNoList.get(i) + "日" ;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float xvalue, AxisBase axis) {
                return Xvalues[(int) xvalue];
            }

        };
        LineDataSet lineDataSet = new LineDataSet(values,"数据");
        //lineDataSet.setValues(values);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
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

        dataSets.add(lineDataSet);
        LineData data1 = new LineData(dataSets);
        lineChart.setData(data1);
        lineChart.getLegend().setEnabled(false);

        lineChart.getXAxis().setGranularity(1);
        lineChart.getXAxis().setValueFormatter(formatter);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.animateXY(000,1200);
        lineChart.setVisibleXRangeMaximum(6);
        lineChart.setViewPortOffsets(50,50,50,50);
        lineChart.moveViewToX(valueDataList.size()-1);
        lineChart.getDescription().setEnabled(false);

        CustomMarkerView mv = new CustomMarkerView(getContext(),
                R.layout.view_customer_marker);
        lineChart.setMarkerView(mv);

        lineChart.invalidate();
    }

}
