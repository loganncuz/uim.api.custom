package com.ncuz.uim.service;

import com.ncuz.uim.dao.MetricRedisDAO;
import log4j.helper.service.Log4jService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RedisService {
    @Autowired
    Log4jService log4jService;
    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;
    @PostConstruct
    private void post() throws SQLException {
        logger = log4jService.getLogger(RedisService.class, "file", "../log_app/uim.api.custom.RedisService");
        logger2 = log4jService.getLogger(RuntimeException.class, "file", "../log_app/uim.api.custom.Exception");

    }
    @PreDestroy
    private void destroy()   {
        logger=null;
        logger2=null;
    }
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public String ConvertDateStamp(String dateString){
        String result="";
        String[] dateList=null;
        Date date = null;
        if(dateString.equals("latest")){
            date=new Date();
//            logger.debug("ConvertDate :"+date.toString()+" | "+output.format(date));
            int rounded=Integer.parseInt(env.getProperty("server.redis.interval"));
            int minute = date.getMinutes() % rounded;
            date.setSeconds(0);
            date.setMinutes(date.getMinutes()-minute);
//            logger.debug("ConvertDate :"+date.toString()+" | "+output.format(date));
            result=input.format(date);
        }else{
            dateList=dateString.split("\\|");
            try {
                date = input.parse(dateList[0]);
                int rounded=Integer.parseInt(env.getProperty("server.redis.interval"));
                int minute = date.getMinutes() % rounded;
                date.setSeconds(0);
                date.setMinutes(date.getMinutes()-minute);
//                logger.debug("ConvertDate :"+date.toString()+" | "+output.format(date));
                result=output.format(date)+"|";
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date=null;
            try {
                date = input.parse(dateList[1]);
                int rounded=Integer.parseInt(env.getProperty("server.redis.interval"));
                int minute = date.getMinutes() % rounded;
                date.setSeconds(0);
                date.setMinutes(date.getMinutes()-minute);
//                logger.debug("ConvertDate :"+date.toString()+" | "+output.format(date));
                result=result+output.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
            logger.debug("ConvertDate :"+result);
        return result;
    }
}
