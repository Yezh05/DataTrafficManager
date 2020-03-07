package edu.yezh.datatrafficmanager.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.Date;
import java.util.List;

import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.SimInfo;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.model.tb.Tb_AppTransRecord;

import static android.content.Context.MODE_PRIVATE;

public class PoiTools {
    public static HSSFWorkbook getHSSFWorkbook(HSSFWorkbook wb, Context context){
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }
        wb=initialMobileDataWorkbook( wb,  context);
        wb = initialWlanDataWorkbook(wb,context);
        return wb;
    }

    public static HSSFWorkbook initialAppMonitorbook(HSSFWorkbook wb, Context context,String AppName,long startTime,long endTime,int NETWORK_TYPE,long ToTal,List<Tb_AppTransRecord> transRecordList) {
        if (wb == null) {
            wb = new HSSFWorkbook();
        }
        int rowLine = 0;
        String sheetName = "应用使用监控窗数据";
        HSSFSheet sheet =wb.createSheet(sheetName);
        HSSFRow row = sheet.createRow(0);

        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        HSSFCell cell = null;

        cell = row.createCell(0);
        cell.setCellValue(sheetName);
        //cell.setCellStyle(style);
        rowLine++;
        rowLine++;
        row = sheet.createRow(rowLine);
        cell = row.createCell(0);cell.setCellValue("应用名");
        cell = row.createCell(1);cell.setCellValue(AppName);

        rowLine++;
        row = sheet.createRow(rowLine);
        cell = row.createCell(0);cell.setCellValue("开始时间");
        cell = row.createCell(1);cell.setCellValue(DateTools.longToDate(startTime));

        rowLine++;
        row = sheet.createRow(rowLine);
        cell = row.createCell(0);cell.setCellValue("结束时间");
        cell = row.createCell(1);cell.setCellValue(DateTools.longToDate(endTime));

        String NetTypeString;
        if (NETWORK_TYPE== ConnectivityManager.TYPE_WIFI){
            NetTypeString = "无线局域网";
        }else if (NETWORK_TYPE == ConnectivityManager.TYPE_MOBILE){
            NetTypeString = "移动网络";
        }else {
            NetTypeString = "其他网路";
        }

        rowLine++;
        row = sheet.createRow(rowLine);
        cell = row.createCell(0);cell.setCellValue("网络类型");
        cell = row.createCell(1);cell.setCellValue(NetTypeString);

        BytesFormatter bytesFormatter = new BytesFormatter();
        OutputTrafficData totalData = bytesFormatter.getPrintSizeByModel(ToTal);
        rowLine++;
        row = sheet.createRow(rowLine);
        cell = row.createCell(0);cell.setCellValue("流量使用总量");
        cell = row.createCell(1);cell.setCellValue(totalData.getValueWithTwoDecimalPoint()+totalData.getType());

        rowLine++;
        row = sheet.createRow(rowLine);
        cell = row.createCell(0);cell.setCellValue("时间");
        cell = row.createCell(1);cell.setCellValue("移动数据上传");
        cell = row.createCell(2);cell.setCellValue("移动数据下载");
        cell = row.createCell(1);cell.setCellValue("WIFI上传");
        cell = row.createCell(2);cell.setCellValue("WIFI下载");


        OutputTrafficData mobileRXSpeed,mobileTXSpeed,wifiTXSpeed,wifiRXSpeed;
        for (int i=1;i<transRecordList.size();i++){

            wifiRXSpeed = bytesFormatter.getPrintSizeByModel((transRecordList.get(i).getWifiRX()-transRecordList.get(i-1).getWifiRX()));
            wifiTXSpeed = bytesFormatter.getPrintSizeByModel((transRecordList.get(i).getWifiTX()-transRecordList.get(i-1).getWifiTX()));
            mobileRXSpeed = bytesFormatter.getPrintSizeByModel((transRecordList.get(i).getMobileRX()-transRecordList.get(i-1).getMobileRX()));
            mobileTXSpeed = bytesFormatter.getPrintSizeByModel((transRecordList.get(i).getMobileTX()-transRecordList.get(i-1).getMobileTX()));

            rowLine++;
            row = sheet.createRow(rowLine);
            cell = row.createCell(0);cell.setCellValue(DateTools.longToDate(transRecordList.get(i).getTimeStamp()));
            cell = row.createCell(1);cell.setCellValue(mobileTXSpeed.getValueWithTwoDecimalPoint()+mobileTXSpeed.getType());
            cell = row.createCell(2);cell.setCellValue(mobileRXSpeed.getValueWithTwoDecimalPoint()+mobileRXSpeed.getType());
            cell = row.createCell(1);cell.setCellValue(wifiTXSpeed.getValueWithTwoDecimalPoint()+mobileRXSpeed.getType());
            cell = row.createCell(2);cell.setCellValue(wifiRXSpeed.getValueWithTwoDecimalPoint()+mobileRXSpeed.getType());
        }

    return wb;
    }

    public static HSSFWorkbook initialMobileDataWorkbook(HSSFWorkbook wb, Context context){
        if(wb == null){
            wb = new HSSFWorkbook();
        }

        List<SimInfo> simInfoList = new SimTools().getSubscriptionInfoList(context);
        int simAmount = simInfoList.size();
        String[] titles;
        for (int i=0;i<simAmount;i++) {
            int rowLine=0;
            BucketDao bucketDao = new BucketDaoImpl();
            BytesFormatter bytesFormatter = new BytesFormatter();
            SimInfo oneSimInfo = simInfoList.get(i);

            String sheetName = "SIM卡"+(i+1)+"流量使用统计表";
            HSSFSheet sheet = wb.createSheet(sheetName);

            // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
            HSSFRow row = sheet.createRow(0);

            // 第四步，创建单元格，并设置值表头 设置表头居中
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

            //声明列对象
            HSSFCell cell = null;

            cell = row.createCell(0);
            cell.setCellValue(sheetName);
            //cell.setCellStyle(style);
            rowLine++;

            //row = sheet.createRow(rowLine);
            rowLine++;

            row = sheet.createRow(rowLine);
            titles = new String[]{"SIM序号","名称","IMSI"};
            for (int j = 0;j<titles.length;j++){
                cell = row.createCell(j);
                cell.setCellValue(titles[j]);
                cell.setCellStyle(style);
            }
            rowLine++;

            row = sheet.createRow(rowLine);

                    //将内容按顺序赋给对应的列对象
                    row.createCell(0).setCellValue(oneSimInfo.getSubscriptionInfo().getSubscriptionId());
                    row.createCell(1).setCellValue(String.valueOf(oneSimInfo.getSubscriptionInfo().getCarrierName()));
                    row.createCell(2).setCellValue(oneSimInfo.getSubscriberId());

                rowLine++;

            System.out.println("Now Line:"+rowLine);
            String subscriberID = oneSimInfo.getSubscriberId();
            SharedPreferences sp = context.getSharedPreferences("TrafficManager",MODE_PRIVATE);
            long dataPlanLong = sp.getLong("dataPlan_"+subscriberID,-1);
            int dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID,1);
            int networkType=ConnectivityManager.TYPE_MOBILE;

            TransInfo trafficDataOfThisMonth =   bucketDao.getTrafficDataOfThisMonth(context,subscriberID, networkType);
            rowLine++;
            titles = new String[]{"本月流量已使用(字节)"};
            row = sheet.createRow(rowLine);
            for (int j = 0;j<titles.length;j++){
                cell = row.createCell(j);
                cell.setCellValue(titles[j]);
                //cell.setCellStyle(style);
            }
            rowLine++;
            row = sheet.createRow(rowLine);
            cell = row.createCell(0);
            cell.setCellValue(trafficDataOfThisMonth.getTotal());
            //rowLine++;

            OutputTrafficData dataPlan = bytesFormatter.getPrintSizeByModel(dataPlanLong);

            TransInfo trafficDataFromStartDay =bucketDao.getTrafficDataFromStartDayToToday(context,subscriberID,dataPlanStartDay,networkType);
            rowLine++;
            titles = new String[]{"月结日","套餐限额","从月结日起流量已使用(字节)"};
            row = sheet.createRow(rowLine);
            for (int j = 0;j<titles.length;j++){
                cell = row.createCell(j);
                cell.setCellValue(titles[j]);
                cell.setCellStyle(style);
            }
            rowLine++;
            row = sheet.createRow(rowLine);
            cell = row.createCell(0);
            cell.setCellValue(dataPlanStartDay);
            cell = row.createCell(1);
            cell.setCellValue(dataPlan.getValueWithNoDecimalPoint() + dataPlan.getType());
            cell = row.createCell(2);
            cell.setCellValue(trafficDataFromStartDay.getTotal());
            rowLine++;rowLine++;rowLine++;

            row = sheet.createRow(rowLine);
            cell = row.createCell(0);
            cell.setCellValue("应用程序流量使用情况");
            rowLine++;

            titles = new String[]{"序号","应用程序名","应用程序包名","上传流量","下载流量"};
            row = sheet.createRow(rowLine);
            for (int j = 0;j<titles.length;j++){
                cell = row.createCell(j);
                cell.setCellValue(titles[j]);
                cell.setCellStyle(style);
            }
            rowLine++;
            List<AppsInfo> installedAppsTrafficData = bucketDao.getAllInstalledAppsTrafficData(context,subscriberID,networkType,new DateTools().getTimesStartDayMorning(dataPlanStartDay),System.currentTimeMillis());
            for (int j=0;j<installedAppsTrafficData.size();j++){
                row = sheet.createRow(rowLine);

                cell = row.createCell(0);
                cell.setCellValue(j+1);
                cell = row.createCell(1);
                cell.setCellValue(installedAppsTrafficData.get(j).getName());
                cell = row.createCell(2);
                cell.setCellValue(installedAppsTrafficData.get(j).getPackageName());
                cell = row.createCell(3);
                cell.setCellValue(installedAppsTrafficData.get(j).getTrans().getTx());
                cell = row.createCell(4);
                cell.setCellValue(installedAppsTrafficData.get(j).getTrans().getRx());
                rowLine++;
            }
            rowLine++;
            /*//创建标题
            for (int i = 0; i < title.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(title[i]);
                cell.setCellStyle(style);
            }

            //创建内容
            for (int i = 0; i < values.length; i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < values[i].length; j++) {
                    //将内容按顺序赋给对应的列对象
                    row.createCell(j).setCellValue(values[i][j]);
                }
            }*/
        }
        return wb;
    }

    public static HSSFWorkbook initialWlanDataWorkbook(HSSFWorkbook wb, Context context){
        if(wb == null){
            wb = new HSSFWorkbook();
        }


        String[] titles;


            int rowLine=0;
            BucketDao bucketDao = new BucketDaoImpl();
            BytesFormatter bytesFormatter = new BytesFormatter();


            String sheetName = "WLAN流量使用统计表";
            HSSFSheet sheet = wb.createSheet(sheetName);

            // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
            HSSFRow row = sheet.createRow(0);

            // 第四步，创建单元格，并设置值表头 设置表头居中
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

            //声明列对象
            HSSFCell cell = null;

            cell = row.createCell(0);
            cell.setCellValue(sheetName);
            //cell.setCellStyle(style);
            rowLine++;
            String subscriberID ="";
            int networkType=ConnectivityManager.TYPE_WIFI,dataPlanStartDay = 1 ;
            TransInfo trafficDataOfThisMonth =   bucketDao.getTrafficDataOfThisMonth(context,subscriberID, networkType);
            rowLine++;
            titles = new String[]{"本月流量已使用(字节)"};
            row = sheet.createRow(rowLine);
            for (int j = 0;j<titles.length;j++){
                cell = row.createCell(j);
                cell.setCellValue(titles[j]);
                //cell.setCellStyle(style);
            }
            rowLine++;
            row = sheet.createRow(rowLine);
            cell = row.createCell(0);
            cell.setCellValue(trafficDataOfThisMonth.getTotal());
            //rowLine++;

            rowLine++;rowLine++;rowLine++;

            row = sheet.createRow(rowLine);
            cell = row.createCell(0);
            cell.setCellValue("应用程序流量使用情况");
            rowLine++;

            titles = new String[]{"序号","应用程序名","应用程序包名","上传流量","下载流量"};
            row = sheet.createRow(rowLine);
            for (int j = 0;j<titles.length;j++){
                cell = row.createCell(j);
                cell.setCellValue(titles[j]);
                cell.setCellStyle(style);
            }
            rowLine++;
            List<AppsInfo> installedAppsTrafficData = bucketDao.getAllInstalledAppsTrafficData(context,subscriberID,
                    networkType,new DateTools().getTimesStartDayMorning(dataPlanStartDay),System.currentTimeMillis());
            for (int j=0;j<installedAppsTrafficData.size();j++){
                row = sheet.createRow(rowLine);

                cell = row.createCell(0);
                cell.setCellValue(j+1);
                cell = row.createCell(1);
                cell.setCellValue(installedAppsTrafficData.get(j).getName());
                cell = row.createCell(2);
                cell.setCellValue(installedAppsTrafficData.get(j).getPackageName());
                cell = row.createCell(3);
                cell.setCellValue(installedAppsTrafficData.get(j).getTrans().getTx());
                cell = row.createCell(4);
                cell.setCellValue(installedAppsTrafficData.get(j).getTrans().getRx());
                rowLine++;
            }
            rowLine++;
            /*//创建标题
            for (int i = 0; i < title.length; i++) {
                cell = row.createCell(i);
                cell.setCellValue(title[i]);
                cell.setCellStyle(style);
            }

            //创建内容
            for (int i = 0; i < values.length; i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < values[i].length; j++) {
                    //将内容按顺序赋给对应的列对象
                    row.createCell(j).setCellValue(values[i][j]);
                }
            }*/

        return wb;
    }
}
