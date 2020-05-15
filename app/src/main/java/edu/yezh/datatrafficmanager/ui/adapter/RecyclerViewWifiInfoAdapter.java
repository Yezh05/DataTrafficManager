package edu.yezh.datatrafficmanager.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.yezh.datatrafficmanager.R;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewWifiInfoAdapter extends RecyclerView.Adapter<RecyclerViewWifiInfoAdapter.ViewHolder> {
    Context context;
    List<ScanResult> wifiList;
    HashMap<Integer,Integer> CHANNEL_FREQUENCY_MAP;
    String nowSSID;
    public RecyclerViewWifiInfoAdapter(Context context, List<ScanResult> wifiList,HashMap<Integer,Integer> CHANNEL_FREQUENCY_MAP,String nowSSID) {
        this.context = context;
        this.wifiList = wifiList;
        this.CHANNEL_FREQUENCY_MAP = CHANNEL_FREQUENCY_MAP;

        this.nowSSID = nowSSID;
    }

    int getChannel(int fre){
        int channel=0;
        channel = this.CHANNEL_FREQUENCY_MAP.get(fre);
        return channel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_wifi_info_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult  nowScanResult= this.wifiList.get(position);
        holder.TextViewWifiSSID.setText( nowScanResult.SSID);
        //System.out.println(nowScanResult.SSID+"|"+nowSSID);
        if (nowScanResult.SSID.equals(this.nowSSID)){
            System.out.println("wifi-SSID相等");
            holder.TextViewWifiSSID.append("(当前)");
            holder.TextViewWifiSSID.setTextColor(Color.parseColor("#DC143C"));
        }
        if (nowScanResult.capabilities.length()>0){
            holder.TextViewWifiIsLock.setText("是");
        }else {
            holder.TextViewWifiIsLock.setText("否");
        }
        int channel = getChannel(nowScanResult.frequency);
        holder.TextViewWifiChannel.setText(""+channel);
        holder.TextViewWifiSignal.setText("信号强度:"+nowScanResult.level+"dbm");

        int convertSignal = 100 + nowScanResult.level;
        if (convertSignal<=0){convertSignal =0;}
        holder.ProgressBarWifiSignal.setProgress(convertSignal);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(nowScanResult.SSID+"信息").setMessage("" +
                        "\nSSID:"+nowScanResult.SSID+ "\n"+
                        "\nMAC地址:"+nowScanResult.BSSID+ "\n"+
                        "\n加密方式:"+nowScanResult.capabilities+ "\n"+
                        "\n信号强度:"+nowScanResult.level+"dbm"+ "\n"
                ).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.wifiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextViewWifiSSID,TextViewWifiIsLock,TextViewWifiChannel,TextViewWifiSignal;
        ProgressBar ProgressBarWifiSignal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TextViewWifiSSID = itemView.findViewById(R.id.TextViewWifiSSID);
                    TextViewWifiIsLock = itemView.findViewById(R.id.TextViewWifiIsLock);
                    TextViewWifiChannel= itemView.findViewById(R.id.TextViewWifiChannel);
                    TextViewWifiSignal= itemView.findViewById(R.id.TextViewWifiSignal);
            ProgressBarWifiSignal = itemView.findViewById(R.id.ProgressBarWifiSignal);
        }
    }
}
