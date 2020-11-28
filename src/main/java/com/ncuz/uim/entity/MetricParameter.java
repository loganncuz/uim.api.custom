package com.ncuz.uim.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MetricParameter {
    private String id_lookup;
    private String id;
    private String metric_type_lookup;
    private String metricFilter;
    private String target;
    private String period;
    private String showSamples;
    private String[] periodList;
    private String startPeriod;
    private String endPeriod;

    public String[] getPeriodList() {
        return periodList;
    }

    public void setPeriodList(String[] periodList) {
//        System.out.println("setPeriodList :"+periodList.length);
        this.periodList = periodList;
    }

    public String getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(String startPeriod) {
        this.startPeriod = startPeriod;
    }

    public String getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(String endPeriod) {
        this.endPeriod = endPeriod;
    }

    public String getId_lookup() {
        return id_lookup;
    }

    public void setId_lookup(String id_lookup) {
        this.id_lookup = replaceCurlyBraces(id_lookup);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id =replaceCurlyBraces(id) ;
    }

    public String getMetric_type_lookup() {
        return metric_type_lookup;
    }

    public void setMetric_type_lookup(String metric_type_lookup) {
        this.metric_type_lookup = replaceCurlyBraces(metric_type_lookup);
    }

    public String getMetricFilter() {
        return metricFilter;
    }

    public void setMetricFilter(String metricFilter) {
        this.metricFilter = replaceCurlyBraces(metricFilter);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = replaceCurlyBraces(target);
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
//        System.out.println("period :"+period);
        if (!period.equals("")) {
            this.period = replaceCurlyBraces(period);
            DateFormat  readF = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

//            System.out.println("getTimeZone :"+readF.getTimeZone());
            if(!"latest".equals(this.getPeriod())){
                this.setPeriodList(this.getPeriod().split("\\|"));
//          TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//

                readF.setTimeZone(TimeZone.getTimeZone("UTC"));
//                System.out.println("readDate :"+this.getPeriodList()[0]);
                Date date1 = null;
                try {
                    date1=readF.parse(this.getPeriodList()[0]);
//                    System.out.println("start Date :"+date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String writeFormat="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                DateFormat writeF = new SimpleDateFormat( writeFormat);
                this.setStartPeriod(writeF.format( date1 ));

                if(this.getPeriodList().length==2){
                    date1 = null;
                    readF = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                    System.out.println("getTimeZone :"+readF.getTimeZone());
                    try {
                        readF.setTimeZone(TimeZone.getTimeZone("UTC"));
                        date1=readF.parse(this.getPeriodList()[1]);
//                        System.out.println("End Date :"+date1);
//            logger.debug("readDate :"+date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    writeF = new SimpleDateFormat( writeFormat);
                    this.setEndPeriod(writeF.format( date1 ));
                }else{
                    this.setEndPeriod("");
                }
                this.period=this.getStartPeriod()+"|"+this.getEndPeriod();
            }
        };
    }

    public String getShowSamples() {
        return showSamples;
    }

    public void setShowSamples(String showSamples) {
        this.showSamples = replaceCurlyBraces(showSamples);
    }

    public MetricParameter(){
        this.setId("");
        this.setShowSamples("");
        this.setPeriod("");
        this.setTarget("");
        this.setMetricFilter("");
        this.setMetric_type_lookup("");
        this.setId_lookup("");
//        System.out.println("metricFilter :"+this.getMetricFilter());
    }


    public MetricParameter(String id_lookup,
                           String id,
                           String metric_type_lookup,
                           String metricFilter,
                           String target,
                           String period,
                           String showSamples){
        this.setId(additionalSuffix(id,false,false));
        this.setShowSamples(showSamples);
        this.setPeriod(period);
        this.setTarget(additionalSuffix(target,true,true));
        this.setMetricFilter(metricFilter);
        this.setMetric_type_lookup(metric_type_lookup);
        this.setId_lookup(id_lookup);
// System.out.println("metricFilter :"+this.getId());
//        System.out.println("metricFilter :"+this.getTarget());
    }

    private String additionalSuffix(String items,Boolean active,Boolean uppercase){
        String result="";
        if(items.equals(" ") || items.equals("")) return items;
        String[] list=items.split(",");
        String [] suffixList={".telkomsel.co.id"};
        List<String> records = new ArrayList<String>();
        for(int i=0;i<suffixList.length;i++){
            for(int k=0;k<list.length;k++){
//                list[k]=list[k]+suffixList[i];
                records.add(list[k]+suffixList[i]);
                if(uppercase)
                    records.add(list[k].toUpperCase()+suffixList[i]);
            }
        }
        for(int k=0;k<records.size();k++){
            if(!records.get(k).contains("--")){
                result=result+records.get(k)+",";
            }
        }
        if(active){
            result=result+items;
        }else
            result=items;

        return result;
    }

    private String replaceCurlyBraces(String text){
        if(text==null) return "";
        String remark=text.replace("{","");
        remark=remark.replace("}","");
        return remark;
    }

//    public MetricParameter(Map<String,String[]> params){
//
//    }

    public MetricParameter(Map<String,String[]> params){
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String val= "";
            try{
                val=Arrays.toString(entry.getValue());
                val=val.substring(1,val.length()-1);
            }catch (Exception e){
                val=String.valueOf(entry.getValue());
            }
//            System.out.println(" ! Key : " + entry.getKey() + " Value : " + val);
          System.out.println("Key : " + entry.getKey() + " Value : " +  String.valueOf(val));
            switch (entry.getKey()) {
                case "id_lookup":{
                    this.setId_lookup(String.valueOf(val));
//                    System.out.println("Key id : " + entry.getKey() + " Value : " + this.getId_lookup());
                    break;
                }
                case "metric_type_lookup":{
                    this.setMetric_type_lookup(String.valueOf(val));
//                    System.out.println("Key id : " + entry.getKey() + " Value : " + this.getMetric_type_lookup());
                    break;
                }
                case "metricFilter":{
                    this.setMetricFilter(String.valueOf(val));
//                    System.out.println("Key id : " + entry.getKey() + " Value : " + this.getMetricFilter());
                    break;
                }
                case "target":{
                    this.setTarget(String.valueOf(val));
                    System.out.println("Key id : " + entry.getKey() + " Value : " + this.getTarget());
                    break;
                }
                case "period":{
                    this.setPeriod(String.valueOf(val));
//                    System.out.println("Key id : " + entry.getKey() + " Value : " + this.getPeriod());
                    break;
                }
                case "showSamples":{
                    this.setShowSamples(String.valueOf(val));
//                    System.out.println("Key id : " + entry.getKey() + " Value : " + this.getShowSamples());
                    break;
                }
                case "id":{
                    this.setId(String.valueOf(val));
//                    System.out.println("Key id : " + entry.getKey() + " Value : " + this.getId());
                    break;
                }
            }
        }
//        System.out.println("metricFilter :"+this.getMetricFilter());
//        System.out.println("");
    }
}
