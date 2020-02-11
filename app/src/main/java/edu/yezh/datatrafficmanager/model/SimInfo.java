package edu.yezh.datatrafficmanager.model;

import android.telephony.SubscriptionInfo;

public class SimInfo {
    private SubscriptionInfo subscriptionInfo;
    private String SubscriberId;

    public SimInfo() {
        super();
    }

    public SimInfo(SubscriptionInfo subscriptionInfo, String subscriberId) {
        this.subscriptionInfo = subscriptionInfo;
        SubscriberId = subscriberId;
    }

    public SubscriptionInfo getSubscriptionInfo() {
        return subscriptionInfo;
    }

    public void setSubscriptionInfo(SubscriptionInfo subscriptionInfo) {
        this.subscriptionInfo = subscriptionInfo;
    }

    public String getSubscriberId() {
        return SubscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        SubscriberId = subscriberId;
    }

    @Override
    public String toString() {
        return "SimInfo{" +
                "subscriptionInfo=" + subscriptionInfo +
                ", SubscriberId='" + SubscriberId + '\'' +
                '}';
    }
}
