package com.ncuz.uim.dao;

import com.ncuz.exception.LoggingException;
import com.ncuz.uim.base.JDBCConnection;
import com.ncuz.uim.entity.MetricParameter;
import com.ncuz.uim.entity.MetricUIM;
import com.ncuz.uim.entity.QOSTableMapper;
import com.ncuz.uim.sqlstatement.MetricUIMOracleSQLStatement;
import com.ncuz.uim.sqlstatement.MetricUIMSQLStatement;
import com.ncuz.uim.utility.JDBCUtility;
import com.ncuz.uim.utility.PropertiesUtility;
import com.ncuz.uim.utility.SQLNotationUtility;
import log4j.helper.service.Log4jService;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MetricUIMV2DAO {
    @Autowired
    Log4jService log4jService;
    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;

    @Autowired
    PropertiesUtility propertiesUtility;

    @Autowired
    SQLNotationUtility sqlNotationUtility;

    @Autowired
    JDBCUtility jdbcUtility;
    private
    JDBCConnection jdbcConnection;
    @PostConstruct
    private void post() throws SQLException {
        logger=log4jService.getLogger(MetricUIMV2DAO.class,"file","../log_app/uim.api.custom.MetricUIMV2DAO");
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.api.custom.Exception");
        jdbcConnection =jdbcUtility.createInstance();
        System.out.println("POST MetricUIMDAO datasource.provider.active : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));
    }

    public ResultSet getQOSUIMCollection(String query,
                                         ResultSet metricUIMList,String proccessID) throws  Exception {
        DateFormat dateFormat=null;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:sss");
        Date dateStart=null;
        dateStart = new Date();
//         logger.debug("### PROCESS ID :" + proccessID + " | START PROCCESS : "+dateFormat.format(dateStart) );
        Connection connection = jdbcConnection.getConnection();
        connection.setAutoCommit(false);
//        logger.debug("datasource.provider.active : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));
//        logger.debug("metricUIMList.size :"+metricUIMList.size());
        PreparedStatement prepareStatement= null;
        String sql=null;


        ResultSet rs =null;
        MetricUIM metricUIM=null;
        String metrics="";
        JSONArray metricsList=new JSONArray();

        Date dateEnd=null;
        long diffInMillis =0;
        sql=query;
        int i = 1;
        try {
            prepareStatement = connection.prepareStatement(
                    sql,
//                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
//            prepareStatement.setFetchSize(50);


//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
            rs = prepareStatement.executeQuery();
            int idx = 0;
              JSONParser parser = new JSONParser();
//            while (rs.next()) {
//                metrics=metrics+"{";
//                metrics=metrics+"\"origin\":\""+rs.getString("origin")+"\",";
//                metrics=metrics+"\"probe\":\""+rs.getString("probe")+"\",";
//                metrics=metrics+"\"samplerate\":\""+rs.getString("samplerate")+"\",";
//                metrics=metrics+"\"sampletime\":\""+rs.getString("sampletime")+"\",";
//                metrics=metrics+"\"samplevalue\":\""+rs.getString("samplevalue")+"\",";
//                metrics=metrics+"\"source\":\""+rs.getString("source")+"\",";
//                metrics=metrics+"\"target\":\""+rs.getString("target")+"\",";
//                metrics=metrics+"\"table_id\":"+rs.getString("table_id")+",";
//                metrics=metrics+"\"qos\":\""+rs.getString("qos")+"\"";
//                metrics=metrics+"}";
////                logger.debug(proccessID+" | RECORD :"+metrics);
//                metricsList.add((JSONObject) parser.parse(metrics));
//                metrics="";
                i++;
//            }
        } catch (SQLException e) {
            LoggingException.log(e, logger2, logger, "PROCESS ID :" + proccessID + " getQOSUIMCollection ERROR : ");
            if(connection!=null)
                jdbcConnection.releaseConnection(connection);
//            e.printStackTrace();
        }finally{
            if(connection!=null)
            jdbcConnection.releaseConnection(connection);
           logger.debug("### PROCESS ID :" + proccessID +" |RECORD : "+i+ " | getQOSUIMCollection SQL :"+sql);
            dateEnd    =new Date();
            diffInMillis =    dateEnd.getTime() - dateStart.getTime();
            logger.debug("");
            logger.debug("### PROCESS ID :" + proccessID + " | Mapping Object Done at "+dateFormat.format(dateEnd)+" | diffInMillis :"+diffInMillis);

        }
//        return metricUIMList;
        try{
            return rs;
        }finally {
            dateEnd    =new Date();
            diffInMillis =    dateEnd.getTime() - dateStart.getTime();
//            logger.debug("RETURN FINAL AT "+dateFormat.format(dateEnd)+" | diffInMillis :"+diffInMillis);
            metricsList=null;
            prepareStatement= null;
            sql=null;
            rs =null;
            metricUIM=null;
            metrics=null;
            dateEnd=null;
//            logger.debug("################# PROCESS ID :" + proccessID + " | getQOSUIMCollection FINISH ####################################################");
//            logger.debug("");
              System.gc();
        }
    };

    public List<QOSTableMapper> getQOSList(MetricParameter params,String proccessID) throws SQLException, InterruptedException {
//        logger.debug("getQOSTableMapping: "+ params.getMetricFilter() );
        Connection connection = jdbcConnection.getConnection();
        connection.setAutoCommit(false);
//        logger.debug("getQOSTableMapping: "+ params.getMetricFilter() );
//        logger.debug("datasource.provider.active : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));

        String sql=null;
        String sqlContext=null;
        PreparedStatement prepareStatement= null;
        QOSTableMapper qosTableMapper=null;
        ResultSet rs=null;
        boolean isOracle=false;
        boolean isError=false;
        int type=0;
//        logger.debug("provider : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));
        if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"))){
            isOracle=true;
            sqlContext=MetricUIMOracleSQLStatement.findQOS;
        }
        else{
            sqlContext= MetricUIMSQLStatement.findQOS;
        }

//        CHECK ID LIST
        String[] idList=params.getId().split(",");
        if(idList.length>2){
//            logger.debug("idList Multi :"+idList[0].equals(""));
            if(params.getMetricFilter().equals("")){
//                logger.debug("isError  :"+params.getMetricFilter());
                isError=true;
            }else{
                if (isOracle) {
                    sqlContext = "select * from ( " + sqlContext + sqlNotationUtility.getWhere() + " name=$name" + sqlNotationUtility.getFetchOne() + " )";
                    sqlContext=sqlContext.replace("$target",",TARGET");
                }
                else
                    sqlContext=sqlContext+sqlNotationUtility.getWhere()+" name=$name"+sqlNotationUtility.getLimitOne();
                sqlContext=sqlContext.replace("$name","'"+params.getMetricFilter()+"'");
            }

        }else{
//            logger.debug("idList Single:"+idList[0].equals(""));
            if(idList[0].trim().equals("") || idList[0].trim().contains("--")){
//                logger.debug("idList Multi :"+idList[0].equals(""));
                if(params.getMetricFilter().equals("")){
                    isError=true;
                }else{
                    if (isOracle){
                        sqlContext="select * from ( "+sqlContext+sqlNotationUtility.getWhere()+" name=$name"+sqlNotationUtility.getFetchOne()+" )";
                        sqlContext=sqlContext.replace("$target",",TARGET");
                    }
                    else
                        sqlContext=sqlContext+sqlNotationUtility.getWhere()+" name=$name"+sqlNotationUtility.getLimitOne();
                    sqlContext=sqlContext.replace("$name","'"+params.getMetricFilter()+"'");
                }
            }else{
//                logger.debug("idList Single getMetricFilter :"+params.getMetricFilter());
                if(params.getMetricFilter().equals("")){
//                    logger.debug("idList Multi  :"+params.getMetricFilter());
                    if(idList.length>1) isError=true;
                    else
                    if (isOracle){
                        sqlContext="select a.*,(select target from s_qos_data where qos=a.qos FETCH FIRST 1 ROWS ONLY) target     from ("+
                                sqlContext+sqlNotationUtility.getWhere()+" SOURCE=$source group by QOS,a.R_TABLE,a.H_TABLE,a.D_TABLE) a";
                        sqlContext=sqlContext.replace("$target","");
                    }else
                        sqlContext=sqlContext+sqlNotationUtility.getWhere()+" SOURCE=$source group by QOS";
                    sqlContext=sqlContext.replace("$source","'"+idList[0].trim()+"'");
                }else{
//                    logger.debug("metricFilter Single  :"+params.getMetricFilter());
                    if(params.getTarget().trim().equals("") || params.getTarget().trim().contains("--")){
//                        logger.debug("Target Non Target  :"+params.getTarget());
                        if (isOracle){
                            sqlContext="select * from ( "+sqlContext+sqlNotationUtility.getWhere()+" SOURCE=$source and name=$name )";
                            sqlContext=sqlContext.replace("$target",",TARGET");

                        }
                        else
                            sqlContext=sqlContext+sqlNotationUtility.getWhere()+" SOURCE=$source and name=$name";
                        sqlContext=sqlContext.replace("$name","'"+params.getMetricFilter()+"'");
                        sqlContext=sqlContext.replace("$source","'"+idList[0].trim()+"'");
                        type=1;
                    }else{
                        if (isOracle){
                            sqlContext="select * from ( "+sqlContext+sqlNotationUtility.getWhere()+" name=$name"+sqlNotationUtility.getFetchOne()+" )";
                            sqlContext=sqlContext.replace("$target",",TARGET");
                        }
                        else
                            sqlContext=sqlContext+sqlNotationUtility.getWhere()+" name=$name"+sqlNotationUtility.getLimitOne();
                        sqlContext=sqlContext.replace("$name","'"+params.getMetricFilter()+"'");
                    }

                }


            }
        }

//

//        logger.debug("getId :"+params.getId());
//        logger.debug("getTarget :"+params.getTarget());
//        logger.debug("getMetricFilter :"+params.getMetricFilter());
//        logger.debug("getPeriod :"+params.getPeriod());
//        logger.debug("getId_lookup :"+params.getId_lookup());
//        logger.debug("getMetric_type_lookup :"+params.getMetric_type_lookup());
//        logger.debug("getShowSamples :"+params.getShowSamples());
//        logger.debug("getQOSTableMapping: "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active")+
//                " | QUERY : "+ sql+" | params:"+params.getMetricFilter());


        List<QOSTableMapper> qosList=new ArrayList<QOSTableMapper>();
        if(!isError){
            qosTableMapper=new QOSTableMapper();
            try {
                prepareStatement = connection.prepareStatement(
                        sqlContext,
//                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
//                prepareStatement.setFetchSize(50);


//            int i = 1;
//            prepareStatement.setString(i++, params.getMetricFilter());
                rs = prepareStatement.executeQuery();

                int idx = 0;
                while (rs.next()) {
//                    logger.debug("R_TABLE :"+rs.getString("R_TABLE"));
//                    logger.debug("H_TABLE :"+rs.getString("H_TABLE"));
//                    logger.debug("D_TABLE :"+rs.getString("D_TABLE"));
//                    logger.debug("QOS :"+rs.getString("QOS"));
                    qosTableMapper=new QOSTableMapper();
                    qosTableMapper.setrTable(rs.getString("R_TABLE"));
                    qosTableMapper.sethTable(rs.getString("H_TABLE"));
                    qosTableMapper.setdTable(rs.getString("D_TABLE"));
                    qosTableMapper.setQos(rs.getString("QOS"));
                    qosTableMapper.setTarget(rs.getString("TARGET"));
                    qosTableMapper.setType(type);
                    qosList.add(qosTableMapper);
//                    logger.debug("R_TABLE :"+qosList.get(qosList.size()-1).getrTable());
                }
            } catch (SQLException e) {
//                e.printStackTrace();
                LoggingException.log(e, logger2, logger, "PROCESS ID :" + proccessID + " getQOSList ERROR : ");
//                logger.error("### PROCESS ID :" + proccessID + " | ERROR SQL :"+e);
                jdbcConnection.releaseConnection(connection);
            }finally{
                if(connection!=null){
                    logger.debug("### PROCESS ID :" + proccessID + " | getQOSList SQL :"+sqlContext);
                    jdbcConnection.releaseConnection(connection);
                }
//                logger.debug("getQOSTableMapping done ");

            }
        }


//        logger.debug("qosList :"+qosList.size());
//        return qosList;
        try{
            return qosList;
        }finally {
            qosTableMapper=null;
            sql=null;
            sqlContext=null;
            prepareStatement= null;
            rs=null;
            System.gc();
        }
    };

    public String buildQueryQOSUIM(String rTable,String hTable,String dTable,String qos,String qosTarget,
                                   MetricParameter params,String proccessID){
        String sql=null;
        SimpleDateFormat input = null;
        SimpleDateFormat output=null;
        String startperiod = null;

        String[] sourceList=null;
        String[] targetList=null;
        Date d = null;
        String source = "";
        String target ="";
        String endperiod = null;
        if("latest".equals(params.getPeriod())){
            if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active")))
                sql=MetricUIMOracleSQLStatement.findMetricByParametersLatestPlain;
            else
                sql=MetricUIMSQLStatement.findMetricByParametersLatestPlain;



        }else{
            if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active")))
                sql=MetricUIMOracleSQLStatement.findMetricByParametersPlain;
            else
                sql=MetricUIMSQLStatement.findMetricByParametersPlain;



            input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            output = new SimpleDateFormat("dd-MMM-yy HH.mm.ss");
//        logger.debug("params.getStartPeriod() : "+params.getStartPeriod());

            try {
                d = input.parse(params.getStartPeriod());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startperiod = "'"+output.format(d)+"'";

            d = null;
            try {
                d = input.parse(params.getEndPeriod());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            endperiod = "'"+output.format(d)+"'";
            sql=sql.replace("$startPeriod",startperiod);
            sql=sql.replace("$endPeriod",endperiod);
//       logger.debug("source : "+source);
//        logger.debug("target : "+target);
//        logger.debug("startperiod : "+startperiod);
//        logger.debug("endperiod : "+endperiod);


        }


//        logger.debug("sourceList :"+params.getId());
//        logger.debug("targetList :"+params.getTarget());
//        logger.debug("qos :"+qos);
//        logger.debug("qosTarget :"+qosTarget);

        //        Mapping where
//        if((params.getId().equals("") || params.getId().contains("--")) &&
//                (params.getTarget().equals("") || params.getTarget().contains("--"))){
//            sql=sql.replace("$where","");
//        }else{
//            sql=sql.replace("$where"," WHERE ");
//        }
        sourceList=params.getId().split(",");
        targetList=params.getTarget().split(",");
        //        Mapping where
        if((params.getId().equals("") ) &&
                (params.getTarget().trim().equals("") )){
            sql=sql.replace("$where","");
        }else{
            logger.debug("sourceList NCUZ :"+sourceList.length+" | "+sourceList[0].trim().contains("--"));
            logger.debug("targetList NCUZ :"+targetList.length+" | "+targetList[0].trim().contains("--"));
            if((sourceList.length<2 && sourceList[0].trim().contains("--"))){
                if(targetList.length<=1 || targetList[0].trim().contains("--")){
                    sql=sql.replace("$where","");
                }else{
                    sql=sql.replace("$where","WHERE");
                }
            }else{
                sql=sql.replace("$where"," WHERE ");
            }

        }

//        Mapping Source
        if(params.getId().equals("")){
            sql=sql.replace("$Source","");
        }else{
//            sourceList=params.getId().split(",");
            if(sourceList.length<2 && sourceList[0].trim().contains("--")){
                sql=sql.replace("$Source","");
            }else{
                for(int i=0;i<sourceList.length;i++){
                    source=source+"'"+sourceList[i].trim()+"',";
                }
                source=source.substring(0,source.length()-1);
                sql=sql.replace("$Source","source in("+source+")");
            }

        }

//        Mapping Target
        if(params.getTarget().trim().equals("") ){
            sql=sql.replace("$Target","");
        }else{
//            logger.debug("TARGET E");
//            targetList=params.getTarget().split(",");
            if(targetList.length<=1 && targetList[0].trim().contains("--")){
                sql=sql.replace("$Target","");
            }else{
                for(int i=0;i<targetList.length;i++){
                    target=target+"'"+targetList[i].trim()+"',";
                }

                target=target.substring(0,target.length()-1);
                if(params.getId().equals("")){
                    sql=sql.replace("$Target","target in("+target+")");
                }else{
                    sql=sql.replace("$Target","AND target in("+target+")");
                }
            }


        }
        sql=sql.replace("$RTable",rTable);
        sql=sql.replace("$HTable",hTable);
        sql=sql.replace("$DTable",dTable);
        try{
            return sql;
        }finally{
            sql=null;
        }

    }
}
