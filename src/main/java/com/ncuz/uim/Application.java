package com.ncuz.uim;

import com.ncuz.uim.utility.CounterUtility;
import com.ncuz.uim.utility.PropertiesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

@SpringBootApplication
@EnableAutoConfiguration
@EntityScan(basePackageClasses = {
        Application.class
        })
@EnableDiscoveryClient
public class Application{
    @Bean
    public HttpTraceRepository htttpTraceRepository()
    {
        return new InMemoryHttpTraceRepository();
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }


    @Autowired
    PropertiesUtility propertiesUtility;


    @PostConstruct
    private void post() throws SQLException {

            propertiesUtility.initApplicationProperties();
    }
    @Autowired
    CounterUtility counterUtility;
}
