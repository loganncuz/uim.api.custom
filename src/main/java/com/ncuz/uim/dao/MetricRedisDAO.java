package com.ncuz.uim.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ncuz.exception.LoggingException;
import com.ncuz.uim.base.RedisConnection;
import com.ncuz.uim.entity.MetricParameter;
import com.ncuz.uim.entity.QOSTableMapper;
import com.ncuz.uim.payload.tso.MetricUIMPayload;
import com.ncuz.uim.service.RedisService;
import com.ncuz.uim.utility.PropertiesUtility;
import com.ncuz.uim.utility.RedisUtility;
import log4j.helper.service.Log4jService;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

@Service
public class MetricRedisDAO {
    @Autowired
    Log4jService log4jService;
    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;
    @Autowired
    RedisService redisService;
    @Autowired
    PropertiesUtility propertiesUtility;
    @Autowired
    RedisUtility redisUtility;
    private
    RedisConnection redisConnection;
    @PostConstruct
    private void post() throws SQLException {
        logger=log4jService.getLogger(MetricRedisDAO.class,"file","../log_app/uim.api.custom.MetricRedisDAO");
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.api.custom.Exception");
        redisConnection =redisUtility.createInstance(propertiesUtility.getRedisDataSourceProperties().getProperty("datasource.primary.active"));
        logger.debug("POST AlarmDAO datasource.provider.active : "+propertiesUtility.getRedisDataSourceProperties().getProperty("datasource.provider.active"));
        logger.debug("POST AlarmDAO datasource.provider.active : "+propertiesUtility.getRedisDataSourceProperties().getProperty("datasource.primary.active"));
    }
    @PreDestroy
    private void destroy()   {
        redisConnection=null;
        logger=null;
        logger2=null;
    }
    private String inputQOSListPattern="qos-list-input::";
//    private String dataQOSListPattern="qos-list-data|";
    private String inputMetricPattern="metric-input::";
//    private String dataQOSPattern="qos-data|";

    public boolean searchMetricInput(MetricParameter params, int proccessID) throws Exception {
        String period=redisService.ConvertDateStamp(params.getPeriod());
        Jedis connection = redisConnection.getConnection();
        ScanParams scanParams = new ScanParams().count(10000).match(inputMetricPattern+"*");
        String cur = "";
        boolean isFound=false;
        boolean cycleIsFinished = false;
        try{
            while(!cycleIsFinished) {
                ScanResult<String> scanResult = connection.scan(cur, scanParams);

                if(scanResult.getResult().size()>0){
//                logger.debug("RESULT :"+scanResult.getResult());
                    for (String key : scanResult.getResult()) {
                        if(key.equals(inputMetricPattern+period+
                                "#"+params.getId()+"#"+params.getMetricFilter()+
                                "#"+params.getTarget()+"#"+params.getShowSamples())){
                            logger.debug("KEY FOUND "+key);
                            isFound=true;
                            cycleIsFinished = true;
                        }
                    }
                }
                cur = scanResult.getCursor();
                if (cur.equals("0")) {
                    cycleIsFinished = true;
                }
            }

        }catch(Exception e){
            LoggingException.log(e, logger2, logger, "PROCESS ID : "+proccessID+" | SCAN METRIC ERROR REDIS :"+e);
            logger.error("### PROCESS ID :" +
                    proccessID +
                    " | ERROR SCAN METRIC REDIS :"+e);
            redisConnection.releaseConnection(connection);
        }finally {
            redisConnection.releaseConnection(connection);
        }

//        return isFound=false;
//        connection.close();

        return isFound;
    }

