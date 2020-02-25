package edu.yezh.datatrafficmanager.tools.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import edu.yezh.datatrafficmanager.model.SimInfo;
import edu.yezh.datatrafficmanager.model.Sms;
import edu.yezh.datatrafficmanager.tools.SimTools;

public class SMSTools {
    Context context;

    public SMSTools(Context context) {
        this.context = context;
    }

    /*public void sendSMSS(String content, String phone) {

        content = content.trim();
        phone = phone.trim();
        if (!(content=="") && !(phone=="")) {
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> strings = manager.divideMessage(content);
            for (int i = 0; i < strings.size(); i++) {
                manager.sendTextMessage(phone, null, content, null, null);
            }
            Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "手机号或内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
    }*/

    public void sendSMS(String phoneNumber, String message, int SIM_NUM) {

        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent,
                0);
        // register the Broadcast Receivers
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK: {
                        String msg = "短信发送成功";
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        System.out.println(msg);
                    }
                    break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE: {
                        String msg = "普通错误";
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        System.out.println(msg);
                    }
                    break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF: {
                        String msg = "无线广播被明确地关闭";
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        System.out.println(msg);
                    }
                    break;
                    case SmsManager.RESULT_ERROR_NULL_PDU: {
                        String msg = "没有提供pdu";
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        System.out.println(msg);
                    }
                    break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE: {
                        String msg = "服务当前不可用";
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        System.out.println(msg);
                    }
                    break;

                }
            }
        }, new IntentFilter(SENT_SMS_ACTION));

        //处理返回的接收状态
        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0,
                deliverIntent, 0);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                Toast.makeText(context,
                        "收信人已经成功接收", Toast.LENGTH_SHORT)
                        .show();
                System.out.println("收信人已经成功接收");
            }
        }, new IntentFilter(DELIVERED_SMS_ACTION));

        SIM_NUM = SIM_NUM - 1;
        SimTools simTools = new SimTools();
        List<SimInfo> simInfoList = simTools.getSubscriptionInfoList(context);

        //获取短信管理器   
        //android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getSmsManagerForSubscriptionId(simInfoList.get(SIM_NUM).getSubscriptionInfo().getSubscriptionId());
        //拆分短信内容（手机短信长度限制）    
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliverPI);
        }
    }
    //public List<Sms> getAllSms() {
    public Sms getAllSms(String simName) {
        String readAddress = getReadAddressBySimName(simName);
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/inbox");
        Activity activity = (Activity) context;
        ContentResolver cr = activity.getContentResolver();
        Cursor c = cr.query(message, null, "address = ?", new String[]{readAddress}, "date desc");
       // Cursor c = cr.query(message, null, null, null, "date desc");
        activity.startManagingCursor(c);
        int totalSMS = c.getCount();
        //totalSMS =1 ;
        if (c.moveToFirst()) {
           // for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                lstSms.add(objSms);
                c.moveToNext();
            //}
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();
    return  objSms;
        //return lstSms;
    }

    private String getReadAddressBySimName(String simName){
        String address;
        switch (simName){
            case "中国移动" : address = "10086";break;
            case "中国电信" : address = "10001";break;
            case "中国联通" : address = "10010";break;
            default: address="";break;
        }
        return address;
    }
}

