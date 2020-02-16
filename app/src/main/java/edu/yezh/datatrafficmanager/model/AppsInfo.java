package edu.yezh.datatrafficmanager.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppsInfo implements Serializable {
    private String uid;
    private String packageName;
    private String name;
    private Drawable appIcon;
    private long rxBytes,txBytes;
    public AppsInfo() {
        super();
    }

    public AppsInfo(String uid, String packageName, String name, Drawable appIcon, long rxBytes, long txBytes) {
        this.uid = uid;
        this.packageName = packageName;
        this.name = name;
        this.appIcon = appIcon;
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
    }

    public long getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public long getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public String toString() {
        return "AppsInfo{" +
                "uid='" + uid + '\'' +
                ", packageName='" + packageName + '\'' +
                ", name='" + name + '\'' +
                ", appIcon=" + appIcon +
                ", rxBytes=" + rxBytes +
                ", txBytes=" + txBytes +
                '}';
    }
}
