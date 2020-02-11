package edu.yezh.datatrafficmanager.tools.chartTools;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;

public class MyLineValueFormatter implements IValueFormatter {
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        BytesFormatter bytesFormatter = new BytesFormatter();

        OutputTrafficData data = bytesFormatter.getPrintSizebyModel(Math.round(value));
        //System.out.println((Double.valueOf(data.get("values"))));
        String afterformat = Math.round( (Double.valueOf(data.getValue()))*100D )/100D+data.getType();
        data=null;
        return afterformat;
    }
}
