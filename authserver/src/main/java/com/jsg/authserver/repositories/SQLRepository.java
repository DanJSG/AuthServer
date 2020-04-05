package com.jsg.authserver.repositories;

import java.util.List;

public interface SQLRepository<T> {
	
	public Boolean save(T item) throws Exception;
	
	public <V> List<T> findWhereEqual(String searchColumn, V value);
	public <V> List<T> findWhereEqual(String searchColumn, V value, int limit);
	
	public Boolean closeConnection() throws Exception;
	
}
