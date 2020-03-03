package edu.yezh.datatrafficmanager.model.tb;

public class Tb_AppPreference {
    private Tb_AppBaseInfo appBaseInfo;
    private int sim1IgnoreFlag,sim2IgnoreFlag;
    private long warningLimit;

    public Tb_AppPreference() {
        super();
    }

    public Tb_AppPreference(String uid, String pkgName, int sim1IgnoreFlag, int sim2IgnoreFlag,long warningLimit) {
        this.appBaseInfo = new Tb_AppBaseInfo(uid,pkgName);
        this.sim1IgnoreFlag = sim1IgnoreFlag;
        this.sim2IgnoreFlag = sim2IgnoreFlag;
        this.warningLimit = warningLimit;
    }

    public Tb_AppPreference(Tb_AppBaseInfo appBaseInfo, int sim1IgnoreFlag, int sim2IgnoreFlag, long warningLimit) {
        this.appBaseInfo = appBaseInfo;
        this.sim1IgnoreFlag = sim1IgnoreFlag;
        this.sim2IgnoreFlag = sim2IgnoreFlag;
        this.warningLimit = warningLimit;
    }

    public void setAppBaseInfo(String uid, String pkgName) {
        this.appBaseInfo = new Tb_AppBaseInfo(uid,pkgName);
    }

    public Tb_AppBaseInfo getAppBaseInfo() {
        return appBaseInfo;
    }

    public void setAppBaseInfo(Tb_AppBaseInfo appBaseInfo) {
        this.appBaseInfo = appBaseInfo;
    }

    public long getWarningLimit() {
        return warningLimit;
    }

    public void setWarningLimit(long warningLimit) {
        this.warningLimit = warningLimit;
    }

    public String getUid() {
        return appBaseInfo.getUid();
    }


    public String getPkgName() {
        return appBaseInfo.getPkgName();
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
        return "Tb_AppPreference{" +
                "appBaseInfo=" + appBaseInfo +
                ", sim1IgnoreFlag=" + sim1IgnoreFlag +
                ", sim2IgnoreFlag=" + sim2IgnoreFlag +
                ", warningLimit=" + warningLimit +
                '}';
    }
}
