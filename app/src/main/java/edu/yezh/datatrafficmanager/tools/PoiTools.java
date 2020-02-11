package edu.yezh.datatrafficmanager.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

import edu.yezh.datatrafficmanager.dao.BucketDao;
import edu.yezh.datatrafficmanager.dao.BucketDaoImpl;
import edu.yezh.datatrafficmanager.model.SimInfo;

import static android.content.Context.MODE_PRIVATE;

public class PoiTools {
    public static HSSFWorkbook getHSSFWorkbook(HSSFWorkbook wb, Context context){
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }
        wb=initialMobileDataWorkbook( wb,  context);

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
            Float dataPlan = sp.getFloat("dataPlan_"+subscriberID,-1);
            int dataPlanStartDay = sp.getInt("dataPlanStartDay_" + subscriberID,1);


            long trafficDataOfThisMonth =   bucketDao.getTrafficDataOfThisMonth(context,subscriberID, ConnectivityManager.TYPE_MOBILE);
            rowLine++;
            titles = new String[]{"本月已使用(字节)"};
            row = sheet.createRow(rowLine);
            for (int j = 0;j<titles.length;j++){
                cell = row.createCell(j);
                cell.setCellValue(titles[j]);
                cell.setCellStyle(style);
            }
            rowLine++;
            row = sheet.createRow(rowLine);
            cell = row.createCell(0);
            cell.setCellValue(trafficDataOfThisMonth);
            rowLine++;

            long trafficDataFromStartDay =bucketDao.getTrafficDataFromStartDay(context,subscriberID,dataPlanStartDay,ConnectivityManager.TYPE_MOBILE);
            rowLine++;
            titles = new String[]{"月结日","套餐限额(GB)","从月结日起已使用(字节)"};
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
            cell.setCellValue(dataPlan);
            cell = row.createCell(2);
            cell.setCellValue(trafficDataFromStartDay);
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


}
