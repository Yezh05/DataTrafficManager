package edu.yezh.datatrafficmanager;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;
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
import java.util.List;

import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.adapter.RecyclerViewAppsTrafficDataAdapter;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;

import static android.content.Context.NETWORK_STATS_SERVICE;

public class MyFragment2 extends Fragment {
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

    public void initialPage(View view, final Context context){
        final int dataPlanStartDay = 1;
        final String subscriberID = "";
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats.Bucket bucket = null;


        DateTools dateTools = new DateTools();
        try {bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", dateTools.getTimesMonthmorning(), System.currentTimeMillis());
            Log.i("Info", "Total: " + (bucket.getRxBytes() + bucket.getTxBytes()));
            long rxBytes = bucket.getRxBytes();
            BytesFormatter bytesFormatter = new BytesFormatter();
            String readableData = bytesFormatter.getPrintSize(rxBytes);
            TextView textView = (TextView) view.findViewById(R.id.DataWLAN);
            textView.setText("本月已下载: " + readableData);

        } catch (RemoteException e) {
            Log.e("bucket", "GetTotalError");
            e.printStackTrace();
        }
        //showRealTimeNetSpeed(view);

        BucketDao bucketDao = new BucketDaoImpl();
        MyFragment1 myFragment1 = new MyFragment1();
        myFragment1.showRealTimeNetSpeed(view);


        RecyclerViewAppsTrafficDataAdapter recyclerViewAppsTrafficDataAdapter = new RecyclerViewAppsTrafficDataAdapter(bucketDao.getInstalledAppsTrafficData(context,subscriberID,dataPlanStartDay,ConnectivityManager.TYPE_WIFI),context);
        RecyclerView RecyclerViewAppsTrafficData = (RecyclerView) view.findViewById(R.id.RecyclerViewAppsTrafficData);
        RecyclerViewAppsTrafficData.setAdapter(recyclerViewAppsTrafficDataAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewAppsTrafficData.setLayoutManager(layoutManager);


        List<Long> lastSevenDaysTrafficData = bucketDao.getLastSevenDaysTrafficData(context, "", ConnectivityManager.TYPE_WIFI);
        showChart(view, lastSevenDaysTrafficData, dateTools.getLastSevenDays());
        Button buttonShowLastSixMonthTrafficData = (Button) view.findViewById(R.id.ButtonShowLastSixMonthTrafficData);
        buttonShowLastSixMonthTrafficData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ShowDataListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subscriberID",subscriberID);
                bundle.putInt("dataPlanStartDay",dataPlanStartDay);
                bundle.putInt("networkType",ConnectivityManager.TYPE_WIFI);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //myFragment1.showChart();
    }

    public void showChart(View view,List<Long> lastSevenDaysTrafficData, List<Integer> lastSevenDays){
        BytesFormatter bytesFormatter = new BytesFormatter();

        LineChart lineChart = (LineChart) view.findViewById(R.id.LineChartLastSevenDaysTrafficData);

        final ArrayList<Entry> values = new ArrayList<>();
        final String[] Xvalues = new String[lastSevenDays.size()];
        for (int i=0;i<lastSevenDays.size();i++){
            values.add(new Entry(i,lastSevenDaysTrafficData.get(i)));
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

        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

}
