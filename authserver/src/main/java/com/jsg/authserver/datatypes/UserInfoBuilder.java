package com.jsg.authserver.datatypes;

import java.sql.ResultSet;

import com.jsg.authserver.libs.sql.SQLEntityBuilder;

public class UserInfoBuilder implements SQLEntityBuilder<UserInfo> {

	@Override
	public UserInfo fromResultSet(ResultSet sqlResults) {
		try {
			long id = sqlResults.getLong("id");
			String displayName = sqlResults.getString("displayname"); 
			String bio = sqlResults.getString("bio");
			return new UserInfo(id, displayName, bio);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
