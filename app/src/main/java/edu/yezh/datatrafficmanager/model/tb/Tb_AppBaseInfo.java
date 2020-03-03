package edu.yezh.datatrafficmanager.model.tb;

public class Tb_AppBaseInfo {
    private String uid, pkgName;

    public Tb_AppBaseInfo(String uid, String pkgName) {
        this.uid = uid;
        this.pkgName = pkgName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    @Override
    public String toString() {
        return "Tb_AppBaseInfo{" +
                "uid='" + uid + '\'' +
                ", pkgName='" + pkgName + '\'' +
                '}';
    }
}
