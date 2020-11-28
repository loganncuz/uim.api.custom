package com.ncuz.uim.payload.tso;

import java.io.Serializable;

public class Device implements Serializable {
    private String id;
    private String selft;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelft() {
        return selft;
    }

    public void setSelft(String selft) {
        this.selft = selft;
    }
}
