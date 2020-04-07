package com.jsg.authserver.repositories;

import java.util.List;

public interface SQLRepository<T> {
	
	public Boolean save(T item) throws Exception;
	
	/**
	 * NEVER PASS COLUMN NAME VALUES FROM THE CLIENT. THIS CREATES A SQLi VULNERABILITY.
	 * @param <V>
	 * @param searchColumn
	 * @param value
	 * @return
	 */
	public <V> List<T> findWhereEqual(String searchColumn, V value);
	
	/**
	 * NEVER PASS COLUMN NAME VALUES FROM THE CLIENT. THIS CREATES A SQLi VULNERABILITY.
	 * @param <V>
	 * @param searchColumn
	 * @param value
	 * @param limit
	 * @return
	 */
	public <V> List<T> findWhereEqual(String searchColumn, V value, int limit);
	
	/**
	 * NEVER PASS COLUMN NAME VALUES FROM THE CLIENT. THIS CREATES A SQLi VULNERABILITY.
	 * @param <V>
	 * @param <U>
	 * @param clauseColumn
	 * @param clauseValue
	 * @param updateColumn
	 * @param updateValue
	 * @throws Exception
	 */
	public <V, U> Boolean updateWhereEquals(String clauseColumn, V clauseValue, String updateColumn, U updateValue) throws Exception;
	
	public Boolean closeConnection() throws Exception;
	
}
