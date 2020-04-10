package com.jsg.authserver.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsg.authserver.datatypes.TokenPair;

public final class TokenRepository extends MySQLRepository implements SQLRepository<TokenPair> {
			
	public TokenRepository(String connectionString, String username, String password) throws Exception {
		super(connectionString, username, password, "auth.tokens");
		super.openConnection();
	}
	
	@Override
	public Boolean save(TokenPair item) throws Exception {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("tokenA", item.getCookieToken());
		valueMap.put("tokenB", item.getHeaderToken());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 28);
		valueMap.put("expires", new Date(calendar.getTimeInMillis()));
		try {
			super.save(valueMap);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to add tokens to store.");
			return false;
		}
	}
	
	@Override
	public <V> List<TokenPair> findWhereEqual(String searchColumn, V value) {
		return findWhereEqual(searchColumn, value, 0);
	}

	@Override
	public <V> List<TokenPair> findWhereEqual(String searchColumn, V value, int limit) {
		try {
			ResultSet results = super.findWhereEquals(searchColumn, value, "*", limit);
			ArrayList<TokenPair> tokens = new ArrayList<>();
			while(results.next()) {
				tokens.add(new TokenPair(results.getString("tokenA"), results.getString("tokenB"), results.getLong("id"), results.getBoolean("expired")));
			}
			return tokens;
		} catch (Exception e) {
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
