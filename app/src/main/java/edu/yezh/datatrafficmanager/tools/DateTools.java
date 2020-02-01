package edu.yezh.datatrafficmanager.tools;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class DateTools {



    public Long getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Log.d("thismounthtime",cal.getTime().toString());
        Long time = cal.getTimeInMillis();
        return time;
    }

    public  long getTimesStartDayMorning(int startDay){

        Calendar todayCal = Calendar.getInstance();

        Calendar targetCal = Calendar.getInstance();
        //long a = Long.parseLong("1577808000000");
        Date startDayFullDate = new Date(targetCal.get(Calendar.YEAR)+"/"+(targetCal.get(Calendar.MONTH)+1)+"/"+19);
        targetCal.setTime(startDayFullDate);
        System.out.println(targetCal.getTime().toString());
        System.out.println(targetCal.getTimeInMillis());

        int today= todayCal.get(Calendar.DATE);
        int thisMonth= todayCal.get(Calendar.MONTH)+1;
        int thisyear = todayCal.get(Calendar.YEAR);
        System.out.println("today:"+today);
        System.out.println("thisMonth:"+thisMonth);
        System.out.println("thisYear:"+thisyear);

        if (today<startDay){
            targetCal.add(Calendar.MONTH, -1);
            System.out.println("需要前推1个月");
        }

        int  targetday= targetCal.get(Calendar.DATE);
        int  targetMonth= targetCal.get(Calendar.MONTH)+1;
        int targetyear = targetCal.get(Calendar.YEAR);
        System.out.println("targetday:"+targetday);
        System.out.println("targetMonth:"+targetMonth);
        System.out.println("targetyear:"+targetyear);
        System.out.println(targetCal.getTimeInMillis());
        return targetCal.getTimeInMillis();
    }
}
