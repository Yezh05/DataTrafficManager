package edu.yezh.datatrafficmanager.model;

import android.graphics.drawable.Drawable;

public class AppsInfo {
    private String uid;
    private String packageName;
    private String name;
    private Drawable appIcon;
    //private long rxBytes,txBytes;
    private TransInfo trans;
    public AppsInfo() {
        super();
    }

    public AppsInfo(String uid, String packageName, String name, Drawable appIcon, long rxBytes, long txBytes) {
        this.uid = uid;
        this.packageName = packageName;
        this.name = name;
        this.appIcon = appIcon;
        this.trans = new TransInfo(rxBytes,txBytes);
    }

    public AppsInfo(String uid, String packageName, String name, Drawable appIcon, TransInfo trans) {
        this.uid = uid;
        this.packageName = packageName;
        this.name = name;
        this.appIcon = appIcon;
        this.trans = trans;
    }

    public TransInfo getTrans() {
        return trans;
    }

    public void setTrans(TransInfo trans) {
        this.trans = trans;
    }
    public void setTrans(long rxBytes, long txBytes) {
        this.trans = new TransInfo(rxBytes,txBytes);
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
                ", trans=" + trans +
                '}';
    }
}
