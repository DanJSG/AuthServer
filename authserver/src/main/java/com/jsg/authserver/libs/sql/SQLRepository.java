package com.jsg.authserver.libs.sql;

import java.util.List;

public interface SQLRepository<T extends SQLEntity> {
	
	public Boolean save(T item);
	
	public Boolean saveMany(List<T> objects);
	
	public <V> List<T> findWhereEqual(String searchColumn, V value, SQLEntityBuilder<T> builder);
	
	public <V> List<T> findWhereEqual(String searchColumn, V value, int limit, SQLEntityBuilder<T> builder);
	
	public <V> List<T> findWhereLike(String searchColumn, V value, SQLEntityBuilder<T> builder);
	
	public <V> List<T> findWhereLike(String searchColumn, V value, int limit, SQLEntityBuilder<T> builder);
	
	public <V, U> Boolean updateWhereEquals(String clauseColumn, V clauseValue, String updateColumn, U updateValue);
	
}
