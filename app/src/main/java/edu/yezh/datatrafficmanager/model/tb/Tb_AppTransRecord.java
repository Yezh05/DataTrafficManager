package edu.yezh.datatrafficmanager.model.tb;

public class Tb_AppTransRecord {
    private String uid;
   private long mobileTX,mobileRX,wifiTX,wifiRX;

    public Tb_AppTransRecord(String uid, long mobileTX, long mobileRX, long wifiTX, long wifiRX) {
        this.uid = uid;
        this.mobileTX = mobileTX;
        this.mobileRX = mobileRX;
        this.wifiTX = wifiTX;
        this.wifiRX = wifiRX;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getMobileRX() {
        return mobileRX;
    }

    public void setMobileRX(long mobileRX) {
        this.mobileRX = mobileRX;
    }

    public long getMobileTX() {
        return mobileTX;
    }

    public void setMobileTX(long mobileTX) {
        this.mobileTX = mobileTX;
    }

    public long getWifiTX() {
        return wifiTX;
    }

    public void setWifiTX(long wifiTX) {
        this.wifiTX = wifiTX;
    }

    public long getWifiRX() {
        return wifiRX;
    }

    public void setWifiRX(long wifiRX) {
        this.wifiRX = wifiRX;
    }

    @Override
    public String toString() {
        return "Tb_AppTransRecord{" +
                "uid='" + uid + '\'' +
                ", mobileRX=" + mobileRX +
                ", mobileTX=" + mobileTX +
                ", wifiRX=" + wifiRX +
                ", wifiTX=" + wifiTX +
                '}';
    }
}
