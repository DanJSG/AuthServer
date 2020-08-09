package com.jsg.authserver.datatypes;

import java.sql.ResultSet;
import java.sql.Timestamp;

import com.jsg.authserver.libs.sql.SQLEntityBuilder;

public class AuthCodeBuilder implements SQLEntityBuilder<AuthCode> {

	@Override
	public AuthCode fromResultSet(ResultSet sqlResults) {
		try {
			String clientId = sqlResults.getString("client_id");
			long userId = sqlResults.getLong("user_id");
			String code = sqlResults.getString("code");
			Timestamp expires = sqlResults.getTimestamp("expires");
			return new AuthCode(clientId, userId, code, expires);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
