package com.jsg.authserver.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsg.authserver.datatypes.AuthCode;

public class AuthCodeRepository extends MySQLRepository implements SQLRepository<AuthCode> {

	public AuthCodeRepository(String connectionString, String username, String password) throws Exception {
		super(connectionString, username, password, "auth.codes");
		super.openConnection();
	}

	@Override
	public Boolean save(AuthCode item) throws Exception {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("client_id", item.getClientId());
		valueMap.put("code", item.getCode());
		valueMap.put("expires", item.getExpiryDateTime());
		try {
			super.save(valueMap);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public <V> List<AuthCode> findWhereEqual(String searchColumn, V value) {
		return findWhereEqual(searchColumn, value, 0);
	}

	@Override
	public <V> List<AuthCode> findWhereEqual(String searchColumn, V value, int limit) {
		try {
			ResultSet results = super.findWhereEquals(searchColumn, value, "*", limit);
			ArrayList<AuthCode> authCodes = new ArrayList<>();
			while(results.next()) {
				authCodes.add(new AuthCode(
						results.getString("client_id"),
						results.getString("code"),
						results.getDate("expires")));
			}
			if(authCodes.size() == 0) {
				return null;
			}
			return authCodes;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public <V, U> Boolean updateWhereEquals(String clauseColumn, V clauseValue, String updateColumn, U updateValue)
			throws Exception {
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
