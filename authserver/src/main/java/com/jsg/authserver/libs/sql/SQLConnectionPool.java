package com.jsg.authserver.libs.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class SQLConnectionPool {
	
	private static BasicDataSource dataSource = new BasicDataSource();
	
	@Autowired
	private SQLConnectionPool(@Value("${SQL_USERNAME}") String sqlUsername,
							  @Value("${SQL_PASSWORD}") String sqlPassword,
							  @Value("${SQL_CONNECTION_STRING}") String sqlConnectionString) {		
		dataSource.setUrl(sqlConnectionString);
		dataSource.setUsername(sqlUsername);
		dataSource.setPassword(sqlPassword);
		dataSource.setMaxActive(200);
		dataSource.setMaxIdle(150);
		dataSource.setMinIdle(10);
		dataSource.setMaxOpenPreparedStatements(200);
		dataSource.setMaxWait(2500); // ms
		dataSource.setRemoveAbandonedTimeout(60); // seconds
		dataSource.setMinEvictableIdleTimeMillis(30000);
		dataSource.setRemoveAbandoned(true);
		dataSource.setTimeBetweenEvictionRunsMillis(30000);
	}
	
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
}
