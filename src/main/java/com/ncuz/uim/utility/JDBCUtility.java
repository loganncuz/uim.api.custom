package com.ncuz.uim.utility;

import com.ncuz.uim.base.JDBCConnection;
import log4j.helper.service.Log4jService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

@Service
public class JDBCUtility {
    @Autowired
    Log4jService log4jService;

    @Autowired
    PropertiesUtility propertiesUtility;

    @Autowired
    private Environment env;
    private static Logger logger;
    private static Logger logger2;

    private JDBCConnection jdbcConnection;



//    public JDBCUtility(){
//        System.out.println("JDBCUtility Construct:"+log4jService);
//    }

    @PostConstruct
    private void post() {
        System.out.println("JDBCUtility Post");
        logger=log4jService.getLogger(JDBCUtility.class,"file","../log_app/uim.api.custom");
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.api.custom.Exception");
//        logger.debug("JDBCUtility Post:"+log4jService);
    }

    public JDBCConnection createInstance(String connectionURL,String userName,String password) throws SQLException {

//        this.logger.debug("JDBCUtility createInstance 1:" + jdbcConnection);
        if(jdbcConnection ==null){
            jdbcConnection= new JDBCConnection(env,logger,connectionURL,userName,password);
        }
        return jdbcConnection;
    }

    public JDBCConnection createInstance(Logger logger,String connectionURL,String userName,String password) throws SQLException {
//        this.logger.debug("JDBCUtility test createInstance:");
        this.logger=logger;
//       this.logger.debug("JDBCUtility createInstance 2:" + jdbcConnection);
        if(jdbcConnection ==null){
            jdbcConnection= new JDBCConnection(env,logger,connectionURL,userName,password);
        }
        return  jdbcConnection;
    }

    private DriverManagerDataSource dataSource = new DriverManagerDataSource();

    public DriverManagerDataSource getDataSource() {
        return dataSource;
    }

    public JDBCConnection createInstance() throws SQLException {

//        this.logger.debug("JDBCUtility createInstance 3:"+ jdbcConnection );
        if(jdbcConnection ==null){
            String provider=propertiesUtility.getDataSourceProperties().getProperty("datasource.provider.active");
            jdbcConnection= new JDBCConnection(env,logger,propertiesUtility.getDataSourceProperties().getProperty(provider+".db.url"),
                    propertiesUtility.getDataSourceProperties().getProperty(provider+".db.username"),
                    propertiesUtility.getDataSourceProperties().getProperty(provider+".db.password"));
            dataSource.setUrl(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.url"));
            dataSource.setUsername(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.username"));
            dataSource.setPassword(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.password"));
            dataSource.setDriverClassName(propertiesUtility.getDataSourceProperties().getProperty(provider+".db.driver"));
        }
        return jdbcConnection ;
    }


}
