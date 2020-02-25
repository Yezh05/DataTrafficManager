package edu.yezh.datatrafficmanager.tools;

import edu.yezh.datatrafficmanager.model.OutputTrafficData;

public class BytesFormatter {
    public long convertValueToLong(double value,String type){
        long result;
        switch (type){
            case "字节":
            case "B":
            case "Byte":
            case "Bytes":
            case "byte":
            case "bytes": result = Math.round(value); break;
            case "K":
            case "KB" :
            case "kb": result =  Math.round(value*1024D) ;break;
            case "M":
            case "MB":
            case "mb": result =  Math.round(value*1024D*1024D);break;
            case "GB":
            case "G":
            case "gb": result =  Math.round(value*1024D*1024D*1024D);break;
            default: result =  0;break;
        }
        return result;
    }
    public OutputTrafficData getPrintSizeByModel(long originSize){
        OutputTrafficData data;
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        double size = Double.valueOf(String.valueOf(originSize));
        if (size == 0) {
            data = new OutputTrafficData(String.valueOf(size),"");
            return  data;
        }else
        if (size < 1024) {
            data = new OutputTrafficData(String.valueOf(size),"Bytes");
            return  data;
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            data = new OutputTrafficData(String.valueOf(size),"KB");
            return  data;
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            //size = size * 100;
            data = new OutputTrafficData(String.valueOf(size),"MB");
            return  data;
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size  / 1024;
            data = new OutputTrafficData(String.valueOf(size),"GB");
            return  data;
        }
    }
}
