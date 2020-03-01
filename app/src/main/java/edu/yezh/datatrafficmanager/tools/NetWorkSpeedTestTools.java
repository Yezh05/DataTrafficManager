package edu.yezh.datatrafficmanager.tools;


import android.os.SystemClock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetWorkSpeedTestTools {
    /**
     *
     * @param NET_TEST_PATH 下载本地路径
     * @param NET_TEST_URL 远程下载路径
     * @return
     */
    public long checkNetSpeed(String NET_TEST_PATH , String NET_TEST_URL) {
        System.out.println("------------------------------------");
        long testSpeed = 0;
        int fileLength = 0;
        long startTime = 0;
        long endTime = 0;
        long middleTime = 0;
        final String fileName = "test.dat";
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;
        File tmpFile = new File(NET_TEST_PATH);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        final File file = new File(NET_TEST_PATH + fileName);
        try {
            URL url = new URL(NET_TEST_URL);
            try {
                conn = (HttpURLConnection) url.openConnection();
                fileLength = conn.getContentLength();
                if (fileLength <= 0) {
                    System.out.println( "fileLength <= 0");
                    return 0;
                }
                startTime = SystemClock.uptimeMillis() / 1000;
                //startTime = System.currentTimeMillis() / 1000;
                is = conn.getInputStream();
                fos = new FileOutputStream(file);
                byte[] buf = new byte[256];
                conn.connect();
                if (conn.getResponseCode() >= 400) {
                    System.out.println( "conn.getResponseCode() = " + conn.getResponseCode());
                    return 0;
                } else {
                    while (true) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                            }
                        } else {
                            break;
                        }
                        middleTime = SystemClock.uptimeMillis()/1000;
                        //设置超时时间20s
                        if (middleTime - startTime >= 10){
                            break;
                        }
                    }
                }
                endTime = SystemClock.uptimeMillis()/1000;
                //endTime = System.currentTimeMillis() / 1000;
                System.out.println( "结束时间 = " + endTime);
            } catch (IOException e) {
                System.out.println("error message : " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File readyFile = new File(NET_TEST_PATH + fileName);
        long downLength =  readyFile.length();
        System.out.println("下载量： " + downLength );

        //删除本地已下载文件，防止占用存储空间
        //FileUtils.deleteFile(NET_TEST_PATH + fileName);
        readyFile.delete();

        System.out.println( "源文件长度：" + (fileLength));
        System.out.println( "消耗时间：" + (endTime - startTime));
        if ( (endTime - startTime) > 0){
            testSpeed = (downLength ) / (endTime - startTime);
        }else {
            testSpeed = 0;
        }

        return testSpeed;
    }
}
