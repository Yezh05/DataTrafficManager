package edu.yezh.datatrafficmanager.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.chartTools.CustomMarkerView;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.ui.adapter.RecyclerViewShowMonthsDataListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowDataListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("近12个月使用情况");
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
        String subscriberID = bundle.getString("subscriberID");
        int networkType = bundle.getInt("networkType");
        BucketDao bucketDao = new BucketDaoImpl();

        List<Object> TrafficDataOfLastTwelveMonths = bucketDao.getTrafficDataOfLastTwelveMonths(this,subscriberID,bundle.getInt("dataPlanStartDay"),networkType);

        RecyclerView RecyclerViewShowMonthsDataList = findViewById(R.id.RecyclerViewShowMonthsDataList);
        RecyclerViewShowMonthsDataListAdapter recyclerViewShowMonthsDataListAdapter = new RecyclerViewShowMonthsDataListAdapter(this,subscriberID,networkType,TrafficDataOfLastTwelveMonths);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerViewShowMonthsDataList.setAdapter(recyclerViewShowMonthsDataListAdapter);
        RecyclerViewShowMonthsDataList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerViewShowMonthsDataList.setLayoutManager(layoutManager);

        showChart((List<TransInfo>) TrafficDataOfLastTwelveMonths.get(1),(List<String>) TrafficDataOfLastTwelveMonths.get(0),R.id.LineChartShowDataList);
    }
    private void showChart( List<TransInfo> valueDataList, List<String> xLineNoList, int chartId){
        /*Collections.reverse(valueDataList);
        Collections.reverse(xLineNoList);*/
        LineChart lineChart = findViewById(chartId);
        final ArrayList<Entry> values = new ArrayList<>();
        final String[] Xvalues = new String[xLineNoList.size()];

        for (int i=0;i<xLineNoList.size();i++){
            values.add(new Entry(i,valueDataList.get(i).getTotal()));
            Xvalues[i]= xLineNoList.get(i) ;
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float xvalue, AxisBase axis) {
                return Xvalues[(int) xvalue];
            }
        };
        LineDataSet lineDataSet = new LineDataSet(values,"数据");
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
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
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
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.animateXY(0,1200);
        lineChart.setVisibleXRangeMaximum(6);
        lineChart.setViewPortOffsets(50,50,50,50);
        //lineChart.moveViewToX(valueDataList.size()-1);
        lineChart.getDescription().setEnabled(false);
        CustomMarkerView mv = new CustomMarkerView(this,
                R.layout.view_customer_marker);
        lineChart.setMarkerView(mv);
        lineChart.invalidate();
    }
}