    public void saveMetricInput(MetricParameter params, int proccessID, List<MetricUIMPayload> data) throws Exception {
        String period=redisService.ConvertDateStamp(params.getPeriod());

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(data);
//        logger.debug("DATA :"+json);
        Jedis connection = redisConnection.getConnection();
        try{
//            logger.debug("KEY :"+
//                    inputQOSListPattern+period+
//                    "#"+params.getId()+"#"+params.getMetricFilter()+
//                    "#"+params.getTarget()+"#"+params.getShowSamples());
//            logger.debug("VALUE :"+json);
            connection.set(inputMetricPattern+period+
                    "#"+params.getId()+"#"+params.getMetricFilter()+
                    "#"+params.getTarget()+"#"+params.getShowSamples(), json);
        }catch(Exception e){
            LoggingException.log(e, logger2, logger, "PROCESS ID : "+proccessID+" | POST METRIC ERROR REDIS :"+e);
            logger.error("### PROCESS ID :" +
                    proccessID +
                    " | ERROR POST METRIC REDIS :"+e);
            redisConnection.releaseConnection(connection);
        }finally {
            redisConnection.releaseConnection(connection);
        }


    }

    public List<MetricUIMPayload> getMetric(MetricParameter params, int proccessID) throws Exception {
        String period=redisService.ConvertDateStamp(params.getPeriod());
        Jedis connection = redisConnection.getConnection();
        ScanParams scanParams = new ScanParams().count(1000000).match(inputMetricPattern+"*");
        String cur = "";
        List<MetricUIMPayload> result = null;
        Gson gson = new Gson();
        Type listType = new TypeToken<List<MetricUIMPayload>>(){}.getType();
        boolean cycleIsFinished = false;
        try{
            while(!cycleIsFinished) {
                ScanResult<String> scanResult = connection.scan(cur, scanParams);
                if(scanResult.getResult().size()>0) {
//                    logger.debug("SCAN RESULT :" + scanResult.getResult());
                    for (String key : scanResult.getResult()) {
                        String[] keyvalid=key.split("::");

                        if(keyvalid[1].equals(period+
                                "#"+params.getId()+"#"+params.getMetricFilter()+
                                "#"+params.getTarget()+"#"+params.getShowSamples())){
                            logger.debug("SCAN KEY VALID :" + keyvalid[1]);

                            try{
                                result = gson.fromJson(connection.get(key),listType);
//                                logger.debug("Origin VALUE :"+result.get(0).getQos());
//                                logger.debug("Origin VALUE :"+connection.get(key));

                            }catch(Exception e){
                                LoggingException.log(e, logger2, logger, "PROCESS ID : "+proccessID+" | Get Key ERROR METRIC REDIS :"+e);
                                logger.error("### PROCESS ID :" +
                                        proccessID +
                                        " | ERROR GET METRIC REDIS :"+e);
                            }
                        }
                    }
                }

                cur = scanResult.getCursor();
                if (cur.equals("0")) {
                    cycleIsFinished = true;
                }
            }

        }catch(Exception e){
            LoggingException.log(e, logger2, logger, "PROCESS ID : "+proccessID+" | SCAN ERROR METRIC REDIS :"+e);
            logger.error("### PROCESS ID :" +
                    proccessID +
                    " | SCAN ERROR METRIC REDIS :"+e);
            redisConnection.releaseConnection(connection);

        }finally{
            redisConnection.releaseConnection(connection);
            gson=null;
            listType=null;
        }
        return result;
    }

