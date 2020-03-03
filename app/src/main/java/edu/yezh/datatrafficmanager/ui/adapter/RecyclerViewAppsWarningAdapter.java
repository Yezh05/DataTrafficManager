package edu.yezh.datatrafficmanager.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.yezh.datatrafficmanager.R;

public class RecyclerViewAppsWarningAdapter extends RecyclerView.Adapter<RecyclerViewAppsWarningAdapter.ViewHolder> {
    private Context context;
    List<String> APP_NAME_LIST,APP_USAGE_LIST,APP_INFO_LIST;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView TextViewColAppName,TextViewColAppTraffic, TextViewColInfo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TextViewColAppName=itemView.findViewById(R.id.TextViewColAppName);
            TextViewColAppTraffic=itemView.findViewById(R.id.TextViewColAppTraffic);
            TextViewColInfo =itemView.findViewById(R.id.TextViewColInfo);
        }
    }
    public RecyclerViewAppsWarningAdapter(Context context,List<String> APP_NAME_LIST,List<String> APP_USAGE_LIST,List<String> APP_INFO_LIST) {
        this.context = context;
        this.APP_NAME_LIST=APP_NAME_LIST;
        this.APP_USAGE_LIST=APP_USAGE_LIST;
        this.APP_INFO_LIST = APP_INFO_LIST;
    }

    @NonNull
    @Override
    public RecyclerViewAppsWarningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_apps_warning_item,parent,false);
        RecyclerViewAppsWarningAdapter.ViewHolder holder = new RecyclerViewAppsWarningAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAppsWarningAdapter.ViewHolder holder, int position) {
        holder.TextViewColAppName.setText(APP_NAME_LIST.get(position));
        holder.TextViewColAppTraffic.setText(APP_USAGE_LIST.get(position));
        holder.TextViewColInfo.setText(APP_INFO_LIST.get(position));
        if (APP_INFO_LIST.get(position).indexOf("APP")>0){
            holder.TextViewColAppName.setTextColor(Color.parseColor("#DB7093"));
            holder.TextViewColAppTraffic.setTextColor(Color.parseColor("#DB7093"));
            holder.TextViewColInfo.setTextColor(Color.parseColor("#DB7093"));
        }

    }


    @Override
    public int getItemCount() {
        return APP_NAME_LIST.size();
    }

    public void addData(int position,String APP_NAME,String APP_USAGE) {
        //   在list中添加数据，并通知条目加入一条
        APP_NAME_LIST.add(position, APP_NAME);
        APP_USAGE_LIST.add(position,APP_USAGE);
        //添加动画
        notifyItemInserted(position);
    }
}
