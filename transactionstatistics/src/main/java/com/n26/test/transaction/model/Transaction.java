package com.n26.test.transaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Girish on 03-05-2017.
 */
public class Transaction {
    @JsonProperty("amount")
    double amount;

    @JsonProperty("timestamp")
    long timeStamp;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
