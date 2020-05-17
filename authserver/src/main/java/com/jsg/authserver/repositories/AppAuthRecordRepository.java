package com.jsg.authserver.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsg.authserver.datatypes.AppAuthRecord;

public class AppAuthRecordRepository extends MySQLRepository implements SQLRepository<AppAuthRecord> {

	public AppAuthRecordRepository(String connectionString, String username, String password) throws Exception {
		super(connectionString, username, password, "auth.apps");
		super.openConnection();
	}

	@Override
	public Boolean save(AppAuthRecord item) {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("client_id", item.getClientId());
		valueMap.put("redirect_uri", item.getRedirectUri());
		valueMap.put("client_secret", item.getClientSecret());
		valueMap.put("accessTokenSecret", item.getAccessTokenSecret());
		try {
			super.save(valueMap);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public <V> List<AppAuthRecord> findWhereEqual(String searchColumn, V value) {
		return findWhereEqual(searchColumn, value, 0);
	}

	@Override
	public <V> List<AppAuthRecord> findWhereEqual(String searchColumn, V value, int limit) {
		try {
			ResultSet results = super.findWhereEquals(searchColumn, value, "*", limit);
			ArrayList<AppAuthRecord> records = new ArrayList<>();
			while(results.next()) {
				records.add(new AppAuthRecord(
						results.getString("client_id"),
						results.getString("redirect_uri"),
						results.getString("client_secret"),
						results.getString("access_token_secret")));
			}
			if(records.size() == 0) {
				return null;
			}
			return records;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
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

	@Override
	public Boolean closeConnection() throws Exception {
		return super.closeConnection();
	}

}
