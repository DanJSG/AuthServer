package com.jsg.authserver.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsg.authserver.datatypes.User;

public class UserRepository extends MySQLRepository implements SQLRepository<User> { 
	
	public UserRepository() throws Exception {
		super.tableName = "users.accounts";
		super.openConnection();
	}

	@Override
	public Boolean save(User item) {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("email", item.getEmail());
		valueMap.put("password", item.getPassword());
		try {
			super.save(valueMap);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is already an email address in the database which matches: " + item.getEmail() + ".");
			return false;
		}
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

	@Override
	public <V, U> Boolean updateWhereEquals(String clauseColumn, V clauseValue, String updateColumn, U updateValue) throws Exception {
		try {
			super.updateWhereEquals(clauseColumn, clauseValue, updateColumn, updateValue);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
