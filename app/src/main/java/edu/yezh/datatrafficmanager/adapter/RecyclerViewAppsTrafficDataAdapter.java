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
import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.InstalledAppsInfoTools;

public class RecyclerViewAppsTrafficDataAdapter extends RecyclerView.Adapter<RecyclerViewAppsTrafficDataAdapter.ViewHolder> {
    List<Map<String, String>> InstalledAppsTrafficData;
    Context context;

    public RecyclerViewAppsTrafficDataAdapter(List<Map<String, String>> allInstalledAppsTrafficData, Context context) {
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
            Map<String, String> OneInstalledAppsTrafficData = InstalledAppsTrafficData.get(nowposition);
            //Map<String, String> OneInstalledAppsTrafficData = InstalledAppsTrafficData.get(position);
            holder.TextViewColAppName.setText(OneInstalledAppsTrafficData.get("name"));
            holder.TextViewColAppRX.setText(bytesFormatter.getPrintSize(Long.valueOf(OneInstalledAppsTrafficData.get("rxBytes"))));
            holder.TextViewColAppTX.setText(bytesFormatter.getPrintSize(Long.valueOf(OneInstalledAppsTrafficData.get("txBytes"))));
            InstalledAppsInfoTools installedAppsInfoTools = new InstalledAppsInfoTools();
            holder.ImageViewColAppIcon.setImageDrawable(installedAppsInfoTools.getAppIconByPackageName(context,OneInstalledAppsTrafficData.get("pkgname")));
        }
    }

    @Override
    public int getItemCount() {
        //return this.InstalledAppsTrafficData.size()+1;
        return this.InstalledAppsTrafficData.size();
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
