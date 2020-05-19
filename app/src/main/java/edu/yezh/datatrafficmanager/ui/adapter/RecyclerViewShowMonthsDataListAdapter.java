package edu.yezh.datatrafficmanager.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.model.TransInfo;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.ui.CustomQueryActivity;

import java.util.List;

public class RecyclerViewShowMonthsDataListAdapter extends RecyclerView.Adapter<RecyclerViewShowMonthsDataListAdapter.ViewHolder> {
    String subscriberID ;
    int networkType ;
    List<Object> objectsDataList;
    Context context;

    public RecyclerViewShowMonthsDataListAdapter(Context context,String subscriberID, int networkType, List<Object> objectsDataList) {
        this.subscriberID = subscriberID;
        this.networkType = networkType;
        this.objectsDataList = objectsDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_listview_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position!=0) {
            final int realPosition = position-1;
            BytesFormatter bytesFormatter = new BytesFormatter();

            List<TransInfo> transInfoList =  (List<TransInfo>)objectsDataList.get(1);
            List<String> monthStringList = (List<String>)objectsDataList.get(0);

            TransInfo dataBytes = transInfoList.get(realPosition);

            String col1String = monthStringList.get(realPosition);
            holder.textViewCol1.setText(col1String);

            OutputTrafficData DataRx = bytesFormatter.getPrintSizeByModel(dataBytes.getRx());
            String col3String = DataRx.getValueWithTwoDecimalPoint() + DataRx.getType();
            holder.textViewCol3.setText(col3String);

            OutputTrafficData DataTx = bytesFormatter.getPrintSizeByModel(dataBytes.getTx());
            String col2String = DataTx.getValueWithTwoDecimalPoint() + DataTx.getType();
            holder.textViewCol2.setText(col2String);

            OutputTrafficData DataTotal = bytesFormatter.getPrintSizeByModel(dataBytes.getTotal());
            String col4String = DataTotal.getValueWithTwoDecimalPoint() + DataTotal.getType();
            holder.textViewCol4.setText(col4String);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CustomQueryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("subscriberID",subscriberID);
                    //System.out.println("点击时:"+bundle.getLong("startTime"));
                    bundle.putInt("networkType",networkType);
                    bundle.putInt("startCode",900);
                    bundle.putLong("startTime", Long.parseLong (((List<String>) objectsDataList.get(2)).get(realPosition)));
                    bundle.putLong("endTime", Long.parseLong (((List<String>) objectsDataList.get(3)).get(realPosition)));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }else {
            holder.textViewCol1.setText("时间");
            holder.textViewCol2.setText("上传流量");
            holder.textViewCol3.setText("下载流量");
            holder.textViewCol4.setText("总量");
        }
    }

    @Override
    public int getItemCount() {
        return ((List)objectsDataList.get(0)).size() +1;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewCol1,textViewCol2,textViewCol3,textViewCol4;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCol1 = (TextView) itemView.findViewById(R.id.TextViewCol1);
            textViewCol2 = (TextView) itemView.findViewById(R.id.TextViewCol2);
            textViewCol3 = (TextView) itemView.findViewById(R.id.TextViewCol3);
            textViewCol4 = (TextView) itemView.findViewById(R.id.TextViewCol4);
        }
    }
}
