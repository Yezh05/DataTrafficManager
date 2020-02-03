package edu.yezh.datatrafficmanager.tools.chartTools;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import edu.yezh.datatrafficmanager.tools.BytesFormatter;

public class MyLineValueFormatter implements IValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        BytesFormatter bytesFormatter = new BytesFormatter();
        String afterformat = bytesFormatter.getPrintSizeWithoutString(Math.round(value)).get("values")+
                bytesFormatter.getPrintSizeWithoutString(Math.round(value)).get("type");
        return afterformat;
    }
}
