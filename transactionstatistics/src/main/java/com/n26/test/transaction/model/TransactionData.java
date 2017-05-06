package com.n26.test.transaction.model;

/**
 * Created by Girish on 02-05-2017.
 */

public class TransactionData {
    transient double sum = 0;
    transient double min = Double.MAX_VALUE;
    transient double max = Double.MIN_VALUE;
    transient long count = 0;

    public TransactionData(){}

    public double getSum() {
        return sum;
    }

    public void incrementSum(double sum) {
        this.sum+=sum;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public long getCount() {
        return count;
    }

    public void incrementCount(long count) {
        this.count+=count;
    }
}
