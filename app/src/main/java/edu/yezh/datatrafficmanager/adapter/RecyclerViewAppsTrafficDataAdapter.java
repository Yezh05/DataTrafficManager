package edu.yezh.datatrafficmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.InstalledAppsInfoTools;

public class RecyclerViewAppsTrafficDataAdapter extends RecyclerView.Adapter<RecyclerViewAppsTrafficDataAdapter.ViewHolder> {
    List<AppsInfo> InstalledAppsTrafficData;
    Context context;

    public RecyclerViewAppsTrafficDataAdapter(List<AppsInfo> allInstalledAppsTrafficData, Context context) {
        this.InstalledAppsTrafficData = allInstalledAppsTrafficData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apps_traffic_data_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position==0){
            holder.TextViewColAppName.setText("应用名称");
            holder.TextViewColAppRX.setText("下载流量");
            holder.TextViewColAppTX.setText("上传流量");
        }else
        {
            BytesFormatter bytesFormatter = new BytesFormatter();
            int nowposition = position-1;
            AppsInfo OneInstalledAppsTrafficData = InstalledAppsTrafficData.get(nowposition);
            holder.TextViewColAppName.setText(OneInstalledAppsTrafficData.getName());
            OutputTrafficData dataAppRX = bytesFormatter.getPrintSizebyModel(OneInstalledAppsTrafficData.getRxBytes());
            holder.TextViewColAppRX.setText(Math.round(Double.valueOf(dataAppRX.getValue())*100D)/100D + dataAppRX.getType());
            OutputTrafficData dataAppTX = bytesFormatter.getPrintSizebyModel(OneInstalledAppsTrafficData.getTxBytes());
            holder.TextViewColAppTX.setText(Math.round(Double.valueOf(dataAppTX.getValue())*100D)/100D + dataAppTX.getType());
            holder.ImageViewColAppIcon.setImageDrawable(OneInstalledAppsTrafficData.getAppIcon());
        }
    }

    @Override
    public int getItemCount() {
        //return this.InstalledAppsTrafficData.size()+1;
        return this.InstalledAppsTrafficData.size()+1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextViewColAppName,TextViewColAppTX,TextViewColAppRX;
        ImageView ImageViewColAppIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewColAppIcon = itemView.findViewById(R.id.ImageViewColAppIcon);
            TextViewColAppName = itemView.findViewById(R.id.TextViewColAppName);
            TextViewColAppTX = itemView.findViewById(R.id.TextViewColAppTX);
            TextViewColAppRX = itemView.findViewById(R.id.TextViewColAppRX);
        }
    }

}
