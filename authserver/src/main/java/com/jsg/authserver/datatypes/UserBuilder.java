package com.jsg.authserver.datatypes;

import java.sql.ResultSet;

import com.jsg.authserver.libs.sql.SQLEntityBuilder;

public class UserBuilder implements SQLEntityBuilder<User> {

	@Override
	public User fromResultSet(ResultSet sqlResults) {
		try {
			String email = sqlResults.getString("email"); 
			String password = sqlResults.getString("password"); 
			long id = sqlResults.getLong("id");
			return new User(email, password, id);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
