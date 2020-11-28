package com.ncuz.uim.service;

import com.ncuz.exception.LoggingException;
import com.ncuz.uim.dao.MetricUIMV3DAO;
import com.ncuz.uim.entity.MetricParameter;
import com.ncuz.uim.entity.QOSTableMapper;
import com.ncuz.uim.payload.tso.MetricUIMPayload;
import com.ncuz.uim.payload.tso.SamplePayload;
import com.ncuz.uim.sqlstatement.MetricUIMOracleSQLStatement;
import com.ncuz.uim.sqlstatement.MetricUIMSQLStatement;
import com.ncuz.uim.utility.PropertiesUtility;
import log4j.helper.service.Log4jService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class MetricUIMV3Service {
    @Autowired
    Log4jService log4jService;

    @Autowired
    MetricUIMV3DAO metricUIMDAO;

    @Autowired
    PropertiesUtility propertiesUtility;


    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;
    @PostConstruct
    private void post() {
        System.out.println("POST MetricUIMService" );
        logger=log4jService.getLogger(MetricUIMV2Service.class,"file","../log_app/uim.api.custom.MetricUIMV2Service");
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.api.custom.Exception");
    }

    private String getGrafanaDateFormat(String readFormat,String readDate,String writeFormat){
        String datestr=readDate;
//        logger.debug("readFormat :"+readFormat);
//        logger.debug("readDateStr :"+readDate);
//        logger.debug("writeFormat :"+writeFormat);
//        DateFormat readF = new SimpleDateFormat( "dd-MMM-yy hh.mm.ss");
        DateFormat readF = new SimpleDateFormat( readFormat);
        DateFormat writeF = new SimpleDateFormat( writeFormat);
        Date date1 = null;
        String formattedDate = "";
        if(readDate!=null){
            try {
                date1=readF.parse(datestr);
//            logger.debug("readDate :"+date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if( date1 != null ) {
                writeF.setTimeZone(TimeZone.getTimeZone("UTC"));
                formattedDate = writeF.format( date1 );
//            logger.debug("formattedDate :"+formattedDate);
            }
        }

//        logger.debug("ts :"+ts);
//        logger.debug("Date format dd-mm-yy :"+ts+" | "+epoc);
        try{
            return formattedDate;
        }finally {
            date1=null;
            formattedDate=null;
            writeF=null;
            readF=null;
            datestr=null;
//            System.gc();
        }
//        return formattedDate;
    }

    private long getEpocTime(String readFormat,String readDate,String writeFormat){
//        String datestr=readDate+" ICT";
        String datestr=readDate;
        long epoc=0;
        Date date1 = null;
        DateFormat readF=null;
        DateFormat writeF =null;
        String formattedDate = "";
        //TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

//        logger.debug("readFormat :"+readFormat);
//        logger.debug("readDate :"+readDate);
//        logger.debug("writeFormat :"+writeFormat);
//        DateFormat readF = new SimpleDateFormat( "dd-MMM-yy hh.mm.ss");
        if(readDate!=null){
            readF = new SimpleDateFormat( readFormat);
//        readF.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                date1=readF.parse(datestr);
//            logger.debug("readDate :"+date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            writeF = new SimpleDateFormat( writeFormat);

            if( date1 != null ) {
                formattedDate = writeF.format( date1 );
//            logger.debug("formattedDate :"+formattedDate);
            }

//        logger.debug("ts :"+ts);
            epoc = date1.getTime()/1000;
        }

//        logger.debug("EPOCHTIME :"+epoc);
//        return epoc;
        try{
            return epoc;
        }finally {
            date1=null;
            formattedDate=null;
            writeF=null;
            readF=null;
            datestr=null;
//            System.gc();
        }

    }

    private MetricUIMPayload createParentNode(String db_sampletime,
                                              String db_samplerate,
                                              String db_samplevalue,
                                              String db_origin,
                                              String db_source,
                                              String db_target,
                                              String db_probe,
                                              String db_qos,
                                              String db_table_id,
                                              boolean showSample){
//        logger.debug("-------------------createParentNode------------------------");
//        logger.debug("createParentNode :"+metricUIM.getOrigin());
//        logger.debug("createParentNode Timestamp :"+metricUIM.getSampletime());
//        logger.debug("sampletime PARENT :"+metricUIM.getSampletime());
        MetricUIMPayload metricUIMPayload=new MetricUIMPayload(false);
        //java.sql.Timestamp ts = java.sql.Timestamp.valueOf(metricUIM.getSampletime());
        long epoc = 0;
        String sampleTime=null;
        if(!(db_sampletime ==null)){
            if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"))) {
                epoc = getEpocTime("yyyy-MM-dd hh:mm:ss.SSS", db_sampletime, "yyyy-MM-dd hh:mm:ss.SSS");
                sampleTime=getGrafanaDateFormat("yyyy-MM-dd hh:mm:ss.SSS",db_sampletime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            }   else{
                epoc=getEpocTime("dd-MMM-yy HH.mm.ss",db_sampletime.toString(),"yyyy-MM-dd hh:mm:ss.SSS");
                sampleTime=getGrafanaDateFormat("dd-MMM-yy HH.mm.ss",db_sampletime,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            }
        }


//        logger.debug("GRAFANA FORMAT DATE :"+sampleTime);
        List<SamplePayload> samplePayloadList=new ArrayList<SamplePayload>();
        SamplePayload samplePayload=new SamplePayload();
        if(showSample){
//        samplePayload.setTime(metricUIM.getSampletime());
            samplePayload.setTime(sampleTime);
            samplePayload.setEpochtime(epoc);
            String rate= db_samplerate;
            float frate= Float.parseFloat(rate);
            samplePayload.setRate(frate);
//            Double value= (Double) metricUIM.get("samplevalue");
            String value=db_samplevalue;
            float fvalue = Float.parseFloat(value);
            samplePayload.setValue(fvalue );
//            logger.debug("getTime :"+samplePayload.getTime());
//            logger.debug("getEpochtime :"+samplePayload.getEpochtime());
//            logger.debug("getValue :"+samplePayload.getValue());
        }


        metricUIMPayload.setOrigin(db_origin);
        metricUIMPayload.setId(Integer.valueOf(db_table_id) );
        metricUIMPayload.setSource(db_source);
        metricUIMPayload.setTarget(db_target);
        metricUIMPayload.setProbe(db_probe);
        metricUIMPayload.getFor_configuration_item().setQosName(db_qos);


        samplePayloadList.add(samplePayload);
        metricUIMPayload.setSample(samplePayloadList);
//        return metricUIMPayload;
        try{
            return metricUIMPayload;
        }finally {
            samplePayload=null;
            samplePayloadList=null;
            metricUIMPayload=null;
//            System.gc();
        }

    }

    private void createChildNode(List<MetricUIMPayload> metricUIMPayloadList,
                                 String db_sampletime,
                                 String db_samplerate,
                                 String db_samplevalue,
                                 int index,boolean showSample){
//        logger.debug("###################createChildNode########################"+index);
//        logger.debug(index+" | createChildNode : "+metricUIMPayloadList.get(index-1).getSample().size());
        long epoc = 0;

        String sampleTime=null;
        if(showSample){
//            logger.debug("sampletime CHILD :"+metricUIM.getSampletime());
            if(!(db_sampletime ==null)){
                if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"))) {
                    epoc = getEpocTime("yyyy-MM-dd HH:mm:ss.SSS", db_sampletime, "yyyy-MM-dd hh:mm:ss.SSS");
                    sampleTime=getGrafanaDateFormat("yyyy-MM-dd HH:mm:ss.SSS", db_sampletime, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                }   else{
                    epoc=getEpocTime("dd-MMM-yy HH.mm.ss",db_sampletime,"yyyy-MM-dd hh:mm:ss.SSS");
                    sampleTime=getGrafanaDateFormat("dd-MMM-yy HH.mm.ss",db_sampletime,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                }
            }


//        logger.debug("GRAFANA FORMAT DATE :"+sampleTime);

            List<SamplePayload> samplePayloadList=new ArrayList<SamplePayload>();
            SamplePayload samplePayload=new SamplePayload();
//        samplePayload.setTime(metricUIM.getSampletime());
            samplePayload.setTime(sampleTime);
            samplePayload.setEpochtime(epoc);
            String rate= db_samplerate;
            float frate= Float.parseFloat(rate);
            samplePayload.setRate(frate);
//            logger.debug("GRAFANA samplevalue :"+ metricUIM.get("samplevalue"));
            String value= db_samplevalue;
            float fvalue = Float.parseFloat(value);
            samplePayload.setValue(fvalue);
            metricUIMPayloadList.get(index-1).getSample().add(samplePayload);
//            if(index<5){
//                logger.debug("getTime :"+samplePayload.getTime());
//                logger.debug("getEpochtime :"+samplePayload.getEpochtime());
//                logger.debug("getValue :"+samplePayload.getValue());
//            }
//        logger.debug(index+" | createChildNode : ");
//        logger.debug("###########################################");
//        logger.debug("");
        }
    }

    private List<MetricUIMPayload>   buildMetricUIMResponse(ResultSet metricUIMList,
                                                            List<MetricUIMPayload> metricUIMPayloadList,
                                                            MetricUIMPayload metricUIMPayload,
                                                            int node, boolean showSample) throws SQLException {
        String source="";
        String target="";
        String origin="";
        int parentNode=0;
        while (metricUIMList.next()) {
//            String db_origin=metricUIMList.getString("origin");
//            String db_source=metricUIMList.getString("source");
//            String db_target=metricUIMList.getString("target");
            if(metricUIMList.getString("origin").equals(origin)) {
                if (metricUIMList.getString("source").equals(source)) {
                    if (metricUIMList.getString("target").equals(target)) {
////                        logger.debug("-------------------createChildNode------------------------");
                        createChildNode(metricUIMPayloadList,metricUIMList.getString("sampletime"),
                                metricUIMList.getString("samplerate"),
                                metricUIMList.getString("samplevalue"),parentNode,showSample);
                    } else {
//                       logger.debug(parentNode+" | New Target: "+ target );
                        metricUIMPayload=createParentNode(  metricUIMList.getString("sampletime"),
                                metricUIMList.getString("samplerate"),
                                metricUIMList.getString("samplevalue"),
                                metricUIMList.getString("origin"),
                                metricUIMList.getString("source"),
                                metricUIMList.getString("target"),
                                metricUIMList.getString("probe"),
                                metricUIMList.getString("qos"),
                                metricUIMList.getString("table_id"),showSample);
                        metricUIMPayloadList.add(metricUIMPayload);
                        parentNode++;
                    }
                } else {
//                    logger.debug(parentNode + " | New Source: " + source);
                    metricUIMPayload=createParentNode(  metricUIMList.getString("sampletime"),
                            metricUIMList.getString("samplerate"),
                            metricUIMList.getString("samplevalue"),
                            metricUIMList.getString("origin"),
                            metricUIMList.getString("source"),
                            metricUIMList.getString("target"),
                            metricUIMList.getString("probe"),
                            metricUIMList.getString("qos"),
                            metricUIMList.getString("table_id"),showSample);
                    metricUIMPayloadList.add(metricUIMPayload);
                    parentNode++;
                }
                origin=metricUIMList.getString("origin");
                source=metricUIMList.getString("source");
                target=metricUIMList.getString("target");
            }else{
                origin=metricUIMList.getString("origin");
                source=metricUIMList.getString("source");
                target=metricUIMList.getString("target");
                metricUIMPayload=createParentNode(  metricUIMList.getString("sampletime"),
                        metricUIMList.getString("samplerate"),
                        metricUIMList.getString("samplevalue"),
                        metricUIMList.getString("origin"),
                        metricUIMList.getString("source"),
                        metricUIMList.getString("target"),
                        metricUIMList.getString("probe"),
                        metricUIMList.getString("qos"),
                        metricUIMList.getString("table_id"),showSample);
                metricUIMPayloadList.add(metricUIMPayload);
                parentNode++;
            }
        }

//        for (int i = 0 ; i < metricUIMList.size(); i++) {
//            JSONObject metric = (JSONObject) metricUIMList.get(i);
//            if(metric.get("origin").toString().equals(origin)){
////                logger.debug("count Sample Data : "+metricUIMPayloadList.get(parentNode).getSample().size());
////                logger.debug("count Sample Data : "+metricUIMPayloadList.size());
//                if(metric.get("source").toString().equals(source)){
//                    if(metric.get("target").toString().equals(target)){
////                        logger.debug("-------------------createChildNode------------------------");
//                        createChildNode(metricUIMPayloadList,metric,parentNode,showSample);
//                    }else{
////                       logger.debug(parentNode+" | New Target: "+ target );
//                        metricUIMPayload=createParentNode(metric,showSample);
//                        metricUIMPayloadList.add(metricUIMPayload);
//                        parentNode++;
//                    }
//                }else{
////                    logger.debug(parentNode+" | New Source: "+ source);
//                            metricUIMPayload=createParentNode(metric,showSample);
//                    metricUIMPayloadList.add(metricUIMPayload);
//                    parentNode++;
//                }
//                origin=metric.get("origin").toString();
//                source=metric.get("source").toString();
//                target=metric.get("target").toString();
//
//            }else{
//                origin=metric.get("origin").toString();
//                source=metric.get("source").toString();
//                target=metric.get("target").toString();
//                metricUIMPayload=createParentNode(metric,showSample);
//                metricUIMPayloadList.add(metricUIMPayload);
//                parentNode++;
//            }
//        }

        return metricUIMPayloadList;
    }

    public List<MetricUIMPayload> getMetricUIMByHideSample(MetricParameter params, int proccessID) throws  Exception {
//        logger.debug("### PROCESS ID :" + proccessID + " | getMetricUIMByHideSample START ####################################################");
        MetricUIMPayload metricUIMPayload=null;
//        List<MetricUIM> metricUIMList=new ArrayList<MetricUIM>();
        List<MetricUIMPayload> metricUIMPayloadList=new ArrayList<MetricUIMPayload>();
        ResultSet metricUIMList = null;

//        logger.debug("buildMetricUIMResponse Collection");
        String sql="";
        String[] sourceList=null;
        String source="";
        String qos="";
        if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active")))
            sql= MetricUIMOracleSQLStatement.findQOS_Data;
        else
            sql= MetricUIMSQLStatement.findQOS_Data;

//        Mapping Source
        if(params.getId().equals("")){
            sql=sql.replace("$Source","");
        }else{
            sourceList=params.getId().split(",");
            if(sourceList.length<=1 && sourceList[0].contains("--")){
                sql=sql.replace("$Source","");
            }else{
                for(int i=0;i<sourceList.length;i++){
                    source=source+"'"+sourceList[i]+"',";
                }
                source=source.substring(0,source.length()-1);
                sql=sql.replace("$Source","source in("+source+")");
            }

        }

        if(params.getMetricFilter().equals("")){
            sql=sql.replace("$QOS","");
        }else{
            sql=sql.replace("$QOS"," and qos='"+params.getMetricFilter()+"'");
        }
//        logger.debug("buildMetricUIMResponse SQL : "+sql);
        try{
            metricUIMList=metricUIMDAO.getQOSUIMCollection(sql,metricUIMList,proccessID+" | getMetricUIMByHideSample");
            //        logger.debug("buildMetricUIMResponse Collection :"+metricUIMList);
            metricUIMPayloadList=buildMetricUIMResponse(metricUIMList,metricUIMPayloadList,metricUIMPayload,0,
                    Boolean.valueOf(params.getShowSamples()));

//        logger.debug("### PROCESS ID :" + proccessID + " | Collection Data :"+metricUIMPayloadList.size());
//        logger.debug("### PROCESS ID :" + proccessID + " | getMetricUIMByHideSample FINISH ####################################################");
//        logger.debug("");
        }catch(Exception e){
            LoggingException.log(e, logger2, logger, "PROCESS ID :" + proccessID + " getQOSList ERROR : ");
        }
        finally {
            metricUIMList.close();
        }

        return metricUIMPayloadList;
    }

    public List<MetricUIMPayload> getMetricUIMByShowSample(MetricParameter params,int proccessID) throws SQLException, InterruptedException {
//        logger.debug("### PROCESS ID :" + proccessID + " | getMetricUIMByShowSample FINISH ####################################################");
        MetricUIMPayload metricUIMPayload=null;
//        List<MetricUIM> metricUIMList=new ArrayList<MetricUIM>();
        List<MetricUIMPayload> metricUIMPayloadList=new ArrayList<MetricUIMPayload>();
        List<QOSTableMapper>   qosTableMapperList=null;
        ResultSet metricUIMList = null;


        qosTableMapperList= metricUIMDAO.getQOSList(params,proccessID+" | getMetricUIMByShowSample"); // Tentative Belum diconvert JSON


        if(qosTableMapperList.size()>0){
            logger.debug("PROCESS ID :" + proccessID +" | SINGLE QUERY METRIC :"+ qosTableMapperList.size()+" | "+
                    qosTableMapperList.get(0).getrTable());
            String sql = metricUIMDAO.buildQueryQOSUIM(qosTableMapperList.get(0).getrTable(),
                    qosTableMapperList.get(0).gethTable(),
                    qosTableMapperList.get(0).getdTable(),
                    qosTableMapperList.get(0).getQos(),
                    qosTableMapperList.get(0).getTarget(),
                    params,proccessID+" | getMetricUIMByShowSample");
//            logger.debug("buildMetricUIMResponse Collection SQL :"+sql);
            try {
                metricUIMList=metricUIMDAO.getQOSUIMCollection(sql,metricUIMList,proccessID+" | getMetricUIMByShowSample"); // Tentative Belum diconvert JSON
                metricUIMPayloadList=buildMetricUIMResponse(metricUIMList,metricUIMPayloadList,metricUIMPayload,0,
                        Boolean.valueOf(params.getShowSamples()));
            } catch (Exception e) {
                LoggingException.log(e, logger2, logger, "PROCESS ID :" + proccessID + " ERROR : ");
//                e.printStackTrace();
            }finally {
                metricUIMList.close();
            }

        }


//        logger.debug("### PROCESS ID :" + proccessID + " | Collection Data :"+metricUIMPayloadList.size());
//        logger.debug("### PROCESS ID :" + proccessID + " | getMetricUIMByShowSample FINISH ####################################################");
//        logger.debug("");
        return metricUIMPayloadList;
    }
}
