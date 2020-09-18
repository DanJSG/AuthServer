package com.jsg.authserver.datatypes;

import java.sql.ResultSet;

import com.jsg.authserver.libs.sql.SQLEntityBuilder;

public class AppBuilder implements SQLEntityBuilder<App> {

	@Override
	public App fromResultSet(ResultSet sqlResults) {
		try {
			String clientId = sqlResults.getString("client_id");
			String redirectUri = sqlResults.getString("redirect_uri");
			String clientSecret = sqlResults.getString("client_secret");
			String accessTokenSecret = sqlResults.getString("access_token_secret");
			long accountId = sqlResults.getLong("associated_account_id");
			String name = sqlResults.getString("name");
			return new App(clientId, redirectUri, clientSecret, accessTokenSecret, accountId, name);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
