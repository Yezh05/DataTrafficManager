package edu.yezh.datatrafficmanager.model.sp;

public class Sp_NetworkPlan {
    long dataPlanLong;
    int dataPlanStartDay;

    public long getDataPlanLong() {
        return dataPlanLong;
    }

    public void setDataPlanLong(long dataPlanLong) {
        this.dataPlanLong = dataPlanLong;
    }

    public int getDataPlanStartDay() {
        return dataPlanStartDay;
    }

    public void setDataPlanStartDay(int dataPlanStartDay) {
        this.dataPlanStartDay = dataPlanStartDay;
    }

    public Sp_NetworkPlan(long dataPlan, int dataPlanStartDay) {
        this.dataPlanLong = dataPlan;
        this.dataPlanStartDay = dataPlanStartDay;
    }

    @Override
    public String toString() {
        return "Sp_NetworkPlan{" +
                "dataPlanLong=" + dataPlanLong +
                ", dataPlanStartDay=" + dataPlanStartDay +
                '}';
    }
}
