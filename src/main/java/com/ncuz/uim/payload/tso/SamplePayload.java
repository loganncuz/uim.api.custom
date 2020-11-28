package com.ncuz.uim.payload.tso;

import java.io.Serializable;

public class SamplePayload implements Serializable {

    private String time;
    private long epochtime;
    private float value;
    private float rate;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getEpochtime() {
        return epochtime;
    }

    public void setEpochtime(long epochtime) {
        this.epochtime = epochtime;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
