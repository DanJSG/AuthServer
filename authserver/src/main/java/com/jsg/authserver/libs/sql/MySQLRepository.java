package com.jsg.authserver.libs.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQLRepository<T extends SQLEntity> implements SQLRepository<T>{
	
	private final String tableName;
	
	public MySQLRepository(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public Boolean save(T object) {
		return saveMany(Arrays.asList(object));
	}
	
	@Override
	public Boolean saveMany(List<T> objects) {
		Connection connection = getConnection();
		if(connection == null || objects.size() == 0) {
			return false;
		}
		Map<String, Object> valueMap = objects.get(0).toSqlMap();
		Object[] values = valueMap.values().toArray();
		String query = 
				"INSERT INTO `" + tableName + "` (" + stringifyKeys(valueMap) + 
				") VALUES (" + createParamMarkers(values) + ")";
		PreparedStatement statement;
		try {
			int count = 0;
			statement = connection.prepareStatement(query);
			for(T object : objects) {
				if(count > 0) {
					valueMap = object.toSqlMap();
					values = valueMap.values().toArray();
				}
				for(int i=0; i < values.length; i++) {
					statement.setObject(i + 1, values[i]);
				}
				statement.addBatch();
				count++;
				if(count % 500 == 0 || count == objects.size()) {
					System.out.println("Executing batched statement!");
					statement.executeBatch();
					connection.commit();
					connection.close();
				}
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	public <V> List<T> findWhereEqual(String searchColumn, V value, int limit, SQLEntityBuilder<T> builder) {
		Connection connection = getConnection();
		if(connection == null) {
			return null;
		}
		if(!checkColumnName(searchColumn)) {
			System.out.println("Column name contains dangerous characters.");
			return null;
		}
		String query = "SELECT * FROM `" + tableName + "` WHERE " + searchColumn + "=?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setFetchSize(limit);
			statement.setObject(1, value);
			ResultSet results = statement.executeQuery();
			connection.commit();
			ArrayList<T> objectList = new ArrayList<>();
			while(results.next()) {
				objectList.add(builder.fromResultSet(results));
			}
			if(objectList.size() == 0) {
				connection.close();
				return null;
			}
			connection.close();
			return objectList;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public <V> List<T> findWhereLike(String searchColumn, V value, int limit, SQLEntityBuilder<T> builder) {
		Connection connection = getConnection();
		if(connection == null) {
			return null;
		}
		if(!checkColumnName(searchColumn)) {
			System.out.println("Column name contains dangerous characters.");
			return null;
		}
		String query = "SELECT * FROM `" + tableName + "` WHERE " + searchColumn + " LIKE ?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setFetchSize(limit);
			statement.setObject(1, value);
			ResultSet results = statement.executeQuery();
			connection.commit();
			ArrayList<T> objectList = new ArrayList<>();
			while(results.next()) {
				objectList.add(builder.fromResultSet(results));
			}
			if(objectList.size() == 0) {
				connection.close();
				return null;
			}
			connection.close();
			return objectList;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public <V> List<T> findWhereLike(String searchColumn, V value, SQLEntityBuilder<T> builder) {
		return findWhereLike(searchColumn, value, 0, builder);
	}
	
	@Override
	public <V> List<T> findWhereEqual(String searchColumn, V value, SQLEntityBuilder<T> builder) {
		return findWhereEqual(searchColumn, value, 0, builder);
	}
	
	public <V, U> Boolean updateWhereEquals(String clauseColumn, V clauseValue, String updateColumn, U updateValue) {
		Connection connection = getConnection();
		if(connection == null) {
			return false;
		}
		if(!checkColumnName(clauseColumn) || !checkColumnName(updateColumn)) {
			System.out.println("Column name contains dangerous characters.");
			return false;
		}
		String query = "UPDATE `" + tableName + "` SET " + updateColumn + "= ? WHERE " + clauseColumn + " = ?;";
		try {
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setObject(1, updateValue);
			statement.setObject(2, clauseValue);
			statement.executeUpdate();
			connection.commit();
			connection.close();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
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
	
	private Boolean checkColumnName(String columnName) {
		// Blocks SQL column name from including dangerous input:
		// spaces or --. This helps prevent SQL injection through a badly 
		// implemented query.
		Pattern blacklist = Pattern.compile("[ ][--]*");
		Matcher matcher = blacklist.matcher(columnName);
		return !matcher.find();
	}
	
	private Connection getConnection() {
		try {
			Connection connection = SQLConnectionPool.getConnection();
			connection.setAutoCommit(false);
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
