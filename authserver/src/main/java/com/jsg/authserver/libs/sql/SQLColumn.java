package com.jsg.authserver.libs.sql;

import java.util.HashMap;
import java.util.Map;

public enum SQLColumn implements Whitelist {
	
	USER_ID, STATE, REDIRECT_URI, PASSWORD, INDEX, ID, HEADERTOKEN,
	EXPIRES, EXPIRED, EMAIL, DISPLAYNAME, DATECREATED, DATECHANGED,
	COOKIETOKEN, CODE_CHALLENGE, CODE, CLIENT_SECRET, CLIENT_ID,
	BIO, ASSOCIATED_ACCOUNT_ID, ACCESS_TOKEN_SECRET;
	
	private static final Map<String, SQLColumn> mapping = new HashMap<>(16);
	
	static {
		for(SQLColumn column : SQLColumn.values()) {
			mapping.put(column.name(), column);
		}
	}

	@Override
	public boolean validate(String value) {
		return mapping.containsKey(value);
	}
	
}
