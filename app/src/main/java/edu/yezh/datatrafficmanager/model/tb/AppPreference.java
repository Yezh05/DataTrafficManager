package edu.yezh.datatrafficmanager.model.tb;

public class AppPreference {
    private String uid, pkgName;
    private int sim1IgnoreFlag,sim2IgnoreFlag;

    public AppPreference() {
        super();
    }

    public AppPreference(String uid, String pkgName, int sim1IgnoreFlag, int sim2IgnoreFlag) {
        this.uid = uid;
        this.pkgName = pkgName;
        this.sim1IgnoreFlag = sim1IgnoreFlag;
        this.sim2IgnoreFlag = sim2IgnoreFlag;
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

    public int getSim1IgnoreFlag() {
        return sim1IgnoreFlag;
    }

    public void setSim1IgnoreFlag(int sim1IgnoreFlag) {
        this.sim1IgnoreFlag = sim1IgnoreFlag;
    }

    public int getSim2IgnoreFlag() {
        return sim2IgnoreFlag;
    }

    public void setSim2IgnoreFlag(int sim2IgnoreFlag) {
        this.sim2IgnoreFlag = sim2IgnoreFlag;
    }

    @Override
    public String toString() {
        return "AppPreference{" +
                "uid='" + uid + '\'' +
                ", pkgName='" + pkgName + '\'' +
                ", sim1IgnoreFlag=" + sim1IgnoreFlag +
                ", sim2IgnoreFlag=" + sim2IgnoreFlag +
                '}';
    }
}
