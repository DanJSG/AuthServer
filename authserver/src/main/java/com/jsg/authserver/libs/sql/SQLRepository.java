package com.jsg.authserver.libs.sql;

import java.util.List;

public interface SQLRepository<T extends SQLEntity> {
	
	public Boolean save(T item);
	
	public Boolean saveMany(List<T> objects);
	
	public <V> List<T> findWhereEqual(SQLColumn searchColumn, V value, SQLEntityBuilder<T> builder);
	
	public <V> List<T> findWhereEqual(SQLColumn searchColumn, V value, int limit, SQLEntityBuilder<T> builder);
	
	public <V> List<T> findWhereEqual(List<SQLColumn> searchColumns, List<V> values, int limit, SQLEntityBuilder<T> builder);
	
	public <V> List<T> findWhereEqual(List<SQLColumn> searchColumns, List<V> values, SQLEntityBuilder<T> builder);
	
	public <V> List<T> findWhereLike(SQLColumn searchColumn, V value, SQLEntityBuilder<T> builder);
	
	public <V> List<T> findWhereLike(SQLColumn searchColumn, V value, int limit, SQLEntityBuilder<T> builder);
	
	public <V, U> Boolean updateWhereEquals(SQLColumn clauseColumn, V clauseValue, SQLColumn updateColumn, U updateValue);
	
}