    public boolean searchQOSList(MetricParameter params, int proccessID) throws Exception {
        String period=redisService.ConvertDateStamp(params.getPeriod());
        Jedis connection = redisConnection.getConnection();
        ScanParams scanParams = new ScanParams().count(10000).match(inputQOSListPattern+"*");
        String cur = "";
        boolean isFound=false;
        boolean cycleIsFinished = false;
        try{
            while(!cycleIsFinished) {
                ScanResult<String> scanResult = connection.scan(cur, scanParams);

                if(scanResult.getResult().size()>0){
//                logger.debug("RESULT :"+scanResult.getResult());
                    for (String key : scanResult.getResult()) {
                        if(key.equals(inputQOSListPattern+period+
                                "#"+params.getId()+"#"+params.getMetricFilter()+
                                "#"+params.getTarget()+"#"+params.getShowSamples())){
                            logger.debug("KEY FOUND "+key);
                            isFound=true;
                            cycleIsFinished = true;
                        }
                    }
                }
                cur = scanResult.getCursor();
                if (cur.equals("0")) {
                    cycleIsFinished = true;
                }
            }

        }catch(Exception e){
            LoggingException.log(e, logger2, logger, "PROCESS ID : "+proccessID+" | SCAN QOS LIST ERROR REDIS :"+e);
            logger.error("### PROCESS ID :" +
                    proccessID +
                    " | ERROR SCAN QOS LIST REDIS :"+e);
            redisConnection.releaseConnection(connection);
        }finally {
            redisConnection.releaseConnection(connection);
        }

//        return isFound=false;
//        connection.close();

        return isFound;
    }
    public void saveQOSListInput(MetricParameter params, int proccessID, List<QOSTableMapper> data) throws Exception {
        String period=redisService.ConvertDateStamp(params.getPeriod());

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(data);
//        logger.debug("DATA :"+json);
        Jedis connection = redisConnection.getConnection();
        try{
//            logger.debug("KEY :"+
//                    inputQOSListPattern+period+
//                    "#"+params.getId()+"#"+params.getMetricFilter()+
//                    "#"+params.getTarget()+"#"+params.getShowSamples());
//            logger.debug("VALUE :"+json);
            connection.set(inputQOSListPattern+period+
                    "#"+params.getId()+"#"+params.getMetricFilter()+
                    "#"+params.getTarget()+"#"+params.getShowSamples(), json);
        }catch(Exception e){
            LoggingException.log(e, logger2, logger, "PROCESS ID : "+proccessID+" | POST QOS LIST ERROR REDIS :"+e);
            logger.error("### PROCESS ID :" +
                    proccessID +
                    " | ERROR POST QOS LIST REDIS :"+e);
            redisConnection.releaseConnection(connection);
        }finally {
            redisConnection.releaseConnection(connection);
        }


    }

    public List<QOSTableMapper>  getQOSListInput(MetricParameter params, int proccessID) throws Exception {
        String period=redisService.ConvertDateStamp(params.getPeriod());
        Jedis connection = redisConnection.getConnection();
        ScanParams scanParams = new ScanParams().count(1000000).match(inputQOSListPattern+"*");
        String cur = "";
        List<QOSTableMapper> result = null;
        Gson gson = new Gson();
        Type listType = new TypeToken<List<QOSTableMapper>>(){}.getType();
        boolean cycleIsFinished = false;
        try{
            while(!cycleIsFinished) {
                ScanResult<String> scanResult = connection.scan(cur, scanParams);
                if(scanResult.getResult().size()>0) {
//                    logger.debug("SCAN RESULT :" + scanResult.getResult());
                    for (String key : scanResult.getResult()) {
                        String[] keyvalid=key.split("::");

                        if(keyvalid[1].equals(period+
                                "#"+params.getId()+"#"+params.getMetricFilter()+
                                "#"+params.getTarget()+"#"+params.getShowSamples())){
//                            logger.debug("SCAN KEY VALID :" + keyvalid[1]);

                            try{
                                result = gson.fromJson(connection.get(key),listType);
//                                logger.debug("Origin VALUE :"+result.get(0).getQos());
//                                logger.debug("Origin VALUE :"+connection.get(key));

                            }catch(Exception e){
                                LoggingException.log(e, logger2, logger, "PROCESS ID : "+proccessID+" | Get Key ERROR QOS LIST REDIS :"+e);
                                logger.error("### PROCESS ID :" +
                                        proccessID +
                                        " | ERROR GET QOS LIST REDIS :"+e);
                            }
                        }
                    }
                }

                cur = scanResult.getCursor();
                if (cur.equals("0")) {
                    cycleIsFinished = true;
                }
            }

        }catch(Exception e){
            LoggingException.log(e, logger2, logger, "PROCESS ID : "+proccessID+" | SCAN ERROR REDIS :"+e);
            logger.error("### PROCESS ID :" +
                    proccessID +
                    " | ERROR SCAN REDIS :"+e);
            redisConnection.releaseConnection(connection);

        }finally{
            redisConnection.releaseConnection(connection);
            gson=null;
            listType=null;
        }
        return result;
    }
}
