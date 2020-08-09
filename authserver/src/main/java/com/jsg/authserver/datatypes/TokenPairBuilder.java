package com.jsg.authserver.datatypes;

import java.sql.ResultSet;
import java.sql.Timestamp;

import com.jsg.authserver.libs.sql.SQLEntityBuilder;

public class TokenPairBuilder implements SQLEntityBuilder<TokenPair> {

	@Override
	public TokenPair fromResultSet(ResultSet sqlResults) {
		try {
			String clientId = sqlResults.getString("client_id");
			String cookieToken = sqlResults.getString("cookieToken");
			String headerToken = sqlResults.getString("headerToken");
			long id = sqlResults.getLong("id");
			Timestamp expires = sqlResults.getTimestamp("expires");
			boolean isExpired = sqlResults.getBoolean("expired");
			return new TokenPair(clientId, cookieToken, headerToken, id, expires, isExpired);
		} catch(Exception e) {
			
		}
		
		return null;
	}

}
