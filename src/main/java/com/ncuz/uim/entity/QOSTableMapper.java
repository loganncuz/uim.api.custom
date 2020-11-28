package com.ncuz.uim.entity;

import java.io.Serializable;

public class QOSTableMapper implements Serializable {
    private String rTable;
    private String hTable;
    private String dTable;
    private String qos;
    private String target;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQos() {
        return qos;
    }

    public void setQos(String qos) {
        this.qos = qos;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getrTable() {
        return rTable;
    }

    public void setrTable(String rTable) {
        this.rTable = rTable;
    }

    public String gethTable() {
        return hTable;
    }

    public void sethTable(String hTable) {
        this.hTable = hTable;
    }

    public String getdTable() {
        return dTable;
    }

    public void setdTable(String dTable) {
        this.dTable = dTable;
    }
}
