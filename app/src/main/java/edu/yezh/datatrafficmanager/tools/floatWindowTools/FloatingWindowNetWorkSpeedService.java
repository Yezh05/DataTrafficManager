package edu.yezh.datatrafficmanager.tools.floatWindowTools;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import edu.yezh.datatrafficmanager.R;
import edu.yezh.datatrafficmanager.model.OutputTrafficData;
import edu.yezh.datatrafficmanager.tools.BytesFormatter;

public class FloatingWindowNetWorkSpeedService extends Service {
    public static boolean isStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    TextView textView;
    private Button button;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 470;
        layoutParams.height = 150;
        layoutParams.x = 300;
        layoutParams.y = 300;
        layoutParams.alpha = 0.7f;
        System.out.println("创建Service");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(textView);
        isStarted = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            button = new Button(getApplicationContext());
            button.setText("Floating Window");
            button.setBackgroundColor(Color.BLUE);

            textView = new TextView(getApplicationContext());
            textView.setText("");
            //textView.setBackgroundColor(Color.BLACK);
            textView.setTextSize(14);
            textView.setTextColor(Color.WHITE);

            Drawable drawable = getDrawable(R.drawable.textview_shape);
                        textView.setBackground(drawable);
            //textView.getBackground().mutate().setAlpha(12);
           //windowManager.addView(button, layoutParams);
            windowManager.addView(textView, layoutParams);
            textView.setOnTouchListener(new FloatingOnTouchListener());
            //button.setOnTouchListener(new FloatingOnTouchListener());

            /*Thread thread = new Thread(){
                @Override
                public void run() {*/
                    final BytesFormatter bytesFormatter = new BytesFormatter();
                    final long[] RXOld = new long[2];
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        int firstTimeShow = 0;
                        @Override
                        public void run() {
                            if (firstTimeShow != 0) {
                                long overTxTraffic = TrafficStats.getTotalTxBytes();
                                long overRxTraffic = TrafficStats.getTotalRxBytes();
                                long currentTxDataRate = overTxTraffic - RXOld[0];
                                long currentRxDataRate = overRxTraffic - RXOld[1];
                                OutputTrafficData dataRealTimeTxSpeed = bytesFormatter.getPrintSizeByModel(currentTxDataRate);
                                OutputTrafficData dataRealTimeRxSpeed = bytesFormatter.getPrintSizeByModel(currentRxDataRate);
                                textView.setText("上传速度:"+dataRealTimeTxSpeed.getValueWithNoDecimalPoint() + dataRealTimeTxSpeed.getType() + "/s"+"\n"
                                        +"下载速度:"+dataRealTimeRxSpeed.getValueWithNoDecimalPoint() + dataRealTimeRxSpeed.getType() + "/s");
                                RXOld[0] = overTxTraffic;
                                RXOld[1] = overRxTraffic;

                            } else {
                                long overTxTraffic = TrafficStats.getTotalTxBytes();
                                long overRxTraffic = TrafficStats.getTotalRxBytes();
                                RXOld[0] = overTxTraffic;
                                RXOld[1] = overRxTraffic;
                                firstTimeShow = 1;
                            }
                            //System.out.println("Run");
                            handler.postDelayed(this, 1000);
                        }
                    };

                    handler.postDelayed(runnable, 000);
              /*  }
              };
            thread.start();*/


        }else {
            System.out.println("错误！");
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}