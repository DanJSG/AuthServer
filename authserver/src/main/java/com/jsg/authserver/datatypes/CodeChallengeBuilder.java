package com.jsg.authserver.datatypes;

import java.sql.ResultSet;
import java.sql.Timestamp;

import com.jsg.authserver.libs.sql.SQLEntityBuilder;

public class CodeChallengeBuilder implements SQLEntityBuilder<Challenge> {

	@Override
	public Challenge fromResultSet(ResultSet sqlResults) {
		try {
			String clientId = sqlResults.getString("client_id");
			String codeChallenge = sqlResults.getString("code_challenge");
			String state = sqlResults.getString("state");
			Timestamp expires = sqlResults.getTimestamp("expires");
			return new Challenge(clientId, codeChallenge, state, expires);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
