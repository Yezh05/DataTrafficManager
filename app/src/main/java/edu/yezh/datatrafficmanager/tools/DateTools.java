package edu.yezh.datatrafficmanager.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateTools {

    public static String longToDate(long lo){
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    public long getTimesTodayMorning(){
        Calendar dayCal = Calendar.getInstance();
        dayCal.set(Calendar.HOUR_OF_DAY, 00);
        dayCal.set(Calendar.MINUTE, 0);
        dayCal.set(Calendar.SECOND, 0);
        dayCal.set(Calendar.MILLISECOND, 0);
        return dayCal.getTimeInMillis();
    }
    public long getTimesTodayEnd(){
        Calendar dayCal = Calendar.getInstance();
        dayCal.set(Calendar.HOUR_OF_DAY, 23);
        dayCal.set(Calendar.MINUTE, 59);
        dayCal.set(Calendar.SECOND, 59);
        dayCal.set(Calendar.MILLISECOND, 999);
        return dayCal.getTimeInMillis();
    }
    public Long getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.MILLISECOND,0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Long time = cal.getTimeInMillis();
        //System.out.println("本月开始时间："+cal.getTime()+"毫秒："+cal.getTimeInMillis());
        return time;
    }

    public  long getTimesStartDayMorning(int startDay){

        Calendar todayCal = Calendar.getInstance();
        Calendar targetCal = Calendar.getInstance();
        Date startDayFullDate = new Date(targetCal.get(Calendar.YEAR)+"/"+(targetCal.get(Calendar.MONTH)+1)+"/"+startDay);
        targetCal.setTime(startDayFullDate);
        int today= todayCal.get(Calendar.DATE);
        if (today<startDay){
            targetCal.add(Calendar.MONTH, -1);
        }
        //System.out.println("计算后时间:"+targetday+"/"+targetMonth+"/"+targetyear+" 毫秒表示:"+targetCal.getTimeInMillis());
        //System.out.println("月结日开始时间："+targetCal.getTime()+"毫秒："+targetCal.getTimeInMillis());
        return targetCal.getTimeInMillis();
    }
    public long getTimeEndOfPlanday(int startDay){
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(getTimesStartDayMorning(startDay));
        startCal.add(Calendar.MONTH,1);
        startCal.add(Calendar.MILLISECOND,-1);
        System.out.println("月结周期结束时间"+startCal.getTime());
        return startCal.getTimeInMillis();
    }
    public Map<String,List<String>> getLastTwelveMonthsMap(int startDay){
        final int size = 12;
        //Integer startDay=1;
        Map<String,List<String>> lastSixMonthsMap = new HashMap<>();
        Calendar dayCal = Calendar.getInstance();
        List<String> lastSixMonthsStartTimeInMillisList = new ArrayList<>();
        List<String> lastSixMonthsEndTimeInMillisList = new ArrayList<>();
        List<String> lastSixMonthsStartMonthAndEndMonth = new ArrayList<>();
        int today= dayCal.get(Calendar.DATE);
        //int today= 19;
        if(today>=startDay){
            dayCal.add(Calendar.MONTH, 1);
        }
        dayCal.set(Calendar.HOUR_OF_DAY, 00);
        dayCal.set(Calendar.MINUTE, 00);
        dayCal.set(Calendar.SECOND, 00);
        dayCal.set(Calendar.MILLISECOND, 000);
        dayCal.set(Calendar.DATE, startDay);
        dayCal.add(Calendar.MONTH, -1);
        for (int i = 0; i < size; i++) {
            //System.out.println(dayCal.getTime());
            lastSixMonthsStartTimeInMillisList.add( String.valueOf(dayCal.getTimeInMillis() ));
            lastSixMonthsStartMonthAndEndMonth.add(dayCal.get(Calendar.YEAR)+"/"+( dayCal.get(Calendar.MONTH)+1+"" ));
            dayCal.add(Calendar.MONTH, -1);
        }
        //System.out.println("--------------------------------");
        dayCal = Calendar.getInstance();
        if(today>=startDay){
            dayCal.add(Calendar.MONTH, 1);
        }
        for (int i = 0; i < size; i++) {
            dayCal.set(Calendar.HOUR_OF_DAY, 00);
            dayCal.set(Calendar.MINUTE, 00);
            dayCal.set(Calendar.SECOND, 00);
            dayCal.set(Calendar.MILLISECOND, 000);
            dayCal.set(Calendar.DATE, startDay);
            dayCal.set(Calendar.MILLISECOND, -1);
            lastSixMonthsEndTimeInMillisList.add(String.valueOf(dayCal.getTimeInMillis()));
            //System.out.println(dayCal.getTime());
            if (startDay!=1) {
                lastSixMonthsStartMonthAndEndMonth.set(i, lastSixMonthsStartMonthAndEndMonth.get(i)+"\n"+dayCal.get(Calendar.YEAR)+"/"+(dayCal.get(Calendar.MONTH)+1)+"");
                dayCal.add(Calendar.MONTH, -1);
            }

        }
        lastSixMonthsMap.put("StartTimeList", lastSixMonthsStartTimeInMillisList);
        lastSixMonthsMap.put("EndTimeList", lastSixMonthsEndTimeInMillisList);
        lastSixMonthsMap.put("MonthString", lastSixMonthsStartMonthAndEndMonth);
        //System.out.println( lastSixMonthsMap.toString());
        return lastSixMonthsMap;
    }
    public Map<String,List<Long>> getLastThirtyDaysMap(){
        List<Long> lastThirtyDaysStartTimeInMillis=new ArrayList<>();
        List<Long> lastThirtyDaysNo = new ArrayList<>();
        Calendar dayCal = Calendar.getInstance();
        dayCal.set(Calendar.HOUR_OF_DAY, 00);
        dayCal.set(Calendar.MINUTE, 0);
        dayCal.set(Calendar.SECOND, 0);
        dayCal.set(Calendar.MILLISECOND, 0);
        lastThirtyDaysStartTimeInMillis.add(dayCal.getTimeInMillis());
        lastThirtyDaysNo.add(new Long(dayCal.get(Calendar.DATE)));
        for (int i = 0; i < 29; i++) {
            dayCal.add(Calendar.DATE, -(1));
            lastThirtyDaysNo.add(new Long(dayCal.get(Calendar.DATE)));
            lastThirtyDaysStartTimeInMillis.add(dayCal.getTimeInMillis());
        }

        List<Long> lastThirtyDaysEndTimeInMillis=new ArrayList<>();
        dayCal = Calendar.getInstance();
        dayCal.set(Calendar.HOUR_OF_DAY, 23);
        dayCal.set(Calendar.MINUTE, 59);
        dayCal.set(Calendar.SECOND, 59);
        dayCal.set(Calendar.MILLISECOND, 999);
        lastThirtyDaysEndTimeInMillis.add(dayCal.getTimeInMillis());
        for (int i = 0; i < 29; i++) {
            dayCal.add(Calendar.DATE, -(1));
            //System.out.println(dayCal.getTime());
            lastThirtyDaysEndTimeInMillis.add(dayCal.getTimeInMillis());
        }
        Map<String,List<Long>> lastThirtyDaysMap = new HashMap<>();
        lastThirtyDaysMap.put("StartTimeList",lastThirtyDaysStartTimeInMillis);
        lastThirtyDaysMap.put("EndTimeList",lastThirtyDaysEndTimeInMillis);
        lastThirtyDaysMap.put("No",lastThirtyDaysNo);
        //System.out.println("lastThirtyDaysMap.size:"+lastThirtyDaysEndTimeInMillis.size());
        return lastThirtyDaysMap;
    }

    public Map<String,List<Long>> getLastTwentyFourHoursPerThreeHourMap(){
        List<Long> lastTwentyFourHoursStartTimeInMillis=new ArrayList<>();
        List<Long> lastTwentyFourHoursNo = new ArrayList<>();
        Calendar dayCal = Calendar.getInstance();
        //dayCal.set(Calendar.HOUR_OF_DAY, 00);
        dayCal.set(Calendar.MINUTE, 0);
        dayCal.set(Calendar.SECOND, 0);
        dayCal.set(Calendar.MILLISECOND, 0);
        //lastTwentyFourHoursStartTimeInMillis.add(dayCal.getTimeInMillis());
        //lastTwentyFourHoursNo.add(new Long(dayCal.get(Calendar.HOUR_OF_DAY)));
        //String NoString

        for (int i = 0; i < 8; i++) {
            dayCal.add(Calendar.HOUR_OF_DAY, -(3));
            lastTwentyFourHoursNo.add(new Long(dayCal.get(Calendar.HOUR_OF_DAY)));
            lastTwentyFourHoursStartTimeInMillis.add(dayCal.getTimeInMillis());
        }

        List<Long> lastTwentyFourHoursEndTimeInMillis=new ArrayList<>();
        dayCal = Calendar.getInstance();
        dayCal.set(Calendar.MINUTE, 0);
        dayCal.set(Calendar.SECOND, 0);
        dayCal.set(Calendar.MILLISECOND, 0);
        //dayCal.set(Calendar.HOUR_OF_DAY, 23);
        /*dayCal.set(Calendar.MINUTE, 59);
        dayCal.set(Calendar.SECOND, 59);
        dayCal.set(Calendar.MILLISECOND, 999);*/
        lastTwentyFourHoursEndTimeInMillis.add(dayCal.getTimeInMillis());
        for (int i = 0; i < 7; i++) {
            dayCal.add(Calendar.HOUR_OF_DAY, -(3));
            //System.out.println(dayCal.getTime());
            lastTwentyFourHoursEndTimeInMillis.add(dayCal.getTimeInMillis());
        }
        Map<String,List<Long>> lastTwentyFourHoursMap = new HashMap<>();
        lastTwentyFourHoursMap.put("StartTimeList",lastTwentyFourHoursStartTimeInMillis);
        lastTwentyFourHoursMap.put("EndTimeList",lastTwentyFourHoursEndTimeInMillis);
        lastTwentyFourHoursMap.put("No",lastTwentyFourHoursNo);
        /*System.out.println("StartTimeList.size"+lastTwentyFourHoursStartTimeInMillis.size());
        System.out.println("EndTimeList.size"+lastTwentyFourHoursEndTimeInMillis.size());
        System.out.println("DaysNo.size"+lastTwentyFourHoursNo.size());*/
        System.out.println(lastTwentyFourHoursMap);
        return lastTwentyFourHoursMap;
    }


}
