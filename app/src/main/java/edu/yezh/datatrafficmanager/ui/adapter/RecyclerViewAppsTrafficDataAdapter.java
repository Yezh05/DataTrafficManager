package edu.yezh.datatrafficmanager.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.ui.ShowAppDetailsActivity;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_apps_traffic_data_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (position==0){
            holder.ImageViewColAppIcon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1,1));
            holder.TextViewColAppName.setText("应用名称");
            holder.TextViewColAppRX.setText("下载流量");
            holder.TextViewColAppTX.setText("上传流量");
            holder.TextViewColAppTotal.setText("总量");
        }else
        {
            BytesFormatter bytesFormatter = new BytesFormatter();
            int nowPosition = position-1;
            final AppsInfo OneInstalledAppsTrafficData = InstalledAppsTrafficData.get(nowPosition);
            holder.TextViewColAppName.setText(OneInstalledAppsTrafficData.getName());
            OutputTrafficData dataAppRX = bytesFormatter.getPrintSizeByModel(OneInstalledAppsTrafficData.getTrans().getRx());
            holder.TextViewColAppRX.setText(dataAppRX.getValueWithTwoDecimalPoint() + dataAppRX.getType());
            OutputTrafficData dataAppTX = bytesFormatter.getPrintSizeByModel(OneInstalledAppsTrafficData.getTrans().getTx());
            holder.TextViewColAppTX.setText(dataAppTX.getValueWithTwoDecimalPoint() + dataAppTX.getType());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.ImageViewColAppIcon.setImageDrawable(OneInstalledAppsTrafficData.getAppIcon());
                }
            },200L);
            OutputTrafficData dataAppTotal = bytesFormatter.getPrintSizeByModel(OneInstalledAppsTrafficData.getTrans().getTotal());
            holder.TextViewColAppTotal.setText(dataAppTotal.getValueWithTwoDecimalPoint() + dataAppTotal.getType());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowAppDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uid",OneInstalledAppsTrafficData.getUid());
                    bundle.putString("name",OneInstalledAppsTrafficData.getName());
                    bundle.putString("packageName",OneInstalledAppsTrafficData.getPackageName());
                    bundle.putLong("rx",OneInstalledAppsTrafficData.getTrans().getRx());
                    bundle.putLong("tx",OneInstalledAppsTrafficData.getTrans().getTx());
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
        return this.InstalledAppsTrafficData.size()+1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextViewColAppName,TextViewColAppTX,TextViewColAppRX,TextViewColAppTotal;
        ImageView ImageViewColAppIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewColAppIcon = itemView.findViewById(R.id.ImageViewColAppIcon);
            TextViewColAppName = itemView.findViewById(R.id.TextViewColAppName);
            TextViewColAppTX = itemView.findViewById(R.id.TextViewColAppTX);
            TextViewColAppRX = itemView.findViewById(R.id.TextViewColAppRX);
            TextViewColAppTotal = itemView.findViewById(R.id.TextViewColAppTotal);
        }
    }

}
