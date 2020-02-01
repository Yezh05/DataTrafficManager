package edu.yezh.datatrafficmanager;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.yezh.datatrafficmanager.tools.BytesFormatter;
import edu.yezh.datatrafficmanager.tools.DateTools;

import static android.content.Context.NETWORK_STATS_SERVICE;

public class MyFragment2 extends Fragment {
    public MyFragment2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.t2, container, false);
//        TextView txt_content = (TextView) view.findViewById(R.id.txt_content);
        //      txt_content.setText("第一个Fragment");
        Log.e("HEHE", "2日狗");

        Context context = this.getContext();
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(NETWORK_STATS_SERVICE);
        NetworkStats.Bucket bucket = null;
        // 获取到目前为止设备的4G流量统计

        try {
            DateTools dateTools = new DateTools();

            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", dateTools.getTimesMonthmorning(), System.currentTimeMillis());
            Log.i("Info", "Total: " + (bucket.getRxBytes() + bucket.getTxBytes()));

            /*bucket.getTxBytes() 为发送*/

            long rxBytes = bucket.getRxBytes();
            BytesFormatter bytesFormatter = new BytesFormatter();
            String readableData = bytesFormatter.getPrintSize(rxBytes);
            TextView textView = (TextView) view.findViewById(R.id.DataWLAN);
            textView.setText("本月已下载: " + readableData);

        } catch (RemoteException e) {
            Log.e("bucket", "GetTotalError");
            e.printStackTrace();
        }


        return view;
    }
}
