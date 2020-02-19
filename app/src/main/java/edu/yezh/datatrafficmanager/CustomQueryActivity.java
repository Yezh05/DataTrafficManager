package edu.yezh.datatrafficmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import edu.yezh.datatrafficmanager.adapter.RecyclerViewAppsTrafficDataAdapter;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.SimInfo;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.SimTools;

public class CustomQueryActivity extends AppCompatActivity {
    Calendar calStartTime;
    Calendar calEndTime;
    String subscriberID;
    int networkType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_query);
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
        final Context context = this;

        calStartTime=null;
        calEndTime=null;
        subscriberID = null;
        networkType = -1000;

        initSpinner(context);

        Button buttonCustomQuerySetStartTime = findViewById(R.id.ButtonCustomQuerySetStartTime);
        Button buttonCustomQuerySetEndTime = findViewById(R.id.ButtonCustomQuerySetEndTime);
        buttonCustomQuerySetStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"设置开始时间",Toast.LENGTH_SHORT).show();
                showDateTimeDialog(context,R.id.ButtonCustomQuerySetStartTime);
                //System.out.println("cal:"+cal.toString());
                //TextView TextViewCustomQueryStartTime = findViewById(R.id.TextViewCustomQueryStartTime);
                //TextViewCustomQueryStartTime.setText(""+calStartTime.getTime());
            }
        });
        buttonCustomQuerySetEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            synchronized public void onClick(View v) {
                Toast.makeText(context,"设置结束时间",Toast.LENGTH_SHORT).show();
                showDateTimeDialog(context,v.getId());
            }
        });

        Button ButtonDoCustomQuery = findViewById(R.id.ButtonDoCustomQuery);
        ButtonDoCustomQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCustomQuery(context);
            }
        });
    }
    private  void initSpinner(Context context){
        final List<SimInfo> simInfoList = new SimTools().getSubscriptionInfoList(context);
        String[] spinnerTexts = new String[simInfoList.size()+1];
        for (int i=0;i<simInfoList.size();i++){
            spinnerTexts[i]  = "Sim卡"+(i+1) +simInfoList.get(i).getSubscriptionInfo().getCarrierName();
        }
        spinnerTexts[simInfoList.size()] = "无线局域网";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,spinnerTexts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.SpinnerNetWorkType);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id==simInfoList.size()){
                    subscriberID="";
                    networkType = ConnectivityManager.TYPE_WIFI;
                }else
                {
                    subscriberID = simInfoList.get( Integer.parseInt(String.valueOf(id)) ).getSubscriberId();
                    networkType = ConnectivityManager.TYPE_MOBILE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /*if (id==simInfoList.size()){
                    subscriberID="";
                    networkType = ConnectivityManager.TYPE_WIFI;
                }else
                {
                    subscriberID = simInfoList.get( Integer.parseInt(String.valueOf(id)) ).getSubscriberId();
                    networkType = ConnectivityManager.TYPE_MOBILE;
                }*/
    }
    private  void showDateTimeDialog(final Context context, final int flag){
        //final Cal cal[] = new Cal[1];
        final Calendar calendar = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            final TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public  void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    /*cal[0].setHour(hourOfDay);
                    cal[0].setMinute(minute);
                    System.out.println(cal[0].toString());*/
                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);
                    calendar.set(Calendar.MILLISECOND,0);
                    calendar.set(Calendar.SECOND,0);
                    System.out.println(calendar.getTime());
                    int textflag = R.id.TextViewCustomQueryStartTime;
                    switch (flag){
                        case R.id.ButtonCustomQuerySetStartTime : calStartTime=calendar; textflag=R.id.TextViewCustomQueryStartTime; break;
                        case R.id.ButtonCustomQuerySetEndTime : calEndTime=calendar; textflag=R.id.TextViewCustomQueryEndTime; break;
                    }
                    TextView textView = findViewById(textflag);
                    textView.setText(calendar.getTime()+"");
                }
            }, 00, 00, true);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public  void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        /*cal[0].setYear(year);
                    cal[0].setMonth(month);
                    cal[0].setDay(dayOfMonth);*/
                    calendar.set(year,month,dayOfMonth);
                        timePickerDialog.show();
                }
            });
            datePickerDialog.show();
        }else {
            Toast.makeText(context,"系统不支持此功能",Toast.LENGTH_SHORT).show();
        }
        //System.out.println(calendar.getTime());
    }
    private void handleCustomQuery(Context context){
        if (calStartTime!=null && calEndTime!=null){
            BucketDao bucketDao = new BucketDaoImpl();
            BytesFormatter bytesFormatter = new BytesFormatter();
            TransInfo customQueryData = bucketDao.getTrafficData(context,subscriberID,networkType,calStartTime.getTimeInMillis(),calEndTime.getTimeInMillis());

            OutputTrafficData customQueryDataTotal = bytesFormatter.getPrintSizeByModel(customQueryData.getTotal());
            TextView TextViewCustomQueryTotal = findViewById(R.id.TextViewCustomQueryTotal);
            TextViewCustomQueryTotal.setText(Math.round(Double.valueOf(customQueryDataTotal.getValue())*100D)/100D + customQueryDataTotal.getType());

            OutputTrafficData customQueryDataRx = bytesFormatter.getPrintSizeByModel(customQueryData.getRx());
            TextView TextViewCustomQueryRx = findViewById(R.id.TextViewCustomQueryRx);
            TextViewCustomQueryRx.setText(Math.round(Double.valueOf(customQueryDataRx.getValue())*100D)/100D + customQueryDataRx.getType());

            OutputTrafficData customQueryDataTx = bytesFormatter.getPrintSizeByModel(customQueryData.getTx());
            TextView TextViewCustomQueryTx = findViewById(R.id.TextViewCustomQueryTx);
            TextViewCustomQueryTx.setText(Math.round(Double.valueOf(customQueryDataTx.getValue())*100D)/100D + customQueryDataTx.getType());

            RecyclerViewAppsTrafficDataAdapter recyclerViewAppsTrafficDataAdapter = new RecyclerViewAppsTrafficDataAdapter(bucketDao.getAllInstalledAppsTrafficData(context,subscriberID,networkType,calStartTime.getTimeInMillis(),calEndTime.getTimeInMillis()),context,subscriberID,networkType);
            RecyclerView RecyclerViewAppsTrafficData = findViewById(R.id.RecyclerViewAppsTrafficData);
            RecyclerViewAppsTrafficData.setAdapter(recyclerViewAppsTrafficDataAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            RecyclerViewAppsTrafficData.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
            RecyclerViewAppsTrafficData.setLayoutManager(layoutManager);

        }else {
            Toast.makeText(context,"请选择开始和结束时间",Toast.LENGTH_SHORT).show();
        }
    }
}

