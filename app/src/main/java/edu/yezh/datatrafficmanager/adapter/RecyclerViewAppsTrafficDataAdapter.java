package edu.yezh.datatrafficmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.ShowAppDetailsActivity;
import edu.yezh.datatrafficmanager.model.AppsInfo;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;

public class RecyclerViewAppsTrafficDataAdapter extends RecyclerView.Adapter<RecyclerViewAppsTrafficDataAdapter.ViewHolder> {
    List<AppsInfo> InstalledAppsTrafficData;
    Context context;
    String subscriberID;
    int networkType;

    public RecyclerViewAppsTrafficDataAdapter(List<AppsInfo> allInstalledAppsTrafficData, Context context,String subscriberID,int networkType) {
        this.InstalledAppsTrafficData = allInstalledAppsTrafficData;
        this.context = context;
        this.subscriberID =subscriberID;
        this.networkType = networkType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apps_traffic_data_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (position==0){
            holder.TextViewColAppName.setText("应用名称");
            holder.TextViewColAppRX.setText("下载流量");
            holder.TextViewColAppTX.setText("上传流量");
        }else
        {
            BytesFormatter bytesFormatter = new BytesFormatter();
            int nowPosition = position-1;
            final AppsInfo OneInstalledAppsTrafficData = InstalledAppsTrafficData.get(nowPosition);
            holder.TextViewColAppName.setText(OneInstalledAppsTrafficData.getName());
            OutputTrafficData dataAppRX = bytesFormatter.getPrintSizebyModel(OneInstalledAppsTrafficData.getRxBytes());
            holder.TextViewColAppRX.setText(Math.round(Double.valueOf(dataAppRX.getValue())*100D)/100D + dataAppRX.getType());
            OutputTrafficData dataAppTX = bytesFormatter.getPrintSizebyModel(OneInstalledAppsTrafficData.getTxBytes());
            holder.TextViewColAppTX.setText(Math.round(Double.valueOf(dataAppTX.getValue())*100D)/100D + dataAppTX.getType());
            holder.ImageViewColAppIcon.setImageDrawable(OneInstalledAppsTrafficData.getAppIcon());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowAppDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uid",OneInstalledAppsTrafficData.getUid());
                    bundle.putString("name",OneInstalledAppsTrafficData.getName());
                    bundle.putString("packageName",OneInstalledAppsTrafficData.getPackageName());
                    bundle.putLong("rx",OneInstalledAppsTrafficData.getRxBytes());
                    bundle.putLong("tx",OneInstalledAppsTrafficData.getTxBytes());
                    bundle.putString("subscriberID",subscriberID);
                    bundle.putInt("networkType",networkType);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);

                }
            });
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
