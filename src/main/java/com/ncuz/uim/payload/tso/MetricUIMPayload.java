package com.ncuz.uim.payload.tso;

import java.io.Serializable;
import java.util.List;

public class MetricUIMPayload implements Serializable {
//    private Response response;
    private String origin;
    private int id;
    private String source;
    private String target;
    private String probe;
    private List<SamplePayload> sample;
    private String self;
    private String type;
    private ComputerSystem computerSystem;
    private Device device;
    private Configuration for_configuration_item;
    private float minSampleValue;
    private float maxSampleValue;
    private float meanSampleValue;

    public Configuration getFor_configuration_item() {
        return for_configuration_item;
    }

    public void setFor_configuration_item(Configuration for_configuration_item) {
        this.for_configuration_item = for_configuration_item;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ComputerSystem getComputerSystem() {
        return computerSystem;
    }

    public void setComputerSystem(ComputerSystem computerSystem) {
        this.computerSystem = computerSystem;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

//    public Configuration getConfiguration() {
//        return configuration;
//    }
//
//    public void setConfiguration(Configuration configuration) {
//        this.configuration = configuration;
//    }

    public float getMinSampleValue() {
        return minSampleValue;
    }

    public void setMinSampleValue(float minSampleValue) {
        this.minSampleValue = minSampleValue;
    }

    public float getMaxSampleValue() {
        return maxSampleValue;
    }

    public void setMaxSampleValue(float maxSampleValue) {
        this.maxSampleValue = maxSampleValue;
    }

    public float getMeanSampleValue() {
        return meanSampleValue;
    }

    public void setMeanSampleValue(float meanSampleValue) {
        this.meanSampleValue = meanSampleValue;
    }

    public MetricUIMPayload(boolean error){
        if(error)
//        this.setResponse(new Response());
            this.setComputerSystem(new ComputerSystem());
            this.setDevice(new Device());
            this.setFor_configuration_item(new Configuration());
//            this.setSample(new ArrayList<SamplePayload>());
    }

//    public Response getResponse() {
//        return response;
//    }
//
//    public void setResponse(Response response) {
//        this.response = response;
//    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<SamplePayload> getSample() {
        return sample;
    }

    public void setSample(List<SamplePayload> sample) {
        this.sample = sample;
    }
}
