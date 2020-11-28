package com.ncuz.uim.entity;

public class MetricUIM {
    private String origin;
    private int table_id;
    private String source;
    private String target;
    private String probe;
    private String sampletime;
    private float samplevalue;
    private float samplerate;
    private String qosName;  // for field QOS


    public String getQosName() {
        return qosName;
    }

    public void setQosName(String qosName) {
        this.qosName = qosName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getTable_id() {
        return table_id;
    }

    public void setTable_id(int table_id) {
        this.table_id = table_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getProbe() {
        return probe;
    }

    public void setProbe(String probe) {
        this.probe = probe;
    }

    public String getSampletime() {
        return sampletime;
    }

    public void setSampletime(String sampletime) {
        this.sampletime = sampletime;
    }

    public float getSamplevalue() {
        return samplevalue;
    }

    public void setSamplevalue(float samplevalue) {
        this.samplevalue = samplevalue;
    }

    public float getSamplerate() {
        return samplerate;
    }

    public void setSamplerate(float samplerate) {
        this.samplerate = samplerate;
    }
}
