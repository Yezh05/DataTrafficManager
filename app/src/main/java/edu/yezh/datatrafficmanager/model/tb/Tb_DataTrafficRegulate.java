package edu.yezh.datatrafficmanager.model.tb;

public class Tb_DataTrafficRegulate {
    private String subscriberID;
    private  long value,settime;

    public Tb_DataTrafficRegulate() {
        super();
    }

    public Tb_DataTrafficRegulate(String subscriberID, long value, long settime) {
        this.subscriberID = subscriberID;
        this.value = value;
        this.settime = settime;
    }

    public String getSubscriberID() {
        return subscriberID;
    }

    public void setSubscriberID(String subscriberID) {
        this.subscriberID = subscriberID;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getSettime() {
        return settime;
    }

    public void setSettime(long settime) {
        this.settime = settime;
    }

    @Override
    public String toString() {
        return "Tb_DataTrafficRegulate{" +
                "subscriberID='" + subscriberID + '\'' +
                ", value=" + value +
                ", settime=" + settime +
                '}';
    }
}
