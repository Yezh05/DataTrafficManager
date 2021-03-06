package edu.yezh.datatrafficmanager.ui;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.tools.widget.DesktopWidget;
import edu.yezh.datatrafficmanager.ui.adapter.RecyclerViewAppsTrafficDataAdapter;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.dao.db.AppBaseInfoDao;
import edu.yezh.datatrafficmanager.dao.db.AppPreferenceDao;
import edu.yezh.datatrafficmanager.dao.db.DataTrafficRegulateDao;
import edu.yezh.datatrafficmanager.dao.sp.NetworkPlanDao;
import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.SimInfo;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.model.sp.Sp_NetworkPlan;
import edu.yezh.datatrafficmanager.model.tb.Tb_AppBaseInfo;
import edu.yezh.datatrafficmanager.model.tb.Tb_AppPreference;
import edu.yezh.datatrafficmanager.model.tb.Tb_DataTrafficRegulate;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.NotificationTools;
import edu.yezh.datatrafficmanager.tools.SimTools;
import edu.yezh.datatrafficmanager.tools.chartTools.CustomMarkerView;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;
import edu.yezh.datatrafficmanager.tools.sms.SMSTools;
import edu.yezh.datatrafficmanager.tools.sms.SmsReceiver;
import edu.yezh.datatrafficmanager.ui.adapter.RecyclerViewAppsWarningAdapter;


import static android.content.Context.MODE_PRIVATE;

public class MyFragmentMobilePage extends Fragment {
    private final int networkType = ConnectivityManager.TYPE_MOBILE;
    private int ACTIVE_SIM_PAGE_NO;
    private int dataPlanStartDay;
    private Button buttonRefresh;
    //private Handler handler;

    public MyFragmentMobilePage() {
    }

