package com.jsg.authserver.repositories;

import java.util.List;

public interface SQLRepository<T> {
	
	public <V> List<T> findWhereEqual(String searchColumn, V value);
	public <V> List<T> findWhereEqual(String searchColumn, V value, int limit);
	
	public Boolean closeConnection() throws Exception;
	
}
