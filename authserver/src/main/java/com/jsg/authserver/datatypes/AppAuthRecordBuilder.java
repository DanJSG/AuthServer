package com.jsg.authserver.datatypes;

import java.sql.ResultSet;

import com.jsg.authserver.libs.sql.SQLEntity;
import com.jsg.authserver.libs.sql.SQLEntityBuilder;

public class AppAuthRecordBuilder implements SQLEntityBuilder<AppAuthRecord> {

	@Override
	public AppAuthRecord fromResultSet(ResultSet sqlResults) {
		try {
			String clientId = sqlResults.getString("client_id");
			String redirectUri = sqlResults.getString("redirect_uri");
			String clientSecret = sqlResults.getString("client_secret");
			String accessTokenSecret = sqlResults.getString("access_token_secret");
			long accountId = sqlResults.getLong("associated_account_id");
			return new AppAuthRecord(clientId, redirectUri, clientSecret, accessTokenSecret, accountId);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
