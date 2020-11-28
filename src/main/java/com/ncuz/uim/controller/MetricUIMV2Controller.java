package com.ncuz.uim.controller;

import com.ncuz.exception.LoggingException;
import com.ncuz.uim.entity.MetricParameter;
import com.ncuz.uim.payload.tso.MetricUIMPayload;
import com.ncuz.uim.service.MetricUIMV2Service;
import com.ncuz.uim.utility.CounterUtility;
import io.swagger.annotations.*;
import log4j.helper.service.Log4jService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.ws.rs.Produces;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/tso/api")
@Api(value="onlinestore", description="Operations pertaining to metrics in UIM API Service")
public class MetricUIMV2Controller {
    @Autowired
    MetricUIMV2Service metricUIMService;
    @Autowired
    Log4jService log4jService;

    @Autowired
    CounterUtility counterUtility;

    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;
    private final Random random = new Random();

    @PostConstruct
    private void post() {
        System.out.println("MetricUIMController POST");
        logger = log4jService.getLogger(MetricUIMV2Controller.class, "file", "../log_app/uim.api.custom.MetricUIMV2Controller");
        logger2 = log4jService.getLogger(RuntimeException.class, "file", "../log_app/uim.api.custom.Exception");
    }

    @ApiOperation(value = "View a list of metrics to with selected parameters for produces Data", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("/v2/metrics")
    @Produces("application/json")
    ResponseEntity<List<MetricUIMPayload>> getMetricsV2(
            @RequestHeader MultiValueMap<String, String> headers,
            @ApiParam(value = "id_lookup of parameters to be obtained.", required = false, defaultValue = "by_metric_source")
            @RequestParam(value = "id_lookup", required = false) String id_lookup,
            @ApiParam(value = "id of parameters to be obtained.", required = false, defaultValue = "")
            @RequestParam(name = "id", required = false) String id,
            @ApiParam(value = "metric_type_lookup of parameters to be obtained.", required = false, defaultValue = "")
            @RequestParam(name = "metric_type_lookup", required = false) String metric_type_lookup,
            @ApiParam(value = "metricFilter of parameters to be obtained.", required = false, defaultValue = "")
            @RequestParam(name = "metricFilter", required = false) String metricFilter,
            @ApiParam(value = "target of parameters to be obtained.", required = false, defaultValue = "")
            @RequestParam(name = "target", required = false) String target,
            @ApiParam(value = "period of parameters to be obtained.", required = false, defaultValue = "latest")
            @RequestParam(name = "period", required = false) String period,
            @ApiParam(value = "showSamples of parameters to be obtained.", required = false)
            @RequestParam(name = "showSamples", required = false) String showSamples) throws SQLException, InterruptedException {
        int proccessID = Math.abs(random.nextInt());
        String periodDecode = "";
        String targetDecode = "";
        String idDecode = "";
        List<MetricUIMPayload> responseList=new ArrayList<MetricUIMPayload>();
//        JSONArray responseList = new JSONArray();
        MetricParameter params = null;

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type",
                "application/json");
        responseHeaders.set("charset",
                "utf-8");

        String nameofCurrMethod = new Throwable()
                .getStackTrace()[0]
                .getMethodName();

        headers.forEach((key, value) -> {
//            logger.debug("Key :"+key+" | value :"+value);
//            if (key.equals("authorization"))
//                logger.debug("HEADER PROCESS ID :" + proccessID + " | Key :" + key + " | value :" + value);

        });

//        logger.debug("");
//
//
//        logger.info("REQUEST PROCESS ID :" + proccessID + " | id_lookup :" + id_lookup);
//        logger.info("REQUEST PROCESS ID :" + proccessID + " | id :" + id);
//        logger.info("REQUEST PROCESS ID :" + proccessID + " | metric_type_lookup :" + metric_type_lookup);
//        logger.info("REQUEST PROCESS ID :" + proccessID + " | metricFilter :" + metricFilter);
//        logger.info("REQUEST PROCESS ID :" + proccessID + " | target :" + target);
//        logger.info("REQUEST PROCESS ID :" + proccessID + " | period :" + period);
//        logger.info("REQUEST PROCESS ID :" + proccessID + " | showSamples :" + showSamples + " | " + Boolean.valueOf(showSamples));
//
//        logger.debug("");

        try {
            if (period != null)
                periodDecode = URLDecoder.decode(period, StandardCharsets.UTF_8.toString());
            if (target != null)
                targetDecode = target;
//                targetDecode= URLDecoder.decode(target, StandardCharsets.UTF_8.toString());
            if (id != null)
                idDecode = URLDecoder.decode(id, StandardCharsets.UTF_8.toString());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        params = new MetricParameter(
                id_lookup, idDecode, metric_type_lookup, metricFilter, targetDecode, periodDecode, showSamples
        );

        logger.info("----------------- "+"REQUEST PROCESS ID :" + proccessID +" MetricParameter -------------------------");
        logger.info("REQUEST PROCESS ID :" + proccessID +" | id_lookup :" + params.getId_lookup()
                +" | target origin :" + targetDecode
            +" | id :" + params.getId()
            +" | metric_type_lookup :" + params.getMetric_type_lookup()
            +" | metricFilter :" + params.getMetricFilter()
            +" | target :" + params.getTarget()
                + " | period :" + params.getPeriod()
                +" | showSamples :" + params.getShowSamples());
        logger.info("-------------------"+"REQUEST PROCESS ID :" + proccessID +"-----------------------------------");

            if (Boolean.valueOf(showSamples)) {
                logger.debug("REQUEST PROCESS ID :" + proccessID +"TRUE");
                try {
                    responseList=metricUIMService.getMetricUIMByShowSample(params,proccessID);
                } catch (Exception e) {
                    LoggingException.log(e, logger2, logger, "PROCESS ID :" + proccessID + " getMetricsV2 getMetricUIMByShowSample ERROR : ");
                }


            } else {
                logger.debug("REQUEST PROCESS ID :" + proccessID +"FALSE");
                try {
                    responseList =   metricUIMService.getMetricUIMByHideSample(params,proccessID);
                } catch (Exception e) {
                    LoggingException.log(e, logger2, logger, "PROCESS ID :" + proccessID + " getMetricsV2 getMetricUIMByHideSample ERROR : ");
                }
            }
        int counter=counterUtility.getMetricsCounter("./metrics-counter.txt");
        counter++;
        counterUtility.setMetricsCounter(counter);

//            logger.debug("#######################################################");
//            logger.debug("");

            try {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(responseList);

            } finally {
                logger.debug("RECORD COUNT :"+responseList.size());
                logger.debug("RECORD DETAIL :"+ Arrays.asList(responseList).toArray());
                logger.debug("### PROCESS ID :" + proccessID + " | server.redis.interval : "+env.getProperty("server.redis.interval")+" - REQUEST METRICS FINISH ####################################################");
                logger.debug("");
                periodDecode = null;
                targetDecode = null;
                idDecode = null;
                responseList = null;
                params = null;
                System.gc();

            }


        }
}
