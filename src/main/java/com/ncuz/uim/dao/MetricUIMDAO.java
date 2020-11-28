package com.ncuz.uim.dao;

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
public class MetricUIMDAO {
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
        logger=log4jService.getLogger(MetricUIMDAO.class,"file","../log_app/uim.api.custom.MetricUIMDAO");
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.api.custom.Exception");
          jdbcConnection =jdbcUtility.createInstance();
    System.out.println("POST MetricUIMDAO datasource.provider.active : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));
    }

    public String buildQueryQOSUIM(String rTable,String hTable,String dTable,String qos,String qosTarget,
                                   MetricParameter params){
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
                sql= MetricUIMSQLStatement.findMetricByParametersLatestPlain;



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


        logger.debug("sourceList :"+params.getId());
        logger.debug("targetList :"+params.getTarget());
        logger.debug("qos :"+qos);
        logger.debug("qosTarget :"+qosTarget);

        //        Mapping where
//        if((params.getId().equals("") || params.getId().contains("--")) &&
//                (params.getTarget().equals("") || params.getTarget().contains("--"))){
//            sql=sql.replace("$where","");
//        }else{
//            sql=sql.replace("$where"," WHERE ");
//        }

        //        Mapping where
        if((params.getId().equals("") ) &&
                (params.getTarget().equals("") )){
            sql=sql.replace("$where","");
        }else{
            sql=sql.replace("$where"," WHERE ");
        }

//        Mapping Source
        if(params.getId().equals("")){
            sql=sql.replace("$Source","");
        }else{
            sourceList=params.getId().split(",");
            if(sourceList.length<2 && sourceList[0].contains("--")){
                sql=sql.replace("$Source","");
            }else{
                for(int i=0;i<sourceList.length;i++){
                    source=source+"'"+sourceList[i]+"',";
                }
                source=source.substring(0,source.length()-1);
                sql=sql.replace("$Source","source in("+source+")");
            }

        }

//        Mapping Target
        if(params.getTarget().equals("") ){
            if(params.getMetricFilter().equals("")){
                logger.debug("TARGET A");
                sql=sql.replace("$Target","");
            }else{
                logger.debug("TARGET B");
//                sql=sql.replace("$Target"," target like'%'");
                sql=sql.replace("$Target","");
                logger.debug("-------"+sql);
//                targetList=qosTarget.split(",");
//                for(int i=0;i<targetList.length;i++){
//                    target=target+"'"+targetList[i]+"',";
//                }
//
//                target=target.substring(0,target.length()-1);
//                if(params.getId().equals("")){
//                    logger.debug("TARGET C");
//                    sql=sql.replace("$Target","target in("+target+")");
//                }else{
//                    logger.debug("TARGET D");
//                    sql=sql.replace("$Target","AND target in("+target+")");
//                }
            }

        }else{
            logger.debug("TARGET E");
            targetList=params.getTarget().split(",");
            if(targetList.length<=1 && targetList[0].contains("--")){
                sql=sql.replace("$Target","");
            }else{
                for(int i=0;i<targetList.length;i++){
                    target=target+"'"+targetList[i]+"',";
                }

                target=target.substring(0,target.length()-1);
                if(params.getId().equals("")){
                    sql=sql.replace("$Target","target in("+target+")");
                }else{
                    sql=sql.replace("$Target","AND target in("+target+")");
                }
            }


        }


//
//
//

//
        sql=sql.replace("$RTable",rTable);
        sql=sql.replace("$HTable",hTable);
        sql=sql.replace("$DTable",dTable);
        return sql;
    }
    public List<MetricUIM> getQOSUIMCollection(String query,
                                               List<MetricUIM> metricUIMList) throws SQLException, InterruptedException {
        Connection connection = jdbcConnection.getConnection();
//        logger.debug("datasource.provider.active : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));
//        logger.debug("metricUIMList.size :"+metricUIMList.size());
        PreparedStatement prepareStatement= null;
        String sql=null;


        ResultSet rs =null;
        MetricUIM metricUIM=null;

        Date dateEnd=null;
        long diffInMillis =0;


//        sql=sql.replace("$Source",source);
//
//
//
        sql=query;

        DateFormat dateFormat=null;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:sss");
        Date dateStart=null;
        dateStart = new Date();
////        logger.debug("");
////        logger.debug("getQOSUIMList : "+dateFormat.format(dateStart)+" | "+ sql );
//
        logger.debug("Start Query getQOSUIMCollection");
        try {
            prepareStatement = connection.prepareStatement(
                    sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            int i = 1;
//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
            rs = prepareStatement.executeQuery();
            logger.debug("____________________Start Mapping Object getQOSUIMCollection_____________________");
            int idx = 0;
            while (rs.next()) {
                if(i<10){
//                    logger.debug("sampletime :"+rs.getString("sampletime"));
//                    logger.debug("samplevalue :"+rs.getString("samplevalue"));
                }
//
                metricUIM=new MetricUIM();
                metricUIM.setOrigin(rs.getString("origin"));
                metricUIM.setProbe(rs.getString("probe"));
                metricUIM.setSamplerate(rs.getFloat("samplerate"));
                metricUIM.setSampletime(rs.getString("sampletime"));
                metricUIM.setSamplevalue(rs.getFloat("samplevalue"));
                metricUIM.setSource(rs.getString("source"));
                metricUIM.setTarget(rs.getString("target"));
                metricUIM.setTable_id(rs.getInt("table_id"));
                metricUIM.setQosName(rs.getString("qos"));
//                logger.debug("sampletime :"+metricUIM.getSampletime());

//                    qosTableMapper.setrTable(rs.getString("R_TABLE"));
//                    qosTableMapper.sethTable(rs.getString("H_TABLE"));
//                    qosTableMapper.setdTable(rs.getString("D_TABLE"));
                metricUIMList.add(metricUIM);
                i++;
            }

            logger.debug("Finish Query");
        } catch (SQLException e) {
            logger.error("Error Query"+e);
            if(connection!=null)
                jdbcConnection.releaseConnection(connection);
//            e.printStackTrace();
        }finally{
            if(connection!=null)
                logger.debug("SQL getQOSUIMCollection :"+sql);
                jdbcConnection.releaseConnection(connection);
            logger.debug("___________________Finish Mapping Object getQOSUIMCollection_____________________");
//            logger.debug("connection.close  "+connection);
        }
//
//
        dateEnd    =new Date();
        diffInMillis =    dateEnd.getTime() - dateStart.getTime();
        logger.debug("metricUIMList.size Final :"+metricUIMList.size());
        logger.debug("getQOSUIMList Done "+dateFormat.format(dateEnd)+" | diffInMillis :"+diffInMillis);
//        return metricUIMList;
        try{
            return metricUIMList;
        }finally {
            dateEnd    =new Date();
            diffInMillis =    dateEnd.getTime() - dateStart.getTime();
            logger.debug("getQOSUIMList Done "+dateFormat.format(dateEnd)+" | diffInMillis :"+diffInMillis);

            prepareStatement= null;
            sql=null;
            rs =null;
            metricUIM=null;
            dateEnd=null;
//              System.gc();
        }

    };



    public List<QOSTableMapper> getQOSList(MetricParameter params) throws SQLException, InterruptedException {
//        logger.debug("getQOSTableMapping: "+ params.getMetricFilter() );
        Connection connection = jdbcConnection.getConnection();
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
        logger.debug("provider : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));
        if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"))){
            isOracle=true;
            sqlContext=MetricUIMOracleSQLStatement.findQOS;
        }
        else{
            sqlContext=MetricUIMSQLStatement.findQOS;
        }

//        CHECK ID LIST
        String[] idList=params.getId().split(",");
        if(idList.length>2){
            logger.debug("idList Multi :"+idList[0].equals(""));
            if(params.getMetricFilter().equals("")){
                logger.debug("isError  :"+params.getMetricFilter());
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
            logger.debug("idList Single:"+idList[0].equals(""));
            if(idList[0].equals("") || idList[0].contains("--")){
                logger.debug("idList Multi :"+idList[0].equals(""));
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
                logger.debug("idList Single getMetricFilter :"+params.getMetricFilter());
                if(params.getMetricFilter().equals("")){
                    logger.debug("idList Multi  :"+params.getMetricFilter());
                    if(idList.length>1) isError=true;
                    else
                    if (isOracle){
                        sqlContext="select a.*,(select target from s_qos_data where qos=a.qos FETCH FIRST 1 ROWS ONLY) target     from ("+
                                sqlContext+sqlNotationUtility.getWhere()+" SOURCE=$source group by QOS,a.R_TABLE,a.H_TABLE,a.D_TABLE) a";
                        sqlContext=sqlContext.replace("$target","");
                    }else
                        sqlContext=sqlContext+sqlNotationUtility.getWhere()+" SOURCE=$source group by QOS";
                    sqlContext=sqlContext.replace("$source","'"+idList[0]+"'");
                }else{
                    logger.debug("metricFilter Single  :"+params.getMetricFilter());
                    if(params.getTarget().equals("") || params.getTarget().contains("--")){
                        logger.debug("Target Non Target  :"+params.getTarget());
                        if (isOracle){
                            sqlContext="select * from ( "+sqlContext+sqlNotationUtility.getWhere()+" SOURCE=$source and name=$name )";
                            sqlContext=sqlContext.replace("$target",",TARGET");

                        }
                        else
                            sqlContext=sqlContext+sqlNotationUtility.getWhere()+" SOURCE=$source and name=$name";
                        sqlContext=sqlContext.replace("$name","'"+params.getMetricFilter()+"'");
                        sqlContext=sqlContext.replace("$source","'"+idList[0]+"'");
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
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);


//            int i = 1;
//            prepareStatement.setString(i++, params.getMetricFilter());
                rs = prepareStatement.executeQuery();

                int idx = 0;
                logger.debug("___________________Start Mapping Object getQOSList_____________________");
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
                logger.error("Error Query"+e);
                jdbcConnection.releaseConnection(connection);
            }finally{
                if(connection!=null){
                    logger.debug("sqlContext getQOSList :"+sqlContext);
                    jdbcConnection.releaseConnection(connection);
                    logger.debug("_____________________Finish Mapping Object getQOSList_____________________");
                }
//                logger.debug("getQOSTableMapping done ");

            }
        }


        logger.debug("qosList :"+qosList.size());
//        return qosList;
        try{
            return qosList;
        }finally {
            qosTableMapper=null;
            sql=null;
            sqlContext=null;
            prepareStatement= null;
            rs=null;
//            System.gc();
        }
    };




    public QOSTableMapper getQOSTableMapping(MetricParameter params) throws SQLException, InterruptedException {
//        logger.debug("getQOSTableMapping: "+ params.getMetricFilter() );
        Connection connection = jdbcConnection.getConnection();
//        logger.debug("getQOSTableMapping: "+ params.getMetricFilter() );
//        logger.debug("datasource.provider.active : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));

        String sql=null;
        String sqlContext=null;
        PreparedStatement prepareStatement= null;
        QOSTableMapper qosTableMapper=new QOSTableMapper();
        ResultSet rs=null;

        if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"))){
            sql= MetricUIMOracleSQLStatement.findQOSByName;
        }
        else{
            sql=MetricUIMSQLStatement.findQOSByName;
        }


        sql=sql.replace("$name","'"+params.getMetricFilter()+"'");

        logger.debug("getQOSTableMapping: "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active")+
                " | QUERY : "+ sql+" | params:"+params.getMetricFilter());




        try {
            prepareStatement = connection.prepareStatement(
                    sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                logger.debug("Start QUERY");
//            int i = 1;
//            prepareStatement.setString(i++, params.getMetricFilter());
              rs = prepareStatement.executeQuery();
            logger.debug("Start Map Object");
                int idx = 0;
                while (rs.next()) {
                    logger.debug("R_TABLE :"+rs.getString("R_TABLE"));
                    logger.debug("H_TABLE :"+rs.getString("H_TABLE"));
                    logger.debug("D_TABLE :"+rs.getString("D_TABLE"));

                    qosTableMapper.setrTable(rs.getString("R_TABLE"));
                    qosTableMapper.sethTable(rs.getString("H_TABLE"));
                    qosTableMapper.setdTable(rs.getString("D_TABLE"));
                }
            logger.debug("Finish Map Object");
            } catch (SQLException e) {
//            e.printStackTrace();
            logger.error("Error Query"+e);
            if(connection!=null)
            jdbcConnection.releaseConnection(connection);
        }finally{
                if(connection!=null)
                    jdbcConnection.releaseConnection(connection);
//                logger.debug("getQOSTableMapping done ");

        }

//        logger.debug("");
//        return qosTableMapper;
        try{
            return qosTableMapper;
        }finally {
            qosTableMapper=null;
              sql=null;
            sqlContext=null;
              prepareStatement= null;
              rs=null;
//            System.gc();
        }
    };

    public List<MetricUIM> getQOSUIMList(String rTable,String hTable,String dTable,MetricParameter params) throws SQLException, InterruptedException {
        Connection connection = jdbcConnection.getConnection();
//        logger.debug("datasource.provider.active : "+propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active"));

        PreparedStatement prepareStatement= null;
        String sql=null;
        String endperiod = null;
        String startperiod = null;
        SimpleDateFormat input = null;
        SimpleDateFormat output=null;
        String[] sourceList=null;
        String[] targetList=null;
        List<MetricUIM> metricUIMList=null;
        ResultSet rs =null;
        MetricUIM metricUIM=null;
        Date d = null;
        String source = "";
        String target ="";
        Date dateEnd=null;
        long diffInMillis =0;

        if("latest".equals(params.getPeriod())){
            if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active")))
                sql=MetricUIMOracleSQLStatement.findMetricByParametersLatest;
            else
                sql=MetricUIMSQLStatement.findMetricByParametersLatest;



        }else{
            if("oracle".equals(propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active")))
                sql=MetricUIMOracleSQLStatement.findMetricByParameters;
            else
                sql=MetricUIMSQLStatement.findMetricByParameters;


            
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

         sourceList=params.getId().split(",");
         targetList=params.getTarget().split(",");


        for(int i=0;i<sourceList.length;i++){
            source=source+"'"+sourceList[i]+"',";
        }
        for(int i=0;i<sourceList.length;i++){
            target=target+"'"+targetList[i]+"',";
        }
        source=source.substring(0,source.length()-1);
        target=target.substring(0,target.length()-1);

        sql=sql.replace("$RTable",rTable);
        sql=sql.replace("$HTable",hTable);
        sql=sql.replace("$DTable",dTable);
        sql=sql.replace("$Source",source);
        sql=sql.replace("$Target",target);


        logger.debug("SQL :"+sql);

        DateFormat dateFormat=null;
                dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:sss");
        Date dateStart=null;
                dateStart = new Date();
//        logger.debug("");
//        logger.debug("getQOSUIMList : "+dateFormat.format(dateStart)+" | "+ sql );

                metricUIMList=new ArrayList<MetricUIM>();
        try {
            prepareStatement = connection.prepareStatement(
                    sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            int i = 1;
//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
//            prepareStatement.setString(i++, params.getId());
//            prepareStatement.setString(i++, params.getTarget());
            logger.debug("Start Query");
             rs = prepareStatement.executeQuery();
            logger.debug("Start Map Object");
                int idx = 0;
                while (rs.next()) {
//                   logger.debug("R_TABLE :"+rs.getString("origin"));
//                    logger.debug("H_TABLE :"+rs.getString("H_TABLE"));
//                    logger.debug("D_TABLE :"+rs.getString("D_TABLE"));
                     metricUIM=new MetricUIM();
                    metricUIM.setOrigin(rs.getString("origin"));
                    metricUIM.setProbe(rs.getString("probe"));
                    metricUIM.setSamplerate(rs.getFloat("samplerate"));
                    metricUIM.setSampletime(rs.getString("sampletime"));
                    metricUIM.setSamplevalue(rs.getFloat("samplevalue"));
                    metricUIM.setSource(rs.getString("source"));
                    metricUIM.setTarget(rs.getString("target"));
                    metricUIM.setTable_id(rs.getInt("table_id"));
                    metricUIM.setQosName(rs.getString("qos"));

//                    qosTableMapper.setrTable(rs.getString("R_TABLE"));
//                    qosTableMapper.sethTable(rs.getString("H_TABLE"));
//                    qosTableMapper.setdTable(rs.getString("D_TABLE"));
                    metricUIMList.add(metricUIM);
                }
            logger.debug("Finish Map Object");
        } catch (SQLException e) {
//            e.printStackTrace();
            logger.error("Error Query"+e);
            if(connection!=null)
                jdbcConnection.releaseConnection(connection);
        }finally{
            if(connection!=null)
                jdbcConnection.releaseConnection(connection);
//            logger.debug("connection.close  "+connection);
        }


        dateEnd    =new Date();
         diffInMillis =    dateEnd.getTime() - dateStart.getTime();
        logger.debug("getQOSUIMList Done "+metricUIMList.size() +" | End Time :"+dateFormat.format(dateEnd)+" | diffInMillis :"+diffInMillis);
//        return metricUIMList;
        try{
            return metricUIMList;
        }finally {
              metricUIMList=null;
              prepareStatement= null;
              sql=null;
              endperiod = null;
              startperiod = null;
              input = null;
              output=null;
              sourceList=null;
              targetList=null;
              rs =null;
              metricUIM=null;
              d = null;
              source =null;
              target =null;
              dateEnd=null;
//              System.gc();
        }

    };
}
