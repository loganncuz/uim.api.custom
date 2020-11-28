package com.ncuz.uim.service;

import com.ncuz.uim.dao.MetricUIMDAO;
import com.ncuz.uim.entity.MetricParameter;
import com.ncuz.uim.entity.MetricUIM;
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
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MetricUIMService {
    @Autowired
    Log4jService log4jService;

    @Autowired
    MetricUIMDAO metricUIMDAO;

    @Autowired
    PropertiesUtility propertiesUtility;


    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;
    @PostConstruct
    private void post() {
        System.out.println("POST MetricUIMService" );
        logger=log4jService.getLogger(MetricUIMService.class,"file","../log_app/uim.api.custom.MetricUIMService");
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.api.custom.Exception");
    }


    private List<MetricUIMPayload>   buildMetricUIMResponse(List<MetricUIM> metricUIMList,
                                                            List<MetricUIMPayload> metricUIMPayloadList,
                                                            MetricUIMPayload metricUIMPayload,
                                                            int node, boolean showSample){
        String source="";
        String target="";
        String origin="";
        int parentNode=0;
        for (MetricUIM metricUIM : metricUIMList) {
            if(metricUIM.getOrigin().equals(origin)){
                //logger.debug("count Sample Data : "+metricUIMPayloadList.get(parentNode).getSample().size());
                //logger.debug("count Sample Data : "+metricUIMPayloadList.size());
                if(metricUIM.getSource().equals(source)){
                    if(metricUIM.getTarget().equals(target)){
//                        logger.debug("-------------------createChildNode------------------------");
                        createChildNode(metricUIMPayloadList,metricUIM,parentNode,showSample);
                    }else{
//                       logger.debug(parentNode+" | New Target: "+ target +" | metricUIM.getOrigin() :"+metricUIM.getOrigin()
//                               +" | metricUIM.getSource() :"+metricUIM.getSource()
//                               +" | metricUIM.getTarget() :"+metricUIM.getTarget());
                        metricUIMPayload=createParentNode(metricUIM,showSample);
                        metricUIMPayloadList.add(metricUIMPayload);
                        parentNode++;
                    }

                }else{
//                   logger.debug(parentNode+" | New Source: "+ source +" | metricUIM.getOrigin() :"+metricUIM.getOrigin()
//                           +" | metricUIM.getSource() :"+metricUIM.getSource()
//                           +" | metricUIM.getTarget() :"+metricUIM.getTarget());
                    metricUIMPayload=createParentNode(metricUIM,showSample);
                    metricUIMPayloadList.add(metricUIMPayload);
                    parentNode++;
                }
                origin=metricUIM.getOrigin();
                source=metricUIM.getSource();
                target=metricUIM.getTarget();
            }else
            {
//                   logger.debug(parentNode+" | New Origin: "+ origin +" | metricUIM.getOrigin() :"+metricUIM.getOrigin()
//                           +" | metricUIM.getSource() :"+metricUIM.getSource()
//                           +" | metricUIM.getTarget() :"+metricUIM.getTarget());
                origin=metricUIM.getOrigin();
                source=metricUIM.getSource();
                target=metricUIM.getTarget();
                metricUIMPayload=createParentNode(metricUIM,showSample);
                metricUIMPayloadList.add(metricUIMPayload);
                parentNode++;
            }
        }
        return metricUIMPayloadList;
    }

    public List<MetricUIMPayload> getMetricUIM(MetricParameter params, String nameofCurrMethod) throws SQLException, InterruptedException{
        return getMetricUIM(params);
    }

    public List<MetricUIMPayload> getMetricUIM(MetricParameter params) throws SQLException, InterruptedException {
        QOSTableMapper qosTableMapper= metricUIMDAO.getQOSTableMapping(params);
//        logger.debug("getMetricUIM Target : "+ params.getTarget()+" | Source :"+params.getId()+
//                " | Start Period"+params.getStartPeriod()+" | "+params.getEndPeriod());
        logger.debug("qosTableMapper :"+qosTableMapper.getrTable());
        MetricUIMPayload metricUIMPayload=null;
        List<MetricUIMPayload> metricUIMPayloadList=new ArrayList<MetricUIMPayload>();

        if(qosTableMapper.getrTable()==null){
            return getMetricUIMBadRequest("NO QOS FOUND");
        }
        List<MetricUIM> metricUIMList=metricUIMDAO.getQOSUIMList(
                qosTableMapper.getrTable(),
                qosTableMapper.gethTable(),
                qosTableMapper.getdTable(),
                params
        );

//    logger.debug("getMetricUIM: "+ qosTableMapper.getrTable() +" | metricUIMList :"+metricUIMList.size());

        logger.debug("buildMetricUIMResponse");
        metricUIMPayloadList=buildMetricUIMResponse(metricUIMList,metricUIMPayloadList,metricUIMPayload,0,
                Boolean.valueOf(params.getShowSamples()));

        logger.debug("buildMetricUIMResponse Data:"+metricUIMList.size()+" | metricUIMPayloadList Node :"+metricUIMPayloadList.size());
        try{
            return metricUIMPayloadList;
        }finally {
            metricUIMPayloadList=null;
            metricUIMPayload=null;
//            System.gc();
        }
//        return metricUIMPayloadList;
    }

    public List<MetricUIMPayload> getMetricUIMByHideSample(MetricParameter params) throws SQLException, InterruptedException {
        MetricUIMPayload metricUIMPayload=null;
        List<MetricUIM> metricUIMList=new ArrayList<MetricUIM>();
        List<MetricUIMPayload> metricUIMPayloadList=new ArrayList<MetricUIMPayload>();

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
        metricUIMList=metricUIMDAO.getQOSUIMCollection(sql,metricUIMList);
//        logger.debug("buildMetricUIMResponse Collection");
        metricUIMPayloadList=buildMetricUIMResponse(metricUIMList,metricUIMPayloadList,metricUIMPayload,0,
                Boolean.valueOf(params.getShowSamples()));

//        logger.debug("buildMetricUIMResponse Collection Data:"+metricUIMList.size()+" | metricUIMPayloadList Node :"+metricUIMPayloadList.size());
        return metricUIMPayloadList;
    }

    public List<MetricUIMPayload> getMetricUIMByShowSample(MetricParameter params) throws SQLException, InterruptedException {
        MetricUIMPayload metricUIMPayload=null;
        List<MetricUIM> metricUIMList=new ArrayList<MetricUIM>();
        List<MetricUIMPayload> metricUIMPayloadList=new ArrayList<MetricUIMPayload>();
        List<QOSTableMapper>   qosTableMapperList=null;


        qosTableMapperList= metricUIMDAO.getQOSList(params);


        if(qosTableMapperList.size()>0){
            logger.debug("SINGLE QUERY METRIC :"+ qosTableMapperList.size()+" | "+
                    qosTableMapperList.get(0).getrTable());
            String sql = metricUIMDAO.buildQueryQOSUIM(qosTableMapperList.get(0).getrTable(),
                    qosTableMapperList.get(0).gethTable(),
                    qosTableMapperList.get(0).getdTable(),
                    qosTableMapperList.get(0).getQos(),
                    qosTableMapperList.get(0).getTarget(),
                    params);
            logger.debug("buildMetricUIMResponse Collection SQL :"+sql);
            metricUIMList=metricUIMDAO.getQOSUIMCollection(sql,metricUIMList);
            metricUIMPayloadList=buildMetricUIMResponse(metricUIMList,metricUIMPayloadList,metricUIMPayload,0,
                    Boolean.valueOf(params.getShowSamples()));
        }


        logger.debug("buildMetricUIMResponse Collection Data:"+metricUIMList.size()+" | metricUIMPayloadList Node :"+metricUIMPayloadList.size());
        return metricUIMPayloadList;
    }

    public List<MetricUIMPayload> getMetricUIMByParametersx(MetricParameter params) throws SQLException, InterruptedException {
        MetricUIMPayload metricUIMPayload=null;
        List<MetricUIM> metricUIMList=new ArrayList<MetricUIM>();
        List<MetricUIMPayload> metricUIMPayloadList=new ArrayList<MetricUIMPayload>();
        List<QOSTableMapper>   qosTableMapperList=null;


        qosTableMapperList= metricUIMDAO.getQOSList(params);
         if(qosTableMapperList.size()==0){
             logger.debug("ERROR FOUND");
             return getMetricUIMBadRequest("NO QOS FOUND");
         }else
         if(qosTableMapperList.size()>1){
             logger.debug("MULTI QUERY METRIC");
             String listquery="";
             if(qosTableMapperList.get(0).getType()==1){
                 logger.debug("MULTI QUERY METRIC TYPE 1");
                 String target="";
                 for(int i=0;i<qosTableMapperList.size();i++){
                     if(i==qosTableMapperList.size()-1)
                         target=target+qosTableMapperList.get(i).getTarget();
                     else
                         target=target+qosTableMapperList.get(i).getTarget()+",";

                 }
                 listquery = metricUIMDAO.buildQueryQOSUIM(
                         qosTableMapperList.get(0).getrTable(),
                         qosTableMapperList.get(0).gethTable(),
                         qosTableMapperList.get(0).getdTable(),
                         qosTableMapperList.get(0).getQos(),
                         target,
                         params);
                 logger.debug("QUERY : "+listquery);
                 metricUIMList=metricUIMDAO.getQOSUIMCollection(listquery,metricUIMList);
             }
            else{
                 // Map<Integer,String> listquery=new HashMap<Integer,String>();
                 for(int i=0;i<qosTableMapperList.size();i++){
                     logger.debug("qosTableMapperList :"+i+" | "+qosTableMapperList.get(i).getrTable());
                     listquery = metricUIMDAO.buildQueryQOSUIM(
                             qosTableMapperList.get(i).getrTable(),
                             qosTableMapperList.get(i).gethTable(),
                             qosTableMapperList.get(i).getdTable(),
                             qosTableMapperList.get(i).getQos(),
                             qosTableMapperList.get(i).getTarget(),
                             params);
//                     if(i==qosTableMapperList.size()-1)
//                         listquery=listquery+sql;
//                     else
//                         listquery=listquery+sql+" union ";
                     logger.debug("QUERY : "+listquery);
//                     metricUIMList=metricUIMDAO.getQOSUIMCollection(listquery,metricUIMList);
                     logger.debug("metricUIMList.size() :"+metricUIMList.size());
                 }
             }


         }else{
             logger.debug("SINGLE QUERY METRIC :"+ qosTableMapperList.get(0).getQos());
             String sql = metricUIMDAO.buildQueryQOSUIM(qosTableMapperList.get(0).getrTable(),
                     qosTableMapperList.get(0).gethTable(),
                     qosTableMapperList.get(0).getdTable(),
                     qosTableMapperList.get(0).getQos(),
                     qosTableMapperList.get(0).getTarget(),
                     params);
             metricUIMList=metricUIMDAO.getQOSUIMCollection(sql,metricUIMList
                 );
         }

         logger.debug("qosTableMapperList "+qosTableMapperList.size()+" | metricUIMListCollection :"+metricUIMList.size());

//             List<MetricUIM> metricUIMList=metricUIMDAO.getQOSUIMList(
//                qosTableMapper.getrTable(),
//                qosTableMapper.gethTable(),
//                qosTableMapper.getdTable(),
//                params
//        );

//    logger.debug("getMetricUIM: "+ qosTableMapper.getrTable() +" | metricUIMList :"+metricUIMList.size());

        logger.debug("buildMetricUIMResponse Collection");
        metricUIMPayloadList=buildMetricUIMResponse(metricUIMList,metricUIMPayloadList,metricUIMPayload,0,
                Boolean.valueOf(params.getShowSamples()));

        logger.debug("buildMetricUIMResponse Collection Data:"+metricUIMList.size()+" | metricUIMPayloadList Node :"+metricUIMPayloadList.size());
//        return metricUIMPayloadList;
        try{
            return metricUIMPayloadList;
        }finally {
            metricUIMPayloadList=null;
            metricUIMPayload=null;
            metricUIMList=null;
//            System.gc();
        }

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

    private MetricUIMPayload createParentNode(MetricUIM metricUIM,boolean showSample){
//        logger.debug("-------------------createParentNode------------------------");
//        logger.debug("createParentNode :"+metricUIM.getOrigin());
//        logger.debug("createParentNode Timestamp :"+metricUIM.getSampletime());
//        logger.debug("sampletime PARENT :"+metricUIM.getSampletime());
        MetricUIMPayload metricUIMPayload=new MetricUIMPayload(false);
        //java.sql.Timestamp ts = java.sql.Timestamp.valueOf(metricUIM.getSampletime());
         long epoc = 0;
         String sampleTime=null;
        if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"))) {
            epoc = getEpocTime("yyyy-MM-dd hh:mm:ss.SSS", metricUIM.getSampletime(), "yyyy-MM-dd hh:mm:ss.SSS");
            sampleTime=getGrafanaDateFormat("yyyy-MM-dd hh:mm:ss.SSS", metricUIM.getSampletime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        }   else{
            epoc=getEpocTime("dd-MMM-yy HH.mm.ss",metricUIM.getSampletime(),"yyyy-MM-dd hh:mm:ss.SSS");
            sampleTime=getGrafanaDateFormat("dd-MMM-yy HH.mm.ss",metricUIM.getSampletime(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        }

//        logger.debug("GRAFANA FORMAT DATE :"+sampleTime);
        List<SamplePayload> samplePayloadList=new ArrayList<SamplePayload>();
        SamplePayload samplePayload=new SamplePayload();
        if(showSample){
//        samplePayload.setTime(metricUIM.getSampletime());
            samplePayload.setTime(sampleTime);
            samplePayload.setEpochtime(epoc);
            samplePayload.setRate(metricUIM.getSamplerate());
            samplePayload.setValue(metricUIM.getSamplevalue());
//            logger.debug("getTime :"+samplePayload.getTime());
//            logger.debug("getEpochtime :"+samplePayload.getEpochtime());
//            logger.debug("getValue :"+samplePayload.getValue());
        }


        metricUIMPayload.setOrigin(metricUIM.getOrigin());
        metricUIMPayload.setId(metricUIM.getTable_id());
        metricUIMPayload.setSource(metricUIM.getSource());
        metricUIMPayload.setTarget(metricUIM.getTarget());
        metricUIMPayload.setProbe(metricUIM.getProbe());
        metricUIMPayload.getFor_configuration_item().setQosName(metricUIM.getQosName());


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

    private void createChildNode(List<MetricUIMPayload> metricUIMPayloadList,MetricUIM metricUIM,int index,boolean showSample){
//        logger.debug("###################createChildNode########################"+index);
//        logger.debug(index+" | createChildNode : "+metricUIMPayloadList.get(index-1).getSample().size());
        long epoc = 0;

        String sampleTime=null;
        if(showSample){
//            logger.debug("sampletime CHILD :"+metricUIM.getSampletime());
            if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"))) {
                epoc = getEpocTime("yyyy-MM-dd HH:mm:ss.SSS", metricUIM.getSampletime(), "yyyy-MM-dd hh:mm:ss.SSS");
                sampleTime=getGrafanaDateFormat("yyyy-MM-dd HH:mm:ss.SSS", metricUIM.getSampletime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            }   else{
                epoc=getEpocTime("dd-MMM-yy HH.mm.ss",metricUIM.getSampletime(),"yyyy-MM-dd hh:mm:ss.SSS");
                sampleTime=getGrafanaDateFormat("dd-MMM-yy HH.mm.ss",metricUIM.getSampletime(),"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            }

//        logger.debug("GRAFANA FORMAT DATE :"+sampleTime);

            List<SamplePayload> samplePayloadList=new ArrayList<SamplePayload>();
            SamplePayload samplePayload=new SamplePayload();
//        samplePayload.setTime(metricUIM.getSampletime());
            samplePayload.setTime(sampleTime);
            samplePayload.setEpochtime(epoc);
            samplePayload.setRate(metricUIM.getSamplerate());
            samplePayload.setValue(metricUIM.getSamplevalue());
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

    public List<MetricUIMPayload> getMetricUIMBadRequest(String nameofCurrMethod){
        List<MetricUIMPayload> responseList=new ArrayList<MetricUIMPayload>();
        MetricUIMPayload metricUIMPayload = new MetricUIMPayload(true);
//        metricUIMPayload.getResponse().setService(this.getClass().getName() + nameofCurrMethod);
//        metricUIMPayload.getResponse().setMessage("Not Found Query String From Request");
        responseList.add(metricUIMPayload);
//        logger.debug("getMetricUIMBadRequest: "+ responseList );
//        return responseList;
        try{
            return responseList;
        }finally {
            responseList=null;
            metricUIMPayload=null;
//            System.gc();
        }

    }
}
