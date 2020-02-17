package edu.yezh.datatrafficmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;

public class ListViewAdapter extends BaseAdapter {
    Map<String, List<String>> lastSixMonthsTrafficDataMap;
    Context context;
    public ListViewAdapter(Context context,Map<String, List<String>> lastSixMonthsTrafficDataMap) {
        super();
        this.context=context;
        this.lastSixMonthsTrafficDataMap = lastSixMonthsTrafficDataMap;
    }

    @Override
    public int getCount() {
        //System.out.println(lastSixMonthsTrafficDataMap.get("LastSixMonthsTrafficDataList").size());
        return lastSixMonthsTrafficDataMap.get("LastSixMonthsTrafficDataList").size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BytesFormatter bytesFormatter = new BytesFormatter();
        Long dataBytes= Long.valueOf(lastSixMonthsTrafficDataMap.get("LastSixMonthsTrafficDataList").get(position));
        String col1String = lastSixMonthsTrafficDataMap.get("MonthString").get(position);
        OutputTrafficData DataMonthUsage = bytesFormatter.getPrintSizeByModel(dataBytes);
        String col2String = Math.round(Double.valueOf(DataMonthUsage.getValue())*100D)/100D + DataMonthUsage.getType();
        TextView textViewCol1;
        TextView textViewCol2;
        if (convertView == null) {
            // 通过LayoutInflater 类的 from 方法 再 使用 inflate()方法得到指定的布局
            // 得到ListView中要显示的条目的布局
            LayoutInflater from = LayoutInflater.from(context);
            convertView = from.inflate(R.layout.listview_item, null);
            // 从要显示的条目布局中 获得指定的组件
            textViewCol1 = (TextView) convertView.findViewById(R.id.TextViewCol1);
            textViewCol2 = (TextView) convertView.findViewById(R.id.TextViewCol2);

        }else{
            textViewCol1= (TextView) convertView.findViewById(R.id.TextViewCol1);
            textViewCol2= (TextView) convertView.findViewById(R.id.TextViewCol2);

        }
        textViewCol1.setText(col1String);
        textViewCol2.setText(col2String);

        return convertView;
    }



}
