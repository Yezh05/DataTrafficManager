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
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;

public class ListViewAdapter extends BaseAdapter {
    /*Map<String, List<String>> objectsDataList;
    Context context;
    public ListViewAdapter(Context context,Map<String, List<String>> objectsDataList) {
        super();
        this.context=context;
        this.objectsDataList = objectsDataList;
    }

    @Override
    public int getCount() {
        //System.out.println(objectsDataList.get("LastSixMonthsTrafficDataList").size());
        return objectsDataList.get("LastSixMonthsTrafficDataList").size();
    }*/

    List<Object> objectsDataList;
    Context context;
    public ListViewAdapter(Context context,List<Object> objectsDataList) {
        super();
        this.context=context;
        this.objectsDataList = objectsDataList;
    }

    @Override
    public int getCount() {
        //System.out.println(objectsDataList.get("LastSixMonthsTrafficDataList").size());
        return ((List<TransInfo>)objectsDataList.get(1)).size();
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

        TextView textViewCol1,textViewCol2,textViewCol3,textViewCol4;
        if (convertView == null) {
            // 通过LayoutInflater 类的 from 方法 再 使用 inflate()方法得到指定的布局
            // 得到ListView中要显示的条目的布局
            LayoutInflater from = LayoutInflater.from(context);
            convertView = from.inflate(R.layout.listview_item, null);
            // 从要显示的条目布局中 获得指定的组件
            textViewCol1 = (TextView) convertView.findViewById(R.id.TextViewCol1);
            textViewCol2 = (TextView) convertView.findViewById(R.id.TextViewCol2);
            textViewCol3 = (TextView) convertView.findViewById(R.id.TextViewCol3);
            textViewCol4 = (TextView) convertView.findViewById(R.id.TextViewCol4);

        } else {
            textViewCol1 = (TextView) convertView.findViewById(R.id.TextViewCol1);
            textViewCol2 = (TextView) convertView.findViewById(R.id.TextViewCol2);
            textViewCol3 = (TextView) convertView.findViewById(R.id.TextViewCol3);
            textViewCol4 = (TextView) convertView.findViewById(R.id.TextViewCol4);

        }
        if (position!=0) {
            BytesFormatter bytesFormatter = new BytesFormatter();

            List<TransInfo> transInfoList =  (List<TransInfo>)objectsDataList.get(1);
            List<String> monthStringList = (List<String>)objectsDataList.get(0);

            TransInfo dataBytes = transInfoList.get(position);

            String col1String = monthStringList.get(position);
            textViewCol1.setText(col1String);

            OutputTrafficData DataRx = bytesFormatter.getPrintSizeByModel(dataBytes.getRx());
            String col3String = Math.round(Double.valueOf(DataRx.getValue()) * 100D) / 100D + DataRx.getType();
            textViewCol3.setText(col3String);

            OutputTrafficData DataTx = bytesFormatter.getPrintSizeByModel(dataBytes.getTx());
            String col2String = Math.round(Double.valueOf(DataTx.getValue()) * 100D) / 100D + DataTx.getType();
            textViewCol2.setText(col2String);

            OutputTrafficData DataTotal = bytesFormatter.getPrintSizeByModel(dataBytes.getTotal());
            String col4String = Math.round(Double.valueOf(DataTotal.getValue()) * 100D) / 100D + DataTotal.getType();
            textViewCol4.setText(col4String);

        }else {
            textViewCol1.setText("时间");
            textViewCol2.setText("上传流量");
            textViewCol3.setText("下载流量");
            textViewCol4.setText("总量");
        }
        return convertView;
    }



}