    //public String subscriberID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_mobile_page, container, false);
        Log.e("标签页", "移动网络页面");
        final Context context = this.getContext();
        SimTools simTools = new SimTools();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        }
        final List<SimInfo> simInfoList = simTools.getSubscriptionInfoList(context.getApplicationContext());
        System.out.println("SIM卡数量："+simInfoList.size());
        //System.out.println("simInfoList:"+simInfoList);
        showRealTimeNetSpeed(view);

        String nowActiveSubscriberId = SimTools.getNowActiveSubscriberId(context,0000);
        final Button button_SIM1 = view.findViewById(R.id.Button_SIM1);
        final Button button_SIM2 = view.findViewById(R.id.Button_SIM2);
        button_SIM1.setText("SIM卡1:" + simInfoList.get(0).getSubscriptionInfo().getCarrierName());
        button_SIM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_SIM1.setTextColor(Color.parseColor("#009688"));
                button_SIM2.setTextColor(Color.BLACK);
                ACTIVE_SIM_PAGE_NO = 1;
                buttonRefresh = button_SIM1;
                setTrafficDataView(view, simInfoList.get(0).getSubscriberId());
            }
        });
        if (simInfoList.size() == 2) {
            button_SIM2.setText("SIM卡2:" + simInfoList.get(1).getSubscriptionInfo().getCarrierName());
            button_SIM2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button_SIM2.setTextColor(Color.parseColor("#009688"));
                    button_SIM1.setTextColor(Color.BLACK);
                    ACTIVE_SIM_PAGE_NO = 2;
                    buttonRefresh = button_SIM2;
                    setTrafficDataView(view, simInfoList.get(1).getSubscriberId());
                }
            });
        } else {
            button_SIM2.setVisibility(View.GONE);
        }
        if (nowActiveSubscriberId.equals(simInfoList.get(0).getSubscriberId())){
            System.out.println("Sim1相等");
            button_SIM1.performClick();
        }else if (nowActiveSubscriberId.equals(simInfoList.get(1).getSubscriberId())){
            System.out.println("Sim2相等");
            button_SIM2.performClick();
        }else {
            System.out.println("都不相等");
        }

        handleToolBarItem(context,simInfoList);
        //System.out.println("网络类型："+networkType);

        final Handler handlerRefresh = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    refresh();
                    handlerRefresh.postDelayed(this,300000);
                }catch (Exception e){
                    e.printStackTrace();
                    handlerRefresh.postDelayed(this,1000);
                }
            }
        };
        handlerRefresh.postDelayed(runnable,300000);


        return view;
    }

    /*获取并显示流量使用情况*/
    public void setTrafficDataView(final View view, final String subscriberID) {
        try {
            //System.out.println("IMSI:"+subscriberID);
            SharedPreferences sp = getActivity().getSharedPreferences("TrafficManager", MODE_PRIVATE);
            final Context context = this.getContext();
            final BucketDao bucketDao = new BucketDaoImpl();
            BytesFormatter bytesFormatter = new BytesFormatter();
            final DateTools dateTools = new DateTools();

            long dataPlanLong = showTextViewDataPlan(view, subscriberID);
            long ThisMonthData = bucketDao.getTrafficDataOfThisMonth(context, subscriberID, networkType).getTotal();

            OutputTrafficData readableThisMonthData = bytesFormatter.getPrintSizeByModel(ThisMonthData);
            TextView TextViewData4GThisMonth = view.findViewById(R.id.TextViewData4GThisMonth);
            TextViewData4GThisMonth.setText(readableThisMonthData.getValueWithTwoDecimalPoint() + readableThisMonthData.getType());

            dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID,-1);

            if (dataPlanStartDay<1){
                openDataPlanEditDialog(subscriberID,false);
            }

            long ignoreTrafficDataThisMonth = statisticAppIgnoreTrafficData(context, subscriberID, dateTools.getTimesStartDayMorning(dataPlanStartDay));
            long ignoreTrafficDataToday = statisticAppIgnoreTrafficData(context, subscriberID, dateTools.getTimesTodayMorning());
            System.out.println("本月忽略应用流量：" + ignoreTrafficDataThisMonth);
            System.out.println("本日忽略应用流量：" + ignoreTrafficDataToday);

            long totalBytesStartDayToToday = bucketDao.getTrafficDataFromStartDayToToday(context, subscriberID, dataPlanStartDay, networkType).getTotal();
            long realTotalBytesStartDayToToday = totalBytesStartDayToToday - ignoreTrafficDataThisMonth;

            try {
                DataTrafficRegulateDao dataTrafficRegulateDao = new DataTrafficRegulateDao(context);
                Tb_DataTrafficRegulate tb_dataTrafficRegulate = dataTrafficRegulateDao.find(subscriberID);
                if (dateTools.getTimesStartDayMorning(dataPlanStartDay)<= tb_dataTrafficRegulate.getSettime() && tb_dataTrafficRegulate.getSettime() <= System.currentTimeMillis()){
                    realTotalBytesStartDayToToday = realTotalBytesStartDayToToday - tb_dataTrafficRegulate.getValue();
                }
            }catch (Exception e){
                System.out.println("数据错误"+e.toString());
            }

            OutputTrafficData readableDataStartDayToToday = bytesFormatter.getPrintSizeByModel(realTotalBytesStartDayToToday);
            TextView TextViewData4GStartDayToToday = view.findViewById(R.id.TextViewData4GStartDayToToday);
            TextViewData4GStartDayToToday.setText(readableDataStartDayToToday.getValueWithTwoDecimalPoint() + readableDataStartDayToToday.getType());

            float DataUseStatus = (float) ((double) realTotalBytesStartDayToToday / (double)dataPlanLong) * 100F;
            //System.out.println("realTotalBytesStartDayToToday="+realTotalBytesStartDayToToday+"\ndataPlanLong:"+dataPlanLong+"\n比例："+DataUseStatus);
            final int PercentDataUseStatus = Math.round(DataUseStatus);
            String TextDataUseStatus = "";
            if (PercentDataUseStatus < 0) {
                TextDataUseStatus = "请设置\n流量限额";
            }
            if (PercentDataUseStatus >= 0 && PercentDataUseStatus < 30) {
                TextDataUseStatus = "流量充足\n放心使用";
            } else if (PercentDataUseStatus >= 30 && PercentDataUseStatus < 50) {
                TextDataUseStatus = "流量较多\n正常使用";
            } else if (PercentDataUseStatus >= 50 && PercentDataUseStatus < 80) {
                TextDataUseStatus = "流量过半\n注意使用";
            } else if (PercentDataUseStatus >= 80 && PercentDataUseStatus < 90) {
                TextDataUseStatus = "流量较少\n谨慎使用";
            } else if (PercentDataUseStatus >= 90 && PercentDataUseStatus < 100) {
                TextDataUseStatus = "流量告急\n谨慎使用";
            } else if (PercentDataUseStatus >= 100) {
                TextDataUseStatus = "流量使用量\n已超过套餐限额\n敬请留意";
            }
            TextView TextViewDataUseStatus =  view.findViewById(R.id.TextViewDataUseStatus);
            TextViewDataUseStatus.setText((PercentDataUseStatus) + "%\n" + TextDataUseStatus);

            final List<TransInfo> lastThirtyDaysTrafficData = bucketDao.getTrafficDataOfLastThirtyDays(context, subscriberID, networkType);
            OutputTrafficData todayUsage = bytesFormatter.getPrintSizeByModel(lastThirtyDaysTrafficData.get(0).getTotal() - ignoreTrafficDataToday);

            TextView TextViewData4GToday = view.findViewById(R.id.TextViewData4GToday);
            TextViewData4GToday.setText(todayUsage.getValueWithTwoDecimalPoint()+ todayUsage.getType());

            OutputTrafficData restTrafficDataAmount;
            if( (dataPlanLong - realTotalBytesStartDayToToday)>0){
                restTrafficDataAmount= bytesFormatter.getPrintSizeByModel(dataPlanLong - realTotalBytesStartDayToToday);}else {
                restTrafficDataAmount= bytesFormatter.getPrintSizeByModel(0);
            }
            OutputTrafficData dataPlan = bytesFormatter.getPrintSizeByModel(dataPlanLong);

            NotificationTools.setNotification(context,
                    "今日 " + todayUsage.getValueWithNoDecimalPoint() + todayUsage.getType() + "   "
                            + "剩余 " + restTrafficDataAmount.getValueWithNoDecimalPoint() + restTrafficDataAmount.getType() + "   "
                            + "总量 " + dataPlan.getValueWithNoDecimalPoint() + dataPlan.getType()
                    , TextDataUseStatus,true,1000);

            int monthWarningFlag = sp.getInt("monthWarningFlag",80);
            System.out.println("月流量限额百分比:"+monthWarningFlag);
            if(PercentDataUseStatus>=monthWarningFlag){
                NotificationTools.setNotification(context,"流量告警","流量使用已超过"+monthWarningFlag+"%\n" +
                                "剩余 " + restTrafficDataAmount.getValueWithNoDecimalPoint() + restTrafficDataAmount.getType()
                        ,false,5000);
            }

            DesktopWidget.updateAppWidget(context,"今日" + todayUsage.getValueWithNoDecimalPoint() + todayUsage.getType() + " "
                    + "剩余" + restTrafficDataAmount.getValueWithNoDecimalPoint() + restTrafficDataAmount.getType() + " "
                    + "总量" + dataPlan.getValueWithNoDecimalPoint() + dataPlan.getType(),TextDataUseStatus.replaceAll("\r|\n", " "));

            final List<Long> DaysNoList = dateTools.getLastThirtyDaysMap().get("No");
            Collections.reverse(lastThirtyDaysTrafficData);
            Collections.reverse(DaysNoList);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showChart(view, PercentDataUseStatus, lastThirtyDaysTrafficData, DaysNoList);
                }
            },500L);

            RecyclerViewAppsTrafficDataAdapter recyclerViewAppsTrafficDataAdapter = new RecyclerViewAppsTrafficDataAdapter(
                    bucketDao.getAllInstalledAppsTrafficData(context, subscriberID, networkType, dateTools.getTimesStartDayMorning(dataPlanStartDay),
                            dateTools.getTimesTodayEnd()), context, subscriberID, networkType);

            RecyclerView RecyclerViewAppsTrafficData = view.findViewById(R.id.RecyclerViewAppsTrafficData);
            RecyclerViewAppsTrafficData.setAdapter(recyclerViewAppsTrafficDataAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            RecyclerViewAppsTrafficData.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            RecyclerViewAppsTrafficData.setLayoutManager(layoutManager);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAppTrafficDataWarning(context, view, subscriberID);
                }
            },200L);

            FloatingActionButton fab = view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDataPlanEditDialog(subscriberID,true);
                }
            });
            Button buttonShowLastSixMonthTrafficData = view.findViewById(R.id.ButtonShowLastSixMonthTrafficData);
            buttonShowLastSixMonthTrafficData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowDataListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("subscriberID", subscriberID);
                    bundle.putInt("dataPlanStartDay", dataPlanStartDay);
                    bundle.putInt("networkType", networkType);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            showMoreAppsTrafficInfo(view,subscriberID,networkType);
        } catch (Exception e) {
            Log.e("出现错误", "错误：" + e.toString());
            e.printStackTrace();
        }
    }


    /*流量套餐限额设置框*/
    @SuppressLint("RestrictedApi")
    private void openDataPlanEditDialog(final String subscriberID, final boolean isHaveData) {
        final Context context = this.getContext();
        final EditText editTextDataPlanStartDay = new EditText(context);

        TextInputLayout textInputLayout = new TextInputLayout(context);
        textInputLayout.addView(editTextDataPlanStartDay);
        textInputLayout.setHint("请输入套餐起始日");
        final AlertDialog.Builder builderDataPlanStartDay = new AlertDialog.Builder(context);
        builderDataPlanStartDay.setTitle("设置套餐起始日").setView(textInputLayout,55,50,55,50);
        if (isHaveData) {
            builderDataPlanStartDay.setNegativeButton("取消", null);
        }
        builderDataPlanStartDay.setCancelable(false);
        builderDataPlanStartDay.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (editTextDataPlanStartDay.getText().toString().equals("")) {
                    Toast.makeText(context, "非法数值", Toast.LENGTH_LONG).show();
                    //dialog.cancel();
                     openDataPlanEditDialog( subscriberID, isHaveData);
                } else {
                    final int dataPlanStartDay = Integer.valueOf(editTextDataPlanStartDay.getText().toString());
                    if (dataPlanStartDay < 1 || dataPlanStartDay > 31) {
                        Toast.makeText(context, "非法数值", Toast.LENGTH_LONG).show();
                        //dialog.cancel();
                         openDataPlanEditDialog(  subscriberID,   isHaveData);
                    } else {
                        dialog.dismiss();
                        final View viewCustomerDialogDataInput = LayoutInflater.from(context).inflate(R.layout.view_customer_dialog_data_input, null);
                        TextView textViewHint = viewCustomerDialogDataInput.findViewById(R.id.TextViewHint);
                        textViewHint.setText("请输入套餐流量额度");
                        final EditText editText = viewCustomerDialogDataInput.findViewById(R.id.EditText_Traffic_Data_Value);
                        final Spinner spinnerDataType = viewCustomerDialogDataInput.findViewById(R.id.Spinner_Traffic_Data_Type);
                        spinnerDataType.setSelection(3);
                        final AlertDialog builderDataPlanAlertDialog = new AlertDialog.Builder(context).create();
                        builderDataPlanAlertDialog.setTitle("设置套餐流量额度");
                        builderDataPlanAlertDialog.setView(viewCustomerDialogDataInput);
                        builderDataPlanAlertDialog.setCancelable(false);
                        Button btnCancel = viewCustomerDialogDataInput.findViewById(R.id.ButtonCustomDialogCancel);
                        if (isHaveData) {
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                builderDataPlanAlertDialog.dismiss();
                            }
                        });}
                        Button btnConfirm = viewCustomerDialogDataInput.findViewById(R.id.ButtonCustomDialogConfirm);
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BytesFormatter bytesFormatter = new BytesFormatter();
                                String inputData = editText.getText().toString();

                                long inputUse = bytesFormatter.convertValueToLong(Double.valueOf(inputData), spinnerDataType.getSelectedItem().toString());
                                if (inputUse <= 0) {
                                    //builderDataPlanAlertDialog.dismiss();
                                    Toast.makeText(context, "非法的数值", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Sp_NetworkPlan sp_networkPlan = new Sp_NetworkPlan(inputUse, dataPlanStartDay);
                                NetworkPlanDao networkPlanDao = new NetworkPlanDao(context, subscriberID);
                                networkPlanDao.setPlanData(sp_networkPlan);
                                builderDataPlanAlertDialog.dismiss();
                                showTextViewDataPlan(getView(), subscriberID);
                                refresh();
                            }
                        });
                        builderDataPlanAlertDialog.show();
                    }
                }
            }
        });
        builderDataPlanStartDay.show();
    }
    /*获取并显示流量套餐限额*/
    private long showTextViewDataPlan(View view, String subscriberID) {
        NetworkPlanDao networkPlanDao = new NetworkPlanDao(getContext(),subscriberID);
        Sp_NetworkPlan sp_networkPlan = networkPlanDao.getPlanData();
        long dataPlanLong = sp_networkPlan.getDataPlanLong();
        int dataPlanStartDay = sp_networkPlan.getDataPlanStartDay();
        System.out.println("流量套餐限额"+ "dataPlan_" + subscriberID + " : " + dataPlanLong);
        System.out.println("流量套餐起始日"+"dataPlanStartDay_" + subscriberID + " : " + dataPlanStartDay);
        TextView TextViewDataPlan = (TextView) view.findViewById(R.id.TextViewDataPlan);

        BytesFormatter bytesFormatter = new BytesFormatter();
        OutputTrafficData dataPlan = bytesFormatter.getPrintSizeByModel(dataPlanLong);
        TextViewDataPlan.setText(dataPlan.getValueWithTwoDecimalPoint() + dataPlan.getType());

        TextView TextViewDataPlanStartDay = view.findViewById(R.id.TextViewDataPlanStartDay);
        TextViewDataPlanStartDay.setText("每月" + dataPlanStartDay + "日");
        return dataPlanLong;
    }

    public void showMoreAppsTrafficInfo(View view,String subscriberID,int networkType){
        TextView TextViewMoreAppsTrafficInfo = view.findViewById(R.id.TextViewMoreAppsTrafficInfo);
        TextViewMoreAppsTrafficInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] timeLimit = new String[]{"今日","本周","本月"};
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("选择时间段").setItems(timeLimit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(view.getContext(),timeLimit[which],Toast.LENGTH_LONG).show();

                        DateTools dateTools = new DateTools();
                        long endTime = dateTools.getTimesTodayEnd();
                        long startTime = 0;
                        switch (which){
                            case 0: startTime=dateTools.getTimesTodayMorning(); break;
                            case 1: startTime=dateTools.getTimesWeekMorning();  break;
                            case 2: startTime=dateTools.getTimesMonthMorning();break;

                        }
                        Intent intent = new Intent(view.getContext(), CustomQueryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("subscriberID",subscriberID);
                        bundle.putInt("networkType",networkType);
                        bundle.putInt("startCode",900);
                        bundle.putLong("startTime", startTime);
                        bundle.putLong("endTime", endTime);
                        intent.putExtras(bundle);
                        view.getContext().startActivity(intent);
                    }
                }).create();
                builder.show();
            }
        });
    }

    private void showChart(View view, int PercentDataUseStatus, List<TransInfo> valueDataList, List<Long> DaysNoList) {
        if (PercentDataUseStatus>100){
            PercentDataUseStatus=100;
        }
        PieChart pieChart = view.findViewById(R.id.Chart1);
        List yVals = new ArrayList<>();
        yVals.add(new PieEntry(100 - PercentDataUseStatus, 100 - PercentDataUseStatus + "%未使用"));
        yVals.add(new PieEntry(PercentDataUseStatus, PercentDataUseStatus + "%已使用"));

        List colors = new ArrayList<>();
        /*colors.add(Color.parseColor("#4A92FC"));
        colors.add(Color.parseColor("#ee6e55"));*/
        /*colors.add(Color.parseColor("#66CC66"));
        colors.add(Color.parseColor("#CCFF99"));*/
        colors.add(Color.parseColor("#009688"));
        colors.add(Color.parseColor("#20B2AA"));
        PieDataSet pieDataSet = new PieDataSet(yVals, "已使用流量");
        pieDataSet.setColors(colors);
        //pieDataSet.setValueFormatter(new PercentFormatter());
        pieDataSet.setDrawValues(false);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        //pieDataSet
        PieData pieData = new PieData(pieDataSet);
        Description description = new Description();
        description.setText("");
        pieChart.setEntryLabelColor(Color.parseColor("#114242"));
        pieChart.setDescription(description);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
        pieChart.animateXY(2000, 2000);

        LineChart lineChart = view.findViewById(R.id.LineChartLastSevenDaysTrafficData);
        final ArrayList<Entry> values = new ArrayList<>();
        /*values.add(new Entry(100, 30));*/

        final String[] XValues = new String[DaysNoList.size()];
        for (int i = 0; i < DaysNoList.size(); i++) {
            values.add(new Entry(i, valueDataList.get(i).getTotal()));
            XValues[i] = DaysNoList.get(i) + "日";
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float xvalue, AxisBase axis) {
                return XValues[(int) xvalue];
            }

        };

        final LineDataSet lineDataSet = new LineDataSet(values, "数据");
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
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setCircleColorHole(Color.parseColor("#FFFFFF"));
        //lineDataSet.setHighlightEnabled(false);
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
        //lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.animateXY(0, 1200);
        lineChart.setVisibleXRangeMaximum(6);
        lineChart.setViewPortOffsets(50, 50, 50, 50);
        lineChart.moveViewToX(valueDataList.size() - 1);
        lineChart.getDescription().setEnabled(false);


        CustomMarkerView mv = new CustomMarkerView(getContext(),
                R.layout.view_customer_marker);
        lineChart.setMarkerView(mv);
        lineChart.invalidate();
    }

    /*
     * 显示实时下载速度
     * */
    public void showRealTimeNetSpeed(View view) {
        final BytesFormatter bytesFormatter = new BytesFormatter();
        final long[] RXOld = new long[2];
        final Handler  handler = new Handler();
        final TextView textViewRealTimeTxSpeed = view.findViewById(R.id.TextViewRealTimeTxSpeed);
        final TextView textViewRealTimeRxSpeed = view.findViewById(R.id.TextViewRealTimeRxSpeed);
        Runnable runnable = new Runnable() {
            int firstTimeShow = 0;
            @Override
            public void run() {
                if (firstTimeShow != 0) {
                    long overTxTraffic = TrafficStats.getTotalTxBytes();
                    long overRxTraffic = TrafficStats.getTotalRxBytes();
                    long currentTxDataRate = overTxTraffic - RXOld[0];
                    long currentRxDataRate = overRxTraffic - RXOld[1];
                    OutputTrafficData dataRealTimeTxSpeed = bytesFormatter.getPrintSizeByModel(currentTxDataRate);
                    OutputTrafficData dataRealTimeRxSpeed = bytesFormatter.getPrintSizeByModel(currentRxDataRate);
                    textViewRealTimeTxSpeed.setText(dataRealTimeTxSpeed.getValueWithNoDecimalPoint() + dataRealTimeTxSpeed.getType() + "/s");
                    textViewRealTimeRxSpeed.setText(dataRealTimeRxSpeed.getValueWithNoDecimalPoint() + dataRealTimeRxSpeed.getType() + "/s");
                    RXOld[0] = overTxTraffic;
                    RXOld[1] = overRxTraffic;
                } else {
                    long overTxTraffic = TrafficStats.getTotalTxBytes();
                    long overRxTraffic = TrafficStats.getTotalRxBytes();
                    RXOld[0] = overTxTraffic;
                    RXOld[1] = overRxTraffic;
                    firstTimeShow = 1;
                }
                //System.out.println("Run");
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void showAppTrafficDataWarning(Context context, View view, String subscriberID) {

        long globalAppsMAXTraffic = -1L;
        SharedPreferences sp = context.getSharedPreferences("TrafficManager", MODE_PRIVATE);
        globalAppsMAXTraffic = sp.getLong("AppsMAXTraffic", -1L);
        //System.out.println("每日应用流量限额："+appsMAXTraffic+"MB");
        TextView TextViewAppTrafficDataWarning = view.findViewById(R.id.TextViewAppTrafficDataWarning);
        String textViewString = "你还没设置APP每日使用限额";
        if (globalAppsMAXTraffic != -1) {

            List<String> APP_NAME_LIST = new ArrayList<>(),APP_USAGE_LIST = new ArrayList<>();
            List<String> APP_INFO_LIST = new ArrayList<>();
            //textViewString = "";
            final BytesFormatter bytesFormatter = new BytesFormatter();
            BucketDao bucketDao = new BucketDaoImpl();
            List<AppsInfo> installedAppsTodayTrafficDataList = bucketDao.getAllInstalledAppsTrafficData(context, subscriberID, networkType, new DateTools().getTimesTodayMorning(), new DateTools().getTimesTodayEnd());
            int flag = 0;
            System.out.println("APP每日使用限额列表："+installedAppsTodayTrafficDataList.size());
            for (int k = 0; k < installedAppsTodayTrafficDataList.size(); k++) {
                AppsInfo i = installedAppsTodayTrafficDataList.get(k);
                String name = i.getName();
                long allBytes = i.getTrans().getTotal();
                //System.out.println("App使用："+name+":"+allBytes);
                //RecyclerViewAppTrafficDataWarning
                AppBaseInfoDao appBaseInfoDao = new AppBaseInfoDao(view.getContext());
                Tb_AppBaseInfo appBaseInfo = appBaseInfoDao.find(i.getUid());
                if (appBaseInfo==null){
                    appBaseInfo = new Tb_AppBaseInfo(i.getUid(),i.getPackageName());
                    appBaseInfoDao.add(appBaseInfo);
                }
                AppPreferenceDao appPreferenceDao = new AppPreferenceDao(view.getContext());
                Tb_AppPreference tempTbAppPreference = appPreferenceDao.find(i.getUid());
                Tb_AppPreference tbAppPreference;
                if (tempTbAppPreference ==null){
                    tbAppPreference = new Tb_AppPreference(appBaseInfo,0,0,-1L);
                    appPreferenceDao.add(tbAppPreference);
                }else{
                    tbAppPreference = tempTbAppPreference;
                }
                if (tbAppPreference.getWarningLimit()>-1L) {
                    if (allBytes >= tbAppPreference.getWarningLimit()) {
                        /*if (flag != 0) {
                            textViewString += "\n";
                        }*/
                        flag++;
                        OutputTrafficData dataAppTodayUsageOutOfLimit = bytesFormatter.getPrintSizeByModel(allBytes);
                        //textViewString += name + " 使用了" + dataAppTodayUsageOutOfLimit.getValueWithTwoDecimalPoint() + dataAppTodayUsageOutOfLimit.getType() + " 超过了APP设置阀值";
                        APP_NAME_LIST.add(name);
                        APP_USAGE_LIST.add(dataAppTodayUsageOutOfLimit.getValueWithTwoDecimalPoint() + dataAppTodayUsageOutOfLimit.getType());
                        APP_INFO_LIST.add("超过了APP设置阀值");
                        continue;
                    }else {
                        continue;
                    }
                }
                if (allBytes >= (globalAppsMAXTraffic)) {
                    /*if (flag != 0) {
                        textViewString += "\n";
                    }*/
                    flag++;
                    OutputTrafficData dataAppTodayUsageOutOfLimit = bytesFormatter.getPrintSizeByModel(allBytes);
                    //textViewString += name + " 使用了" + dataAppTodayUsageOutOfLimit.getValueWithTwoDecimalPoint() + dataAppTodayUsageOutOfLimit.getType() + " 超过了全局设置阀值";
                    APP_NAME_LIST.add(name);
                    APP_USAGE_LIST.add(dataAppTodayUsageOutOfLimit.getValueWithTwoDecimalPoint() + dataAppTodayUsageOutOfLimit.getType());
                    APP_INFO_LIST.add("超过了全局设置阀值");
                }
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            RecyclerView mRecyclerView = view.findViewById(R.id.RecyclerViewAppTrafficDataWarning);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setAdapter(new RecyclerViewAppsWarningAdapter(context,APP_NAME_LIST,APP_USAGE_LIST,APP_INFO_LIST));
            //mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            mRecyclerView.setVisibility(View.VISIBLE);
            TextViewAppTrafficDataWarning.setVisibility(View.VISIBLE);
            if (flag == 0) {
                mRecyclerView.setVisibility(View.GONE);
                textViewString = "今日没有程序流量超过阀值";
                TextViewAppTrafficDataWarning.setText(textViewString);
            }else {
            TextViewAppTrafficDataWarning.setVisibility(View.GONE);
                NotificationTools.setNotification(context,"APP使用警告","有"+flag+"个APP超过了本日使用限额",false,1001);


            }
        } else {
            TextViewAppTrafficDataWarning.setText(textViewString);
        }
    }

    private long statisticAppIgnoreTrafficData(Context context, String subscriberID, long startTime) {
        List<Tb_AppPreference> tbAppPreferenceList = new AppPreferenceDao(context).find();
        long ignoreAmount = 0L;
        if (tbAppPreferenceList != null) {
            BucketDao bucketDao = new BucketDaoImpl();
            for (int i = 0; i < tbAppPreferenceList.size(); i++) {
                Tb_AppPreference tempTbAppPreference = tbAppPreferenceList.get(i);
                switch (ACTIVE_SIM_PAGE_NO) {
                    case 1: {
                        if (tempTbAppPreference.getSim1IgnoreFlag() == 1) {
                            ignoreAmount += bucketDao.getAppTrafficData(context, subscriberID, networkType, startTime, System.currentTimeMillis(), Integer.valueOf(tempTbAppPreference.getUid())).getTotal();
                        }
                    }
                    break;
                    case 2: {
                        if (tempTbAppPreference.getSim2IgnoreFlag() == 1) {
                            ignoreAmount += bucketDao.getAppTrafficData(context, subscriberID, networkType, startTime, System.currentTimeMillis(), Integer.valueOf(tempTbAppPreference.getUid())).getTotal();
                        }
                    }
                    break;
                }
            }
            return ignoreAmount;
        } else {
            return ignoreAmount;
        }
    }

    private void handleToolBarItem(final Context context, final List<SimInfo> simInfoList) {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_refresh: {
                            refresh();
                    }
                    break;
                    case R.id.action_regulate: {
                        final SmsReceiver receiver=new SmsReceiver();
                        SmsReceiver.Handle handle = new SmsReceiver.Handle() {
                            @Override
                            public void handle(long s) {
                                final long realAmount = s;
                                System.out.println("返回" + realAmount);
                                OutputTrafficData data = new BytesFormatter().getPrintSizeByModel(realAmount);

                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("校正Sim"+(ACTIVE_SIM_PAGE_NO)+" "+simInfoList.get(ACTIVE_SIM_PAGE_NO-1).getSubscriptionInfo().getCarrierName()+"流量");
                                builder.setMessage("校正已使用流量为"+ data.getValueWithTwoDecimalPoint()+data.getType() );

                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String subscriberId = simInfoList.get(ACTIVE_SIM_PAGE_NO-1).getSubscriberId();
                                        DataTrafficRegulateDao dataTrafficRegulateDao = new DataTrafficRegulateDao(context);
                                        Tb_DataTrafficRegulate tb_dataTrafficRegulate = dataTrafficRegulateDao.find(subscriberId);
                                        System.out.println(tb_dataTrafficRegulate);
                                        if (tb_dataTrafficRegulate==null){
                                            tb_dataTrafficRegulate = new Tb_DataTrafficRegulate(subscriberId,0,System.currentTimeMillis());
                                            dataTrafficRegulateDao.add(tb_dataTrafficRegulate);
                                    }

                                        BucketDao bucketDao = new BucketDaoImpl();
                                        long systemUse = bucketDao.getTrafficDataFromStartDayToToday(context,subscriberId,dataPlanStartDay,networkType).getTotal();
                                        tb_dataTrafficRegulate.setValue(systemUse-realAmount);
                                        tb_dataTrafficRegulate.setSettime(System.currentTimeMillis());
                                        dataTrafficRegulateDao.update(tb_dataTrafficRegulate);
                                        Toast.makeText(context, "确定校正", Toast.LENGTH_SHORT).show();
                                        context.unregisterReceiver(receiver);
                                        refresh();
                                    }
                                });
                                builder.setNegativeButton("取消",null);
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }

                        };

                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
                        context.registerReceiver(receiver,intentFilter);
                        receiver.setHandle(handle);

                        final String[] items = { "自动校正","手动校正" };
                        AlertDialog.Builder listDialog =  new AlertDialog.Builder(context);
                        listDialog.setTitle("流量校正");
                        listDialog.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0: {
                                        Toast.makeText(context,"自动校正中",Toast.LENGTH_LONG).show();
                                        SMSTools smsTools = new SMSTools(context);
                                        String phoneNumber="",content="";
                                        String carrierName=simInfoList.get(ACTIVE_SIM_PAGE_NO-1).getSubscriptionInfo().getCarrierName().toString();
                                        int subscriptionId=simInfoList.get(ACTIVE_SIM_PAGE_NO-1).getSubscriptionInfo().getSubscriptionId();
                                        switch (carrierName)
                                        {
                                            case "中国电信" : {
                                                phoneNumber = getString(R.string.china_tel_SMS_number);
                                                content = getString(R.string.china_tel_SMS_text);
                                            }break;
                                            case "中国移动" : {
                                                phoneNumber = getString(R.string.china_mobile_SMS_number);
                                                content = getString(R.string.china_mobile_SMS_text);
                                            }break;
                                            case "中国联通" : {
                                                phoneNumber = getString(R.string.china_uni_SMS_number);
                                                content = getString(R.string.china_uni_SMS_text);
                                            }break;
                                        }
                                        if ((!phoneNumber.equals(""))&&(!content.equals(""))) {
                                            smsTools.sendSMS(phoneNumber, content, subscriptionId);

                                        }else {
                                            Snackbar.make(getView(),"短信发送不成功",Snackbar.LENGTH_LONG).show();
                                        }
                                    } break;
                                    case 1: {
                                        final View viewCustomerDialogDataInput = LayoutInflater.from(context).inflate(R.layout.view_customer_dialog_data_input,null);
                                        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                        alertDialog.setView(viewCustomerDialogDataInput);
                                        final EditText editText = viewCustomerDialogDataInput.findViewById(R.id.EditText_Traffic_Data_Value);
                                        //final Spinner spinnerRegulateType = viewCustomerDialogDataInput.findViewById(R.id.Spinner_Traffic_Data_RegulateType);
                                        final Spinner spinnerDataType = viewCustomerDialogDataInput.findViewById(R.id.Spinner_Traffic_Data_Type);
                                        spinnerDataType.setSelection(3);

                                        Button btnCancel = viewCustomerDialogDataInput.findViewById(R.id.ButtonCustomDialogCancel);
                                        btnCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.dismiss();
                                            }
                                        });
                                        Button btnConfirm= viewCustomerDialogDataInput.findViewById(R.id.ButtonCustomDialogConfirm);
                                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String subscriberId = simInfoList.get(ACTIVE_SIM_PAGE_NO-1).getSubscriberId();
                                                DataTrafficRegulateDao dataTrafficRegulateDao = new DataTrafficRegulateDao(context);
                                                Tb_DataTrafficRegulate tb_dataTrafficRegulate = dataTrafficRegulateDao.find(subscriberId);
                                                System.out.println(tb_dataTrafficRegulate);
                                                if (tb_dataTrafficRegulate==null){
                                                    tb_dataTrafficRegulate = new Tb_DataTrafficRegulate(subscriberId,0,System.currentTimeMillis());
                                                    dataTrafficRegulateDao.add(tb_dataTrafficRegulate);
                                                }

                                                BucketDao bucketDao = new BucketDaoImpl();
                                                BytesFormatter bytesFormatter = new BytesFormatter();
                                                long inputUse =  bytesFormatter.convertValueToLong( Double.valueOf(editText.getText().toString()),spinnerDataType.getSelectedItem().toString());
                                                long systemUse = bucketDao.getTrafficDataFromStartDayToToday(context,subscriberId,dataPlanStartDay,networkType).getTotal();
                                                tb_dataTrafficRegulate.setValue(systemUse-inputUse);
                                                tb_dataTrafficRegulate.setSettime(System.currentTimeMillis());
                                                dataTrafficRegulateDao.update(tb_dataTrafficRegulate);
                                                Toast.makeText(context,"数据：已使用"
                                                        +editText.getText()+""+spinnerDataType.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
                                                alertDialog.dismiss();
                                                refresh();
                                            }
                                        });
                                        String title;
                                        if (ACTIVE_SIM_PAGE_NO == 1) {
                                            title = "SIM1流量校正";
                                            alertDialog.setTitle(title);
                                        } else {
                                            title = "SIM2流量校正";
                                            alertDialog.setTitle(title);
                                        }
                                        alertDialog.show();
                                        editText.setFocusableInTouchMode(true);
                                        editText.setFocusable(true);
                                        editText.requestFocus();
                                        Toast.makeText(context, title, Toast.LENGTH_LONG).show();
                                    } break;
                                }
                            }
                        });
                        listDialog.show();

                    }
                    break;
                }
                return false;
            }
        });

    }
    private void refresh(){
        buttonRefresh.performClick();
    }
}
