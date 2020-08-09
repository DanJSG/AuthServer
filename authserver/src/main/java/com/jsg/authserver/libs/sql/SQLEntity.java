package com.jsg.authserver.libs.sql;

import java.util.Map;

public interface SQLEntity {
	
	public Map<String, Object> toSqlMap();
	
}
