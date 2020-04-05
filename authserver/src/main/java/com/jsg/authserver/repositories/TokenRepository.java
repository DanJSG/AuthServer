package com.jsg.authserver.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenRepository extends MySQLRepository implements SQLRepository<String[]> {
		
//	private static final int msDay = 86400000;
	
	public TokenRepository() throws Exception {
		super.tableName = "auth.tokens";
		super.openConnection();
	}
	
	@Override
	public Boolean save(String[] item) throws Exception {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("tokenA", item[0]);
		valueMap.put("tokenB", item[1]);
//		valueMap.put("expires", new Date(Calendar.getInstance().getTimeInMillis() - (msDay * 28)));
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
	public <V> List<String[]> findWhereEqual(String searchColumn, V value) {
		return findWhereEqual(searchColumn, value, 0);
	}

	@Override
	public <V> List<String[]> findWhereEqual(String searchColumn, V value, int limit) {
		try {
			ResultSet results = super.findWhereEquals(searchColumn, value, "*", limit);
			ArrayList<String[]> tokens = new ArrayList<>();
			while(results.next()) {
				tokens.add(new String[] {results.getString("tokenA"), results.getString("tokenB")});
			}
			for(String[] token : tokens) {
				System.out.println("Token pair:");
				System.out.println(token[0]);
				System.out.println(token[1]);
			}
			return tokens;
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
