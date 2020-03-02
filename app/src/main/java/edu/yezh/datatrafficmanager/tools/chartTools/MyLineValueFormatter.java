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
    OutputTrafficData data = bytesFormatter.getPrintSizeByModel(Math.round(value));
    //System.out.println((Double.valueOf(data.get("values"))));
    String afterFormat = data.getValueWithTwoDecimalPoint()+data.getType();
    return afterFormat;
  }
}