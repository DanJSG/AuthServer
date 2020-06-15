package com.jsg.authserver.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

public abstract class MySQLRepository {

	private String tableName;
	private Connection connection;
	
	private final String connectionString;
	private final String sqlUsername;
	private final String sqlPassword;
	
	protected MySQLRepository(String connectionString, String username, String password, String tableName) {
		this.connectionString = connectionString;
		this.sqlUsername = username;
		this.sqlPassword = password;
		this.tableName = tableName;
	}
	
	protected void openConnection() throws Exception {
		Properties properties = new Properties();
		properties.put("user", sqlUsername);
		properties.put("password", sqlPassword);
		connection = DriverManager.getConnection(connectionString, properties);
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
		String query = "SELECT " + resultColumn + " FROM `" + tableName + "` WHERE " + column + "=?;";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setFetchSize(limit);
		statement.setObject(1, value);
		ResultSet results = statement.executeQuery();
		return results;
	}
	
	protected <V, U> Boolean updateWhereEquals(String clauseColumn, V clauseValue, String updateColumn, U updateValue) throws Exception {
		String query = "UPDATE `" + tableName + "` SET " + updateColumn + "= ? WHERE " + clauseColumn + " = ?;";
		PreparedStatement statement = connection.prepareStatement(query);
		statement.setObject(1, updateValue);
		statement.setObject(2, clauseValue);
		statement.executeUpdate();
		return true;
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
