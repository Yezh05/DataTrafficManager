package edu.yezh.datatrafficmanager;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yezh.datatrafficmanager.adapter.RecyclerViewAppsTrafficDataAdapter;
import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.dao.db.AppPreferenceDao;
import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.SimInfo;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.model.tb.AppPreference;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;
import edu.yezh.datatrafficmanager.tools.NotificationTools;
import edu.yezh.datatrafficmanager.tools.SimTools;
import edu.yezh.datatrafficmanager.tools.chartTools.CustomMarkerView;
import edu.yezh.datatrafficmanager.tools.chartTools.MyLineValueFormatter;

import static android.content.Context.MODE_PRIVATE;

public class MyFragment1 extends Fragment {
    final int networkType = ConnectivityManager.TYPE_MOBILE;
    int ACTIVE_SIM_PAGE_NO;
    private Handler handler;

    public MyFragment1() {
    }

    //public String subscriberID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.t1, container, false);
        final View mainview = inflater.inflate(R.layout.activity_main, container, false);
        Log.e("标签页", "移动网络页面");

        final Context context = this.getContext();

        SimTools simTools = new SimTools();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //  Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{"android.permission.READ_PHONE_STATE"}, 1);
        }

        final List<SimInfo> simInfoList = simTools.getSubscriptionInfoList(context);

        showRealTimeNetSpeed(view);

        final Button button_SIM1 = view.findViewById(R.id.Button_SIM1);
        final Button button_SIM2 = view.findViewById(R.id.Button_SIM2);
        button_SIM1.setText("SIM卡1:" + simInfoList.get(0).getSubscriptionInfo().getCarrierName());
        button_SIM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_SIM1.setTextColor(Color.parseColor("#009688"));
                button_SIM2.setTextColor(Color.BLACK);
                ACTIVE_SIM_PAGE_NO = 1;
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
                    setTrafficDataView(view, simInfoList.get(1).getSubscriberId());
                }
            });
        } else {
            button_SIM2.setVisibility(View.GONE);
        }
        button_SIM1.performClick();
        handleToolBarItem(context, button_SIM1, button_SIM2, simInfoList);

        return view;
    }

    /*获取并显示流量使用情况*/
    public void setTrafficDataView(View view, final String subscriberID) {
        try {
            SharedPreferences sp = getActivity().getSharedPreferences("TrafficManager", MODE_PRIVATE);
            final Context context = this.getContext();
            BucketDao bucketDao = new BucketDaoImpl();
            BytesFormatter bytesFormatter = new BytesFormatter();
            DateTools dateTools = new DateTools();

            float dataPlan = showTextViewDataPlan(view, subscriberID);

            long ThisMonthData = bucketDao.getTrafficDataOfThisMonth(context, subscriberID, networkType).getTotal();


            OutputTrafficData readableThisMonthData = bytesFormatter.getPrintSizeByModel(ThisMonthData);
            TextView TextViewData4GThisMonth = view.findViewById(R.id.TextViewData4GThisMonth);
            TextViewData4GThisMonth.setText(Math.round(Double.valueOf(readableThisMonthData.getValue()) * 100D) / 100D + readableThisMonthData.getType());

            final int dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID, 1);


            long ignoreTrafficDataThisMonth = statisticAppIgnoreTrafficData(context, subscriberID, dateTools.getTimesStartDayMorning(dataPlanStartDay));
            long ignoreTrafficDataToday = statisticAppIgnoreTrafficData(context, subscriberID, dateTools.getTimesTodayMorning());
            System.out.println("本月忽略应用流量：" + ignoreTrafficDataThisMonth);
            System.out.println("本日忽略应用流量：" + ignoreTrafficDataToday);

            long rxBytesStartDayToToday = bucketDao.getTrafficDataFromStartDayToToday(context, subscriberID, dataPlanStartDay, networkType).getTotal();
            OutputTrafficData readableDataStartDayToToday = bytesFormatter.getPrintSizeByModel(rxBytesStartDayToToday - ignoreTrafficDataThisMonth);
            TextView TextViewData4GStartDayToToday = view.findViewById(R.id.TextViewData4GStartDayToToday);
            TextViewData4GStartDayToToday.setText(Math.round(Double.valueOf(readableDataStartDayToToday.getValue()) * 100D) / 100D + readableDataStartDayToToday.getType());

            float DataUseStatus = (rxBytesStartDayToToday / (dataPlan * 1024 * 1024 * 1024)) * 100;
            int PercentDataUseStatus = Math.round(DataUseStatus);
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
            TextView TextViewDataUseStatus = (TextView) view.findViewById(R.id.TextViewDataUseStatus);
            TextViewDataUseStatus.setText(String.valueOf(PercentDataUseStatus) + "%\n" + TextDataUseStatus);

            List<TransInfo> lastThirtyDaysTrafficData = bucketDao.getTrafficDataOfLastThirtyDays(context, subscriberID, networkType);
            OutputTrafficData todayUsage = bytesFormatter.getPrintSizeByModel(lastThirtyDaysTrafficData.get(0).getTotal() - ignoreTrafficDataToday);

            TextView TextViewData4GToday = view.findViewById(R.id.TextViewData4GToday);
            TextViewData4GToday.setText(Math.round(Double.valueOf(todayUsage.getValue()) * 100D) / 100D + todayUsage.getType());

            OutputTrafficData restTrafficDataAmount = bytesFormatter.getPrintSizeByModel(Math.round(dataPlan * 1024D * 1024D * 1024D) - rxBytesStartDayToToday);

            NotificationTools.setNotification(context,
                    "今日 " + Math.round(Double.valueOf(todayUsage.getValue())) + todayUsage.getType() + "   "
                            + "剩余 " + Math.round(Double.valueOf(restTrafficDataAmount.getValue())) + restTrafficDataAmount.getType() + "   "
                            + "总量 " + Math.round(dataPlan) + "GB"
                    , TextDataUseStatus);


            List<Long> DaysNoList = dateTools.getLastThirtyDaysMap().get("No");
            Collections.reverse(lastThirtyDaysTrafficData);
            Collections.reverse(DaysNoList);
            showChart(view, PercentDataUseStatus, lastThirtyDaysTrafficData, DaysNoList);

            RecyclerViewAppsTrafficDataAdapter recyclerViewAppsTrafficDataAdapter = new RecyclerViewAppsTrafficDataAdapter(bucketDao.getAllInstalledAppsTrafficData(context, subscriberID, networkType, dateTools.getTimesStartDayMorning(dataPlanStartDay), System.currentTimeMillis()), context, subscriberID, networkType);
            RecyclerView RecyclerViewAppsTrafficData = view.findViewById(R.id.RecyclerViewAppsTrafficData);
            RecyclerViewAppsTrafficData.setAdapter(recyclerViewAppsTrafficDataAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            RecyclerViewAppsTrafficData.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            RecyclerViewAppsTrafficData.setLayoutManager(layoutManager);

            showAppTrafficDataWarning(context, view, subscriberID);

            FloatingActionButton fab = view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDataPlanEditDialog(subscriberID);
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

        } catch (Exception e) {
            Log.e("出现错误", "错误：" + e.toString());
            e.printStackTrace();
        }
    }


    /*流量套餐限额设置框*/
    private void openDataPlanEditDialog(final String subscriberID) {
        final Context context = this.getContext();
        final EditText inputDataPlanStartDay = new EditText(context);
        inputDataPlanStartDay.setHint("请输入套餐起始日");
        AlertDialog.Builder builderDataPlanStartDay = new AlertDialog.Builder(context);
        builderDataPlanStartDay.setTitle("设置套餐起始日").setIcon(R.mipmap.edit).setView(inputDataPlanStartDay).setNegativeButton("取消", null);
        builderDataPlanStartDay.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final EditText inputDataPlan = new EditText(context);
                inputDataPlan.setHint("请输入套餐流量额度(GB)");
                AlertDialog.Builder builderDataPlan = new AlertDialog.Builder(context);
                builderDataPlan.setTitle("设置套餐流量额度").setIcon(R.mipmap.edit).setView(inputDataPlan).setNegativeButton("取消", null);
                builderDataPlan.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String inputData = inputDataPlan.getText().toString();
                        Toast.makeText(getActivity(), inputData, Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = context.getSharedPreferences("TrafficManager", Context.MODE_PRIVATE).edit();
                        //Log.w("设置流量套餐信息", "dataPlan_" + subscriberID + " : " + Float.valueOf(inputData).toString());
                        editor.putFloat("dataPlan_" + subscriberID, Float.valueOf(inputData));
                        editor.putInt("dataPlanStartDay_" + subscriberID, Integer.valueOf(inputDataPlanStartDay.getText().toString()));
                        editor.apply();
                        showTextViewDataPlan(getView(), subscriberID);
                    }
                });
                builderDataPlan.show();
            }
        });
        builderDataPlanStartDay.show();
    }

    /*获取并显示流量套餐限额*/
    private Float showTextViewDataPlan(View view, String subscriberID) {
        SharedPreferences sp = getActivity().getSharedPreferences("TrafficManager", MODE_PRIVATE);
        Float dataPlan = sp.getFloat("dataPlan_" + subscriberID, -1);
        int dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID, 1);
        Log.w("流量套餐限额", "dataPlan_" + subscriberID + " : " + dataPlan.toString());
        Log.w("流量套餐起始日", "dataPlanStartDay_" + subscriberID + " : " + dataPlanStartDay);
        TextView TextViewDataPlan = (TextView) view.findViewById(R.id.TextViewDataPlan);
        TextViewDataPlan.setText(dataPlan.toString() + "GB");
        TextView TextViewDataPlanStartDay = view.findViewById(R.id.TextViewDataPlanStartDay);
        TextViewDataPlanStartDay.setText("每月" + dataPlanStartDay + "日");
        return dataPlan;
    }

    private void showChart(View view, int PercentDataUseStatus, List<TransInfo> valueDataList, List<Long> DaysNoList) {
        BytesFormatter bytesFormatter = new BytesFormatter();
        PieChart pieChart = (PieChart) view.findViewById(R.id.Chart1);
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

        final String[] Xvalues = new String[DaysNoList.size()];
        for (int i = 0; i < DaysNoList.size(); i++) {
            values.add(new Entry(i, valueDataList.get(i).getTotal()));
            Xvalues[i] = DaysNoList.get(i) + "日";
        }

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float xvalue, AxisBase axis) {
                return Xvalues[(int) xvalue];
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
        lineDataSet.setDrawCircleHole(false);
        //lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawHighlightIndicators(false);
        //lineDataSet.set

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
                R.layout.customer_marker_view);
        lineChart.setMarkerView(mv);

        lineChart.invalidate();
    }

    /*
     * 显示实时下载速度
     * */
    public void showRealTimeNetSpeed(View view) {
        final BytesFormatter bytesFormatter = new BytesFormatter();
        final long[] RXOld = new long[2];
        handler = new Handler();
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
                    textViewRealTimeTxSpeed.setText(Math.round(Double.valueOf(dataRealTimeTxSpeed.getValue())) + dataRealTimeTxSpeed.getType() + "/s");
                    textViewRealTimeRxSpeed.setText(Math.round(Double.valueOf(dataRealTimeRxSpeed.getValue())) + dataRealTimeRxSpeed.getType() + "/s");
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
        int appsMAXTraffic = -1;
        SharedPreferences sp = context.getSharedPreferences("TrafficManager", MODE_PRIVATE);
        appsMAXTraffic = sp.getInt("AppsMAXTraffic", -1);
        //System.out.println("每日应用流量限额："+appsMAXTraffic+"MB");
        TextView TextViewAppTrafficDataWarning = view.findViewById(R.id.TextViewAppTrafficDataWarning);
        String textViewString = "你还没设置APP每日使用限额";
        if (appsMAXTraffic != -1) {
            textViewString = "";
            final BytesFormatter bytesFormatter = new BytesFormatter();
            BucketDao bucketDao = new BucketDaoImpl();
            List<AppsInfo> installedAppsTodayTrafficDataList = bucketDao.getAllInstalledAppsTrafficData(context, subscriberID, networkType, new DateTools().getTimesTodayMorning(), System.currentTimeMillis());
            int flag = 0;
            for (int k = 0; k < installedAppsTodayTrafficDataList.size(); k++) {
                AppsInfo i = installedAppsTodayTrafficDataList.get(k);
                String name = i.getName();
                long rxBytes = i.getTrans().getRx();
                long txBytes = i.getTrans().getTx();
                long allBytes = rxBytes + txBytes;
                if (allBytes >= (appsMAXTraffic * 1024 * 1024)) {
                    if (flag != 0) {
                        textViewString += "\n";
                    }
                    flag++;
                    OutputTrafficData dataAppTodayUseageOutofLimit = bytesFormatter.getPrintSizeByModel(allBytes);
                    textViewString += name + " 使用了" + Math.round(Double.valueOf(dataAppTodayUseageOutofLimit.getValue()) * 100D) / 100D + dataAppTodayUseageOutofLimit.getType() + " 超过了设置阀值";
                }
            }
            if (flag == 0) {
                textViewString = "今日没有程序流量超过阀值";
            }
            TextViewAppTrafficDataWarning.setText(textViewString);
        } else {
            TextViewAppTrafficDataWarning.setText(textViewString);
        }
    }

    private long statisticAppIgnoreTrafficData(Context context, String subscriberID, long startTime) {
        List<AppPreference> appPreferenceList = new AppPreferenceDao(context).find();
        long ignoreAmount = 0L;
        if (appPreferenceList != null) {
            BucketDao bucketDao = new BucketDaoImpl();
            for (int i = 0; i < appPreferenceList.size(); i++) {
                AppPreference tempAppPreference = appPreferenceList.get(i);
                switch (ACTIVE_SIM_PAGE_NO) {
                    case 1: {
                        if (tempAppPreference.getSim1IgnoreFlag() == 1) {
                            ignoreAmount += bucketDao.getAppTrafficData(context, subscriberID, networkType, startTime, System.currentTimeMillis(), Integer.valueOf(tempAppPreference.getUid())).getTotal();
                        }
                    }
                    break;
                    case 2: {
                        if (tempAppPreference.getSim2IgnoreFlag() == 1) {
                            ignoreAmount += bucketDao.getAppTrafficData(context, subscriberID, networkType, startTime, System.currentTimeMillis(), Integer.valueOf(tempAppPreference.getUid())).getTotal();
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

    private void handleToolBarItem(final Context context, final Button button_SIM1, final Button button_SIM2, List<SimInfo> simInfoList) {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_refresh: {
                        if (ACTIVE_SIM_PAGE_NO == 1) {
                            button_SIM1.performClick();
                        } else {
                            button_SIM2.performClick();
                        }
                    }
                    break;
                    case R.id.action_regulate: {
                        final View viewCustomerDialogDataInput = LayoutInflater.from(context).inflate(R.layout.customer_dialog_data_input_view,null);
                        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setView(viewCustomerDialogDataInput);
                        final EditText editText = viewCustomerDialogDataInput.findViewById(R.id.EditText_Traffic_Data_Value);
                        final Spinner spinnerRegulateType = viewCustomerDialogDataInput.findViewById(R.id.Spinner_Traffic_Data_RegulateType);
                        final Spinner spinnerDataType = viewCustomerDialogDataInput.findViewById(R.id.Spinner_Traffic_Data_Type);
                        spinnerDataType.setSelection(3);
                        editText.setFocusable(true);
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
                                Toast.makeText(context,"数据："+spinnerRegulateType.getSelectedItem().toString()+editText.getText()+""+spinnerDataType.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();

                            }
                        });
                        if (ACTIVE_SIM_PAGE_NO == 1) {
                            String title = "SIM1流量校正";

                            alertDialog.setTitle(title);
                            alertDialog.show();


                            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
                            System.out.println(title);
                        } else {
                            String title = "SIM2流量校正";
                            alertDialog.setTitle(title);
                            alertDialog.show();


                            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
                            System.out.println(title);
                        }
                    }
                    break;
                }
                return false;
            }
        });
    }
}
