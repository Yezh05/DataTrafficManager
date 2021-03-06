package edu.yezh.datatrafficmanager.model;

public class TransInfo implements Comparable<TransInfo> {
    long rx,tx,total;

    public TransInfo(long rx, long tx) {
        this.rx = rx;
        this.tx = tx;
        this.total = rx+tx;
    }

    public long getRx() {
        return rx;
    }

    public long getTx() {
        return tx;
    }

    public long getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "TransInfo{" +
                "rx=" + rx +
                ", tx=" + tx +
                ", total=" + total +
                '}';
    }

    @Override
    public int compareTo(TransInfo o) {
        if(this.total-o.getTotal()>0){
            return 1;
        }else if (this.total-o.getTotal()<0){
            return -1;
        }else
        {
            return  0;
        }
    }
}
