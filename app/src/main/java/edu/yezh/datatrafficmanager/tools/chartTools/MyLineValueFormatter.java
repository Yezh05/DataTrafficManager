package edu.yezh.datatrafficmanager.tools.chartTools;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.Map;

import edu.yezh.datatrafficmanager.tools.BytesFormatter;

public class MyLineValueFormatter implements IValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        BytesFormatter bytesFormatter = new BytesFormatter();

        Map<String,String> data = bytesFormatter.getPrintSizeWithoutString(Math.round(value));
        //System.out.println((Double.valueOf(data.get("values"))));
        String afterformat = Math.round( (Double.valueOf(data.get("values")))*100D )/100D+data.get("type");
        return afterformat;
    }
}
