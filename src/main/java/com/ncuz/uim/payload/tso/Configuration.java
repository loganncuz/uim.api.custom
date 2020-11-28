package com.ncuz.uim.payload.tso;

import java.io.Serializable;

public class Configuration implements Serializable {
    private String id;
    private String self;
    private String name;
    private String qosName;  // for field QOS
    private String description;
    private String unit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQosName() {
        return qosName;
    }

    public void setQosName(String qosName) {
        this.qosName = qosName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
