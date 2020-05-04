package com.jsg.authserver.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsg.authserver.datatypes.UserInfo;

public class UserInfoRepository extends MySQLRepository implements SQLRepository<UserInfo> {

	public UserInfoRepository(String connectionString, String username, String password) throws Exception  {
		super(connectionString, username, password, "users.info");
		super.openConnection();
	}
	
	@Override
	public Boolean save(UserInfo item) throws Exception {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("id", item.getId());
		valueMap.put("displayname", item.getDisplayName());
		valueMap.put("bio", item.getBio());
		try {
			super.save(valueMap);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is already a user in the database with the ID: " + item.getId() + ".");
			return false;
		}
	}

	@Override
	public <V> List<UserInfo> findWhereEqual(String searchColumn, V value) {
		return findWhereEqual(searchColumn, value, 0);
	}

	@Override
	public <V> List<UserInfo> findWhereEqual(String searchColumn, V value, int limit) {
		try {
			ResultSet results = super.findWhereEquals(searchColumn, value, "*", limit);
			ArrayList<UserInfo> usersInfo = new ArrayList<>();
			while(results.next()) {
				usersInfo.add(new UserInfo(results.getLong("id"), results.getString("displayname"), results.getString("bio")));
			}
			if(usersInfo.size() == 0) {
				throw new Exception();
			}
			return usersInfo;
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
