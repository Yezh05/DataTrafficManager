package edu.yezh.datatrafficmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
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

import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.adapter.RecyclerViewAppsTrafficDataAdapter;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.chartTools.CustomMarkerView;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;

public class MyFragment2 extends Fragment {
    final int networkType = ConnectivityManager.TYPE_WIFI;
    public MyFragment2() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.t2, container, false);
        //TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        //      txt_content.setText("第一个Fragment");
        Log.e("Fragment", "WLAN页面");
        Context context = this.getContext();

        initialPage(view,context);

        /*Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_refresh:
                        // Toast.makeText(context,"asdsadsada",Toast.LENGTH_LONG).show();
                       initialPage(view,context);
                        break;

                }
                return false;
            }
        });*/

        return view;
    }

    private void initialPage(View view, final Context context){
        final int dataPlanStartDay = 1;
        final String subscriberID = "";
        BytesFormatter bytesFormatter = new BytesFormatter();
        BucketDao bucketDao = new BucketDaoImpl();
        DateTools dateTools = new DateTools();

        try {
            TransInfo ThisMonthUsageData = bucketDao.getTrafficDataOfThisMonth(context,subscriberID,networkType);
            OutputTrafficData ThisMonthUsageDataRx = bytesFormatter.getPrintSizeByModel(ThisMonthUsageData.getRx());
            TextView textViewDataWLANThisMonthRx = view.findViewById(R.id.DataWLANThisMonthRx);
            textViewDataWLANThisMonthRx.setText(Math.round(Double.valueOf(ThisMonthUsageDataRx.getValue())*100D)/100D + ThisMonthUsageDataRx.getType());

            OutputTrafficData ThisMonthUsageDataTx = bytesFormatter.getPrintSizeByModel(ThisMonthUsageData.getTx());
            TextView textViewDataWLANThisMonthTx = view.findViewById(R.id.DataWLANThisMonthTx);
            textViewDataWLANThisMonthTx.setText(Math.round(Double.valueOf(ThisMonthUsageDataTx.getValue())*100D)/100D + ThisMonthUsageDataTx.getType());

            OutputTrafficData ThisMonthUsageDataTotal = bytesFormatter.getPrintSizeByModel(ThisMonthUsageData.getTotal());
            TextView textViewDataWLANThisMonthTotal = view.findViewById(R.id.DataWLANThisMonthTotal);
            textViewDataWLANThisMonthTotal.setText(Math.round(Double.valueOf(ThisMonthUsageDataTotal.getValue())*100D)/100D + ThisMonthUsageDataTotal.getType());

            TransInfo TodayUsageData = bucketDao.getTrafficDataOfToday(context,subscriberID,networkType);
            OutputTrafficData TodayUsageDataRx = bytesFormatter.getPrintSizeByModel(TodayUsageData.getRx());
            TextView textViewDataWLANTodayRx = view.findViewById(R.id.DataWLANTodayRx);
            textViewDataWLANTodayRx.setText(Math.round(Double.valueOf(TodayUsageDataRx.getValue())*100D)/100D + TodayUsageDataRx.getType());

            OutputTrafficData TodayUsageDataTx = bytesFormatter.getPrintSizeByModel(TodayUsageData.getTx());
            TextView textViewDataWLANTodayTx = view.findViewById(R.id.DataWLANTodayTx);
            textViewDataWLANTodayTx.setText(Math.round(Double.valueOf(TodayUsageDataTx.getValue())*100D)/100D + TodayUsageDataTx.getType());

            OutputTrafficData TodayUsageDataTotal = bytesFormatter.getPrintSizeByModel(TodayUsageData.getTotal());
            TextView textViewDataWLANTodayTotal = view.findViewById(R.id.DataWLANTodayTotal);
            textViewDataWLANTodayTotal.setText(Math.round(Double.valueOf(TodayUsageDataTotal.getValue())*100D)/100D + TodayUsageDataTotal.getType());

        } catch (Exception e) {
            Log.e("严重错误", e.toString() );
        }

        MyFragment1 myFragment1 = new MyFragment1();
        myFragment1.showRealTimeNetSpeed(view);

        RecyclerViewAppsTrafficDataAdapter recyclerViewAppsTrafficDataAdapter = new RecyclerViewAppsTrafficDataAdapter(bucketDao.getAllInstalledAppsTrafficData(context,subscriberID,networkType,dateTools.getTimesStartDayMorning(dataPlanStartDay),System.currentTimeMillis()),context,subscriberID,networkType);
        RecyclerView RecyclerViewAppsTrafficData = (RecyclerView) view.findViewById(R.id.RecyclerViewAppsTrafficData);
        RecyclerViewAppsTrafficData.setAdapter(recyclerViewAppsTrafficDataAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewAppsTrafficData.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        RecyclerViewAppsTrafficData.setLayoutManager(layoutManager);

        List<TransInfo> lastSevenDaysTrafficData = bucketDao.getTrafficDataOfLastThirtyDays(context, subscriberID, networkType);
        List<Long> DaysNoList = dateTools.getLastThirtyDaysMap().get("No");
        Collections.reverse(lastSevenDaysTrafficData);
        Collections.reverse(DaysNoList);
        showChart(view, lastSevenDaysTrafficData,DaysNoList);

        Button buttonShowLastSixMonthTrafficData = (Button) view.findViewById(R.id.ButtonShowLastSixMonthTrafficData);
        buttonShowLastSixMonthTrafficData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ShowDataListActivity.class);
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
                R.layout.customer_marker_view);
        lineChart.setMarkerView(mv);

        lineChart.invalidate();
    }

}
