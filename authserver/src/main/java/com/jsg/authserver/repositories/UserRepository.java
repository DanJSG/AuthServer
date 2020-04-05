package com.jsg.authserver.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.jsg.authserver.datatypes.User;

public class UserRepository extends MySQLRepository implements SQLRepository<User> { 
	
	public UserRepository() throws Exception {
		super.tableName = "users.accounts";
		super.openConnection();
	}

	@Override
	public <V> List<User> findWhereEqual(String searchColumn, V value) {
		return findWhereEqual(searchColumn, value, 0);
	}

	@Override
	public <V> List<User> findWhereEqual(String searchColumn, V value, int limit) {
		try {
			ResultSet results = super.findWhereEquals(searchColumn, value, "*", limit);
			ArrayList<User> users = new ArrayList<User>();
			while(results.next()) {
				users.add(new User(results.getString("email"), results.getString("password"), results.getLong("id")));
			}
			if(users.size() == 0) {
				return null;
			}
			return users;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Boolean closeConnection() throws Exception {
		return super.closeConnection();
	}
	
}
