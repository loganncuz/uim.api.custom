package com.ncuz.uim.base;

 
import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCConnection {
	private static final int MAX_POOL_SIZE = 10;
	private String connectionURL;
	private String username;
	private String password;
	private List<Connection> connectionPool= new ArrayList<>();
	private List<Connection> usedConnections = new ArrayList<>();
	private   int INITIAL_POOL_SIZE = 1;


	private static Logger logger;

	private Environment env;

	public JDBCConnection(Environment env, Logger logger, String connectionURL, String username, String password) throws SQLException {
		this.logger =logger;
		this.setConnectionURL(connectionURL);
		this.setUsername(username);
		this.setPassword(password);
		this.env=env;
//		logger.debug("JDBCConnection size : "+ connectionPool.size()+
//				" | spring.initial.pool.size :  "+Integer.parseInt(env.getProperty("spring.initial.pool.size")));
		if (usedConnections.size() ==0) {
			for (int i = 0; i < Integer.parseInt(env.getProperty("spring.initial.pool.size")); i++) {
				connectionPool.add(createConnection(this.getConnectionURL(), this.getUsername(), this.getPassword()));
			}

		}else {
			throw new RuntimeException(
					"Maximum pool size reached, no available connections!");
		}
//		logger.debug("JDBCConnection initiate size : "+ connectionPool.size());
	}
	
	public Connection getConnection() throws SQLException, InterruptedException {
//		logger.debug("getConnection: "+ connectionPool.size()+" | isEmpty :"+connectionPool.isEmpty() +" | "+env.getProperty("spring.max.pool.size"));
		synchronized (connectionPool) {
			if (connectionPool.isEmpty()) {
				if ((usedConnections.size()+connectionPool.size()) < Integer.parseInt(env.getProperty("spring.max.pool.size"))) {
					logger.debug("--------------------------------------------");
					logger.debug("getConnection usedConnections: "+usedConnections.size()+
							" | connectionPool :"+connectionPool.size()+" | total :"+(usedConnections.size()+connectionPool.size()));
					connectionPool.add(createConnection(this.getConnectionURL(), this.getUsername(), this.getPassword()));
					logger.debug("--------------------------------------------");
				} else {
//					throw new RuntimeException(
//							"Maximum pool size reached, no available connections!");
					while (connectionPool.isEmpty()) {
						logger.debug("wait....");
						connectionPool.wait();
					}
				}
			}


			Connection connection = connectionPool
					.remove(connectionPool.size() - 1);
			usedConnections.add(connection);
//			logger.debug("getConnection usedConnections: "+ usedConnections.size()+" | isEmpty :"+usedConnections.isEmpty() );
//			logger.debug("getConnection connectionPool: "+ connectionPool.size()+" | isEmpty :"+connectionPool.isEmpty());
			return connection;
		}
	}





	public void releaseConnection(Connection connection) {
		synchronized (connectionPool) {
			connectionPool.add(connection);
			usedConnections.remove(connection);
			connectionPool.notify();


//			logger.debug("releaseConnection usedConnections: "+ usedConnections.size()+" | isEmpty :"+usedConnections.isEmpty() );
//			logger.debug("releaseConnection connectionPool: "+ connectionPool.size()+" | isEmpty :"+connectionPool.isEmpty());
//			logger.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		}
	}

	public void shutdown() throws SQLException {
		usedConnections.forEach(this::releaseConnection);
		for (Connection c : connectionPool) {
			c.close();
		}
		connectionPool.clear();
	}

	private   Connection createConnection(
			String url, String user, String password)
			throws SQLException {
//		logger.debug("createConnection: "+ connectionPool.isEmpty() );
		return DriverManager.getConnection(url, user, password);
	}

	public int getSize() {
		return connectionPool.size() + usedConnections.size();
	}

	public String getConnectionURL() {
		return connectionURL;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
