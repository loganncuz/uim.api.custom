package com.ncuz.uim.controller;

import com.ncuz.uim.entity.MetricParameter;
import com.ncuz.uim.payload.tso.MetricUIMPayload;
import com.ncuz.uim.service.MetricUIMService;
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
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/tso/api")
@Api(value="onlinestore", description="Operations pertaining to metrics in UIM API Service")
public class MetricUIMController {
    @Autowired
    MetricUIMService metricUIMService;
    @Autowired
    Log4jService log4jService;

    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;
    @PostConstruct
    private void post() {
        System.out.println("MetricUIMController POST");
        logger=log4jService.getLogger(MetricUIMController.class,"file","../log_app/uim.api.custom.MetricUIMController");
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.api.custom.Exception");
    }

    @ApiOperation(value = "View a list of metrics with all parameters as mandatory for produces Data",response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )




//    @GetMapping("/v1/metrics")
    @Produces("application/json")
    ResponseEntity<List<MetricUIMPayload>> getMetricsV1(
                                                @RequestParam("id_lookup") String id_lookup,
                                                @RequestParam("id") String id,
                                                @RequestParam("metric_type_lookup") String metric_type_lookup,
                                                @RequestParam("metricFilter") String metricFilter,
                                                @RequestParam("target") String target,
                                                @RequestParam("period") String period,
                                                @RequestParam("showSamples") String showSamples) throws SQLException, InterruptedException {

        String periodDecode = null;
        String targetDecode = null;
        String idDecode = null;
        List<MetricUIMPayload> responseList=null;
        MetricParameter params=null;

        logger.debug("############### getMetricsV1 ######################"+period+" | "+params+" | "+responseList);

        try {
            periodDecode= URLDecoder.decode(period, StandardCharsets.UTF_8.toString());
            targetDecode= URLDecoder.decode(target, StandardCharsets.UTF_8.toString());
            idDecode= URLDecoder.decode(id, StandardCharsets.UTF_8.toString());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.debug("REQUEST period:"+period +" | periodDecode :"+periodDecode+
                "id_lookup:"+id_lookup +" | id :"+id+
                "idDecode:"+idDecode +" | metricFilter :"+metricFilter+
                "targetDecode:"+targetDecode +" | showSamples :"+showSamples);

          params=new MetricParameter(
                id_lookup,idDecode,metric_type_lookup,metricFilter,targetDecode,periodDecode, showSamples
        );

//        String nameofCurrMethod = new Throwable()
//                .getStackTrace()[0]
//                .getMethodName();



          responseList=metricUIMService.getMetricUIM(params);
        logger.debug("responseList :"+responseList.size());
        logger.debug("#######################################################");
        logger.debug("");
//        try{
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(responseList);
//
//        }finally{
////              periodDecode = null;
////              targetDecode = null;
////              idDecode = null;
////              responseList=null;
////              params=null;
////            System.gc();
//        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseList);
    }




    @ApiOperation(value = "View a list of metrics to with selected parameters for produces Data",response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @GetMapping("/v3/metrics")
    @Produces("application/json")
    ResponseEntity<List<MetricUIMPayload>> getMetricsV3(
            @RequestHeader MultiValueMap<String, String> headers,
            @ApiParam(value="id_lookup of parameters to be obtained.",required = false,defaultValue = "by_metric_source")
            @RequestParam(value = "id_lookup",required = false) String id_lookup,
            @ApiParam(value="id of parameters to be obtained.",required = false,defaultValue = "")
            @RequestParam(name= "id",required = false) String id,
            @ApiParam(value="metric_type_lookup of parameters to be obtained.",required = false,defaultValue = "")
            @RequestParam(name="metric_type_lookup",required = false) String metric_type_lookup,
            @ApiParam(value="metricFilter of parameters to be obtained.",required = false,defaultValue = "")
            @RequestParam(name="metricFilter",required = false) String metricFilter,
            @ApiParam(value="target of parameters to be obtained.",required = false,defaultValue = "")
            @RequestParam(name="target",required = false) String target,
            @ApiParam(value="period of parameters to be obtained.",required = false,defaultValue = "latest")
            @RequestParam(name="period",required = false) String period,
            @ApiParam(value="showSamples of parameters to be obtained.",required = false)
            @RequestParam(name="showSamples",required = false) String showSamples ) throws SQLException, InterruptedException {
        String periodDecode = "";
        String targetDecode = "";
        String idDecode = "";
        List<MetricUIMPayload> responseList=new ArrayList<MetricUIMPayload>();
        MetricParameter params=null;

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type",
                "application/json");
        responseHeaders.set("charset",
                "utf-8");

        String nameofCurrMethod = new Throwable()
                .getStackTrace()[0]
                .getMethodName();
        logger.info("----------------- HEADER -------------------------");
        headers.forEach((key, value) -> {
//            logger.debug("Key :"+key+" | value :"+value);
            if(key.equals("authorization"))
                logger.debug("Key :"+key+" | value :"+value);

        });
        logger .info("------------------------------------------------------");
        logger.debug("");

        logger.info("----------------- REQUEST -------------------------");
        logger.info("id_lookup :"+id_lookup);
        logger.info("id :"+id);
        logger.info("metric_type_lookup :"+metric_type_lookup);
        logger.info("metricFilter :"+metricFilter);
        logger.info("target :"+target);
        logger.info("period :"+period);
        logger.info("showSamples :"+showSamples+" | "+Boolean.valueOf(showSamples));
//        logger.info("response :"+response);
        logger .info("------------------------------------------------------");
        logger.debug("");

        try {
            if(period !=null)
                periodDecode= URLDecoder.decode(period, StandardCharsets.UTF_8.toString());
            if(target !=null)
                targetDecode=target;
//                targetDecode= URLDecoder.decode(target, StandardCharsets.UTF_8.toString());
            if(id !=null)
                idDecode= URLDecoder.decode(id, StandardCharsets.UTF_8.toString());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        params=new MetricParameter(
                id_lookup,idDecode,metric_type_lookup,metricFilter,targetDecode,periodDecode, showSamples
        );

        logger.info("----------------- MetricParameter -------------------------");
        logger.info("id_lookup :"+params.getId_lookup());
        logger.info("id :"+params.getId());
        logger.info("metric_type_lookup :"+params.getMetric_type_lookup());
        logger.info("metricFilter :"+params.getMetricFilter());
        logger.info("target :"+params.getTarget());
        logger.info("period :"+params.getPeriod());
        logger.info("showSamples :"+params.getShowSamples());
        logger .info("------------------------------------------------------");

//        if(Boolean.valueOf(showSamples) && !params.getTarget().equals("")){
        if(Boolean.valueOf(showSamples) ){
            logger.debug("TRUE");

            responseList=metricUIMService.getMetricUIMByShowSample(params);
        }else{
            logger.debug("FALSE");
            responseList=metricUIMService.getMetricUIMByHideSample(params);
        }

        logger.debug("GET responseList :"+responseList.size());
        logger.debug("#######################################################");
        logger.debug("");

//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(responseList);
//        try{
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(responseList);
//
//        }finally{
//
////            periodDecode = null;
////            targetDecode = null;
////            idDecode = null;
////            responseList=null;
////            params=null;
//            logger.debug("FInish Request Metrics");
////            System.gc();
//        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseList);

    }

    ResponseEntity<List<MetricUIMPayload>> getMetricsV2x(
            @ApiParam(value="id_lookup of parameters to be obtained.",required = false,defaultValue = "by_metric_source")
            @RequestParam(value = "id_lookup",required = false) String id_lookup,
            @ApiParam(value="id of parameters to be obtained.",required = false,defaultValue = "apmemcoldapp1")
            @RequestParam(name= "id",required = false) String id,
            @ApiParam(value="metric_type_lookup of parameters to be obtained.",required = false,defaultValue = "")
            @RequestParam(name="metric_type_lookup",required = false) String metric_type_lookup,
            @ApiParam(value="metricFilter of parameters to be obtained.",required = false,defaultValue = "")
            @RequestParam(name="metricFilter",required = false) String metricFilter,
            @ApiParam(value="target of parameters to be obtained.",required = false,defaultValue = "")
            @RequestParam(name="target",required = false) String target,
            @ApiParam(value="period of parameters to be obtained.",required = false,defaultValue = "latest")
            @RequestParam(name="period",required = false) String period,
            @ApiParam(value="showSamples of parameters to be obtained.",required = false)
            @RequestParam(name="showSamples",required = false) String showSamples) throws SQLException, InterruptedException {

        String periodDecode = "";
        String targetDecode = "";
        String idDecode = "";
        List<MetricUIMPayload> responseList=new ArrayList<MetricUIMPayload>();
        MetricParameter params=null;

//        logger.debug("############### getMetricsV2 ######################"+target+" | "+params+" | "+responseList);
        logger.info("----------------- REQUEST -------------------------");
        logger.info("id_lookup :"+id_lookup);
        logger.info("id :"+id);
        logger.info("metric_type_lookup :"+metric_type_lookup);
        logger.info("metricFilter :"+metricFilter);
        logger.info("target :"+target);
        logger.info("period :"+period);
        logger.info("showSamples :"+showSamples);
        logger .info("------------------------------------------------------");
        logger.debug("");


        try {
            if(period !=null)
            periodDecode= URLDecoder.decode(period, StandardCharsets.UTF_8.toString());
            if(target !=null)
            targetDecode= URLDecoder.decode(target, StandardCharsets.UTF_8.toString());
            if(id !=null)
            idDecode= URLDecoder.decode(id, StandardCharsets.UTF_8.toString());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        logger.debug("REQUEST period:"+period +" | periodDecode :"+periodDecode+
//                "id_lookup:"+id_lookup +" | id :"+id+
//                "idDecode:"+idDecode +" | metricFilter :"+metricFilter+
//                "targetDecode:"+targetDecode +" | showSamples :"+showSamples);

        params=new MetricParameter(
                id_lookup,idDecode,metric_type_lookup,metricFilter,targetDecode,periodDecode, showSamples
        );

        String nameofCurrMethod = new Throwable()
                .getStackTrace()[0]
                .getMethodName();

        logger.info("----------------- MetricParameter -------------------------");
        logger.info("id_lookup :"+params.getId_lookup());
        logger.info("id :"+params.getId());
        logger.info("metric_type_lookup :"+params.getMetric_type_lookup());
        logger.info("metricFilter :"+params.getMetricFilter());
        logger.info("target :"+params.getTarget());
        logger.info("period :"+params.getPeriod());
        logger.info("showSamples :"+params.getShowSamples());
        logger .info("------------------------------------------------------");



        responseList=metricUIMService.getMetricUIMByShowSample(params);
        logger.debug("GET QOS :"+params.getMetricFilter());
        logger.debug("#######################################################");
        logger.debug("");
        return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseList);
//        try{
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(responseList);
//
//        }finally{
//
//            periodDecode = null;
//            targetDecode = null;
//            idDecode = null;
//            responseList=null;
//            params=null;
//            logger.debug("FInish Request Metrics");
////            System.gc();
//        }
    }




   // @GetMapping("/v3/metrics")
    @Produces("application/json")
    ResponseEntity<List<MetricUIMPayload>> getMetricsV3(HttpServletRequest request) throws SQLException, InterruptedException {
        List<MetricUIMPayload> responseList = null;
        String nameofCurrMethod = new Throwable()
                .getStackTrace()[0]
                .getMethodName();



        String queryStringDecode=request.getQueryString();
        String[] queryParameter;

        logger.debug("#######################################################");
        logger.debug("getMetricsV2:" + request.getQueryString());
        logger.debug("getParameterMap:" + request.getParameterMap());
        MetricParameter params=null;
        if(request.getQueryString()==null){
            responseList=metricUIMService.getMetricUIMBadRequest(nameofCurrMethod);

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseList);
        }else{
            params=new MetricParameter(request.getParameterMap());
            responseList=metricUIMService.getMetricUIM(params,nameofCurrMethod);
        }
        logger.debug("#######################################################");
        logger.debug("");

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseList);
    }

    private Map<String,String[]> validationQueryString(Map<String,String[]> source){
        Map<String,String[]> target=null;
        String validate = null;
        boolean isfound=false;
        for (Map.Entry<String, String[]> entry : source.entrySet()) {

            if(entry.getKey().equals("queryStrings")){
                validate=String.valueOf(entry.getValue());
//                logger.debug("validate :"+validate);
                try {
                    validate= URLDecoder.decode(validate, StandardCharsets.UTF_8.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                isfound=true;
                break;
            }
        }

        if (isfound){
            target=new HashMap<String, String[]>();
//            logger.debug("validate decode :"+validate);
            String[] parameters=validate.split("&");
//            logger.debug("validate parameters :"+ Arrays.toString(parameters));
            for (String s: parameters) {
                //Do your stuff here
                String[] parameter=s.split("=");
                Object value=new String[] {parameter[1]};
                target.put(parameter[0], (String[]) value);
//                logger.debug("validate parameter :"+ s);
            }
        }else
        target=source;
        return target;
//        try{
//            return target;
//        }finally {
////            target=null;
//        }

    }


//    @GetMapping("/v4/metrics")
    @Produces("application/json")
    ResponseEntity<List<MetricUIMPayload>> getMetricsV4(
            @ApiParam(value="queryString of parameters to be obtained. Cannot be empty.",required = true)
           @RequestParam Map<String,String[]> queryStrings) throws SQLException, InterruptedException {


        Map<String,String[]> queryStringsValidation = validationQueryString(queryStrings);


        String nameofCurrMethod = new Throwable()
                .getStackTrace()[0]
                .getMethodName();
        MetricParameter params=null;
        List<MetricUIMPayload> responseList=null;
        System.out.println("REQUEST getMetricsV2:"  +queryStrings.toString()+" | "+queryStrings.size());
        params=new MetricParameter(queryStringsValidation);

        if(params.getMetricFilter()==null){
            logger.debug("GET QOS :"+params.getMetricFilter());
//            responseList=metricUIMService.getMetricUIM(params);
        }else
        if(params.getTarget()==null) {
            logger.debug("GET TARGET :"+params.getMetricFilter());
//            responseList=metricUIMService.getMetricUIM(params);
        }else{
            logger.debug("GET DATA :"+params.getMetricFilter());
//            responseList=metricUIMService.getMetricUIM(params);
        }




        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseList);
    }
}
