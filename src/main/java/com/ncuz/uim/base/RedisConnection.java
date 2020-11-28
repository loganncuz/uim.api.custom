package com.ncuz.uim.base;


import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class RedisConnection
{
    public Jedis getConnection() throws InterruptedException {
//		System.out.println("getConnection: "+ connectionPool.size()+" | isEmpty :"+connectionPool.isEmpty() +" | "+env.getProperty("spring.max.pool.size"));
        synchronized (connectionPool) {
            if (connectionPool.isEmpty()) {
                if ((usedConnections.size()+connectionPool.size()) < Integer.parseInt(env.getProperty("spring.max.pool.size"))) {
                    System.out.println("--------------------------------------------");
                    System.out.println("getConnection usedConnections: "+usedConnections.size()+
                            " | connectionPool :"+connectionPool.size()+" | total :"+(usedConnections.size()+connectionPool.size()));
                    connectionPool.add(createConnection(this.getHostname(),this.getPort(), this.getPassword(), this.getDb()));
                    System.out.println("--------------------------------------------");
                } else {
//					throw new RuntimeException(
//							"Maximum pool size reached, no available connections!");
                    while (connectionPool.isEmpty()) {
                        System.out.println("wait....");
                        connectionPool.wait();
                    }
                }
            }


            Jedis connection = connectionPool
                    .remove(connectionPool.size() - 1);
            usedConnections.add(connection);
//			System.out.println("getConnection usedConnections: "+ usedConnections.size()+" | isEmpty :"+usedConnections.isEmpty() );
//			System.out.println("getConnection connectionPool: "+ connectionPool.size()+" | isEmpty :"+connectionPool.isEmpty());
            return connection;
        }
    }
    public void releaseConnection(Jedis connection) {
        synchronized (connectionPool) {
            connectionPool.add(connection);
            usedConnections.remove(connection);
            connectionPool.notify();


//			System.out.println("releaseConnection usedConnections: "+ usedConnections.size()+" | isEmpty :"+usedConnections.isEmpty() );
//			System.out.println("releaseConnection connectionPool: "+ connectionPool.size()+" | isEmpty :"+connectionPool.isEmpty());
//			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
    }
    public void shutdown()  {
        usedConnections.forEach(this::releaseConnection);
        for (Jedis c : connectionPool) {
            c.close();
        }
        connectionPool.clear();
    }

    private   Jedis createConnection(
            String hostname,int port, String password, int db)
    {
//		System.out.println("createConnection: "+ connectionPool.isEmpty() );
        Jedis jedis =new Jedis(hostname,port);
        if(!password.equals("") && password !=null)
            jedis.auth(password);
        if(db>15)
            jedis.select(0);
        else
            jedis.select(db);

        return jedis;
    }
    public RedisConnection(Environment env , Logger logger, String hostname, int port, String password, int db)   {
        this.logger =logger;
        this.setPort(port);
        this.setHostname(hostname);
        this.setPassword(password);
        this.setDb(db);
        this.env=env;
//		System.out.println("JDBCConnection size : "+ connectionPool.size()+
//				" | spring.initial.pool.size :  "+Integer.parseInt(env.getProperty("spring.initial.pool.size")));
        if (usedConnections.size() ==0) {
            for (int i = 0; i < Integer.parseInt(env.getProperty("spring.initial.pool.size")); i++) {
                connectionPool.add(createConnection(this.getHostname(),this.getPort(), this.getPassword(), this.getDb()));
            }

        }else {
            throw new RuntimeException(
                    "Maximum pool size reached, no available connections!");
        }
//		System.out.println("RedisConnection initiate size : "+ connectionPool.size());
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Jedis> getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(List<Jedis> connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<Jedis> getUsedConnections() {
        return usedConnections;
    }

    public void setUsedConnections(List<Jedis> usedConnections) {
        this.usedConnections = usedConnections;
    }

    private int db;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private int port;
    private String hostname;
    private String password;
    private List<Jedis> connectionPool= new ArrayList<>();
    private List<Jedis> usedConnections = new ArrayList<>();
    private static Logger logger;

    private Environment env;
}
