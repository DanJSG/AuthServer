package com.jsg.authserver.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

public abstract class MySQLRepository {

	protected String tableName;
	private Connection connection;
	
	protected void openConnection() throws Exception {
		Properties properties = new Properties();
		properties.put("user", "localDev");
		properties.put("password", "l0c4l_d3v!");
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/courier", properties);
	}
	
	protected Boolean closeConnection() throws Exception {
		if(connection.isClosed()) {
			return false;
		}
		connection.close();
		return true;
	}
	
	protected void save(Map<String, Object> valueMap) throws Exception {
		Object[] values = valueMap.values().toArray();
		String query = "INSERT INTO `" + tableName + "` (" + stringifyKeys(valueMap) + ") VALUES (" 
							+ createParamMarkers(values) + ");";
		PreparedStatement statement = connection.prepareStatement(query);
		for(int i=0; i < values.length; i++) {
			statement.setObject(i + 1, values[i]);
		}
		statement.execute();
	}
	
	protected <V> ResultSet findWhereEquals(String column, V value, String resultColumn, int limit) throws Exception {
		String query = "SELECT " + resultColumn + " FROM courier.`" + tableName + "` WHERE " + column + "=?;";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setFetchSize(limit);
		statement.setObject(1, value);
		ResultSet results = statement.executeQuery();
		return results;
	}
	
	private String stringifyKeys(Map<String, Object> valueMap) {
		String keyString = new String();
		Object[] keys = valueMap.keySet().toArray();
		for(int i=0; i< valueMap.keySet().size(); i++) {
			keyString += keys[i];
			if(i != valueMap.keySet().size() - 1) {
				keyString += ",";
			}
		}
		return keyString;
	}
	
	private String createParamMarkers(Object[] values) {
		String paramString = new String();
		for(int i=0; i < values.length; i++) {
			paramString += "?";
			if(i < values.length - 1) {
				paramString += ",";
			}
		}
		return paramString;
	}
	
}
