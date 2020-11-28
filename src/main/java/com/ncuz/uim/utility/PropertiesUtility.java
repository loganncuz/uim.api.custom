package com.ncuz.uim.utility;

import com.ncuz.uim.Application;
import log4j.helper.service.Log4jService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

@Service
public class PropertiesUtility {

    @Autowired
    Log4jService log4jService;

    @Autowired
    private Environment env;

    public PropertiesUtility(){
        System.out.println("PropertiesUtility CREATE");
        path= this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println("PATH : "+path);
        String[] directory=path.split("/");
        if(directory[0].contains("file")){
//            System.out.println("PROD");
            path= prod(directory);
        }else{
            path="./";
        }
    }


    @PostConstruct
    private void post() {
        System.out.println("PropertiesUtility POST");
        logger=log4jService.getLogger(PropertiesUtility.class,"file","../log_app/uim.api.custom",path);
        logger2=log4jService.getLogger(RuntimeException.class,"file","../log_app/uim.api.custom.Exception",path);
    }


    private static Logger logger;
    private static Logger logger2;
    private static Properties dataSourceProperties=new Properties();
    private Path configPath = null;
    private static String path="./";
    private static Properties applicationProperties=new Properties();
    private static Properties redisDataSourceProperties=new Properties();

    public static Properties getRedisDataSourceProperties() {
        return redisDataSourceProperties;
    }

    public String getPath(){
        return path;
    }

    public static Properties getApplicationProperties() {
        InputStream input = null;
        String fileName="";

        path= PropertiesUtility.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String[] directory=path.split("/");
        if(directory[0].contains("file")){
//            System.out.println("PROD");
            path= prod(directory);
        }else{
            path="./";
        }
        fileName=path+"application.properties";
        try {
            input = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            applicationProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(input!=null){
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("applicationProperties: "+ applicationProperties );
        return applicationProperties;
    }


    public static Properties getDataSourceProperties() {

        return dataSourceProperties;
    }

    public   void  loadRedisDataSourceProperties()   {
        InputStream input = null;
        String fileName="";
        if(env.getProperty("spring.profiles.active").equals("dev")){
            fileName="datasource-redis.properties";
            input = Application.class.getClassLoader().getResourceAsStream(fileName);

        }else{
            // fileName="./datasource-prod.properties";
            fileName=path+"datasource-redis.properties";
            try {
                input = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        logger.debug("loadRedisDataSourceProperties: "+fileName);
        try {
            redisDataSourceProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(input!=null){
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.debug("loadRedisDataSourceProperties: "+ redisDataSourceProperties );
    }

    public   void  loadDataSourceProperties()   {
        InputStream input = null;
        String fileName="";
        if(env.getProperty("spring.profiles.active").equals("dev")){
            fileName="datasource-dev.properties";
            input = Application.class.getClassLoader().getResourceAsStream(fileName);

        }else{
           // fileName="./datasource-prod.properties";
            fileName=path+"datasource-prod.properties";
            try {
                input = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        logger.debug("loadDataSourceProperties: "+fileName);
        try {
            dataSourceProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(input!=null){
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.debug("loadDataSourceProperties: "+ dataSourceProperties );
    }



    public void initApplicationProperties()   {
        InputStream source;
//        PropertiesService.initApplicationProperties();
        configPath = Paths.get(path + "bootstrap.yml");
        System.out.println("configPath :" + configPath);
        if (!Files.exists(configPath, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
            source = Thread.currentThread().getContextClassLoader().getResourceAsStream("bootstrap.yml");
            copyApplicationPropertiesFile(source, "bootstrap.yml", path);
        } else {
            System.out.println("configPath Bootstrap.yml IS EXIST :" + configPath);
        }

        configPath = Paths.get(path+"application.properties");
        logger.debug("configPath: "+configPath +" | "+Files.exists(configPath, LinkOption.NOFOLLOW_LINKS)+
                " | "+this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        if(!Files.exists(configPath, LinkOption.NOFOLLOW_LINKS)) {

            source = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
            copyApplicationPropertiesFile(source,"application.properties",path);
//            source = Thread.currentThread().getContextClassLoader().getResourceAsStream("datasource-dev.properties");
//            copyApplicationPropertiesFile(source,"datasource-dev.properties","./");
            source = Thread.currentThread().getContextClassLoader().getResourceAsStream("datasource-prod.properties");
            copyApplicationPropertiesFile(source,"datasource-prod.properties",path);
            source = Thread.currentThread().getContextClassLoader().getResourceAsStream("log4j.properties");
            copyApplicationPropertiesFile(source,"log4j.properties",path);
            source = Thread.currentThread().getContextClassLoader().getResourceAsStream("datasource-redis.properties");
            copyApplicationPropertiesFile(source,"datasource-redis.properties",path);
//            File sqlScriptDir =new File("./SQL Script/");
//            if (! sqlScriptDir.exists()){
//                sqlScriptDir.mkdir();
//            }
        }
        logger.debug("initApplicationProperties: "+env.getProperty("spring.profiles.active") );
        loadDataSourceProperties();
        loadRedisDataSourceProperties();
    }


    private  static String prod(String[] directory){
        String newPath="";
        for (int i=0;i<directory.length-1;i++){
            if(!directory[i].equals("")){
                if(!directory[i].contains("jar")){
                    if(i>0)
                        newPath=newPath+directory[i]+ File.separator;
                }else{
                    break;
                }

            }
        }
        return newPath;
    }



    private void copyApplicationPropertiesFile(InputStream in,String targetfilename,String path) {
        File targetFile = new File(path+File.separator+targetfilename);
        try {
            Files.copy(
                    in,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.debug("copyApplicationPropertiesFile Error : "+targetFile.toPath());
            e.printStackTrace();
        }
        IOUtils.closeQuietly(in);
    }

}
