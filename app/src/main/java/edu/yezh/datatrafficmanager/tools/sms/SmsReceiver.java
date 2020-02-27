package edu.yezh.datatrafficmanager.tools.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.yezh.datatrafficmanager.tools.BytesFormatter;


public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //判断广播消息
        if (action.equals(SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            //如果不为空
            if (bundle != null) {
                //将pdus里面的内容转化成Object[]数组
                Object pdusData[] = (Object[]) bundle.get("pdus");// pdus ：protocol data unit  ：
                //解析短信
                SmsMessage[] msg = new SmsMessage[pdusData.length];
                for (int i = 0; i < msg.length; i++) {
                    byte pdus[] = (byte[]) pdusData[i];
                    msg[i] = SmsMessage.createFromPdu(pdus);
                }
                StringBuffer content = new StringBuffer();//获取短信内容
                StringBuffer phoneNumber = new StringBuffer();//获取地址
                String address="";
                //分析短信具体参数
                for (SmsMessage temp : msg) {
                    content.append(temp.getMessageBody());
                    phoneNumber.append(temp.getOriginatingAddress());
                    address = temp.getOriginatingAddress();
                }
                System.out.println("发送者号码：" + address + "  短信内容：" + content.toString());
                String strContent = content.toString();
                String flagStr = "使用";
                int int_flag=strContent.indexOf(flagStr);
                if (!(int_flag>0)){
                    flagStr = "已用";
                    int_flag=strContent.indexOf(flagStr);
                }
                strContent=strContent.substring(int_flag+2);
                System.out.println(strContent);
                System.out.println("-----------------------");

                Pattern pattern = Pattern.compile("[A-Z]");
                Matcher matcher = pattern.matcher(strContent);

                if(matcher.find()) {
                    //System.out.println(matcher.start());
                    int_flag=strContent.indexOf(matcher.group());
                    System.out.println("位置:"+int_flag);
                    strContent=strContent.substring(0,int_flag+1);
                    System.out.println(strContent);

                    String type = strContent.substring(strContent.length()-1);
                    Double value = Double.parseDouble(strContent.substring(0,strContent.length()-1));
                    BytesFormatter bytesFormatter = new BytesFormatter();
                    long data =  bytesFormatter.convertValueToLong(value,type);
                    System.out.println(data);

                    handle.handle(data);


                } else {
                    System.out.println("Not found!");
                    handle.handle(0);
                }


            }
        }
    }

    private Handle handle;
    public interface Handle{
        public void handle(long s);
    };
    public void setHandle(Handle handle) {
        this.handle = handle;
    }
}