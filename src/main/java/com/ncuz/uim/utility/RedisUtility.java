package com.ncuz.uim.utility;

import com.ncuz.uim.base.RedisConnection;
import log4j.helper.service.Log4jService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;

@Service
public class RedisUtility {
    @Autowired
    Log4jService log4jService;

    @Autowired
    PropertiesUtility propertiesUtility;

    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;

    private RedisConnection redisConnection;
    @PostConstruct
    private void post() {
//        System.out.println("JDBCUtility Post");
        logger=log4jService.getLogger(RedisUtility.class,"file","../log_app/uim.grafana.adapter");
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.grafana.adapter.Exception");
//        logger.debug("JDBCUtility Post:"+log4jService);
    }
    @PreDestroy
    private void destroy()   {
        logger=null;
        logger2=null;
        redisConnection=null;
    }

    public RedisConnection createInstance(String hostname, int port, String password, int db) throws SQLException {

//        this.logger.debug("JDBCUtility createInstance 1:" + jdbcConnection);
        if(redisConnection ==null){
            redisConnection= new RedisConnection(env,logger,hostname,port,password,db);
        }
        return redisConnection;
    }

    public RedisConnection createInstance(Logger logger, String hostname, int port, String password, int db) throws SQLException {
//        this.logger.debug("JDBCUtility test createInstance:");
        this.logger=logger;
//       this.logger.debug("JDBCUtility createInstance 2:" + jdbcConnection);
        if(redisConnection ==null){
            redisConnection= new RedisConnection(env,logger,hostname,port,password,db);
        }
        return  redisConnection;
    }

    public RedisConnection createInstance() throws SQLException {

//        this.logger.debug("JDBCUtility createInstance 3:"+ jdbcConnection );
        if(redisConnection ==null){
            String provider=propertiesUtility.getRedisDataSourceProperties().getProperty("datasource.provider.active");
            redisConnection= new RedisConnection(env,logger,propertiesUtility.getRedisDataSourceProperties().getProperty(provider+".dev.hostname"),
                    Integer.parseInt(propertiesUtility.getRedisDataSourceProperties().getProperty(provider+".dev.port")),
                    propertiesUtility.getRedisDataSourceProperties().getProperty(provider+".dev.password"),
                    Integer.parseInt(propertiesUtility.getRedisDataSourceProperties().getProperty(provider+".dev.database")));
//            dataSource.setUrl(propertiesUtility.getDataSourceProperties().getProperty(provider+".dev.url"));
//            dataSource.setUsername(propertiesUtility.getDataSourceProperties().getProperty(provider+".dev.username"));
//            dataSource.setPassword(propertiesUtility.getDataSourceProperties().getProperty(provider+".dev.password"));
//            dataSource.setDriverClassName(propertiesUtility.getDataSourceProperties().getProperty(provider+".dev.driver"));
        }
        return redisConnection ;
    }

    public RedisConnection createInstance(String datasource) throws SQLException {

//        if(jdbcConnection ==null){
        String provider=propertiesUtility.getRedisDataSourceProperties().getProperty("datasource.provider.active");
        logger.debug("PROVIDER :"+provider+"| "+datasource+" | "+
                propertiesUtility.getRedisDataSourceProperties());
        logger.debug("JDBCUtility createInstance 3:"+ redisConnection +"|"+
                provider+" | "+datasource+" | "+(provider+"."+datasource+".port")+" | "
//                +Integer.parseInt(propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".port"))
        );
//            jdbcConnection=
//            dataSource.setUrl(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.url"));
//            dataSource.setUsername(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.username"));
//            dataSource.setPassword(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.password"));
//            dataSource.setDriverClassName(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.driver"));
//        }
        return new RedisConnection(env,logger,
                propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".hostname"),
                Integer.parseInt(propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".port")),
                propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".password"),
                Integer.parseInt(propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".database")));
    }

    public RedisConnection createInstance(String provider, String datasource) throws SQLException {

//        this.logger.debug("JDBCUtility createInstance 3:"+ jdbcConnection );
//        if(jdbcConnection ==null){
//            String provider=propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active");
//            jdbcConnection=
//            dataSource.setUrl(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.url"));
//            dataSource.setUsername(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.username"));
//            dataSource.setPassword(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.password"));
//            dataSource.setDriverClassName(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.driver"));
//        }
        return new RedisConnection(env,logger,
                propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".hostname"),
                Integer.parseInt(propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".port")),
                propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".password"),
                Integer.parseInt(propertiesUtility.getRedisDataSourceProperties().getProperty(provider+"."+datasource+".database")));
    }

}
