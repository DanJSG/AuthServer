package com.jsg.authserver.datatypes;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.jsg.authserver.auth.JWTHandler;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLEntity;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

public class TokenPair implements SQLEntity {
	
	private String clientId;
	private String cookieToken;
	private String headerToken;
	private long id;
	private Timestamp expires;
	private Boolean isExpired;
	
	public TokenPair(String clientId, String cookieToken, String headerToken, long id, Timestamp expires, Boolean isExpired) {
		this.clientId = clientId;
		this.cookieToken = cookieToken;
		this.headerToken = headerToken;
		this.id = id;
		this.expires = expires;
		this.isExpired = isExpired;
	}
	
	public TokenPair(String clientId, String cookieToken, String headerToken) {
		this(clientId, cookieToken, headerToken, -1, createExpiryTimestamp(), false);
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public String getCookieToken() {
		return cookieToken;
	}
	
	public String getHeaderToken() {
		return headerToken;
	}
	
	public long getId() {
		return id;
	}
	
	public Timestamp getExpiryDateTime() {
		return expires;
	}
	
	public Boolean isExpired() {
		return isExpired;
	}
	
	private static Timestamp createExpiryTimestamp() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.DATE, 28);
		return new Timestamp(calendar.getTimeInMillis());
	}
	
	public Boolean verifyRefreshTokens(String secret) {
		if(!JWTHandler.tokenIsValid(cookieToken, secret) || !JWTHandler.tokenIsValid(headerToken, secret)) {
			return false;
		}
		SQLRepository<TokenPair> tokenRepo = new MySQLRepository<>(SQLTable.TOKENS);
		List<TokenPair> results = tokenRepo.findWhereEqual(SQLColumn.COOKIETOKEN, cookieToken, 1, new TokenPairBuilder());
		if(results == null || results.size() < 1) {
			return false;
		}
		TokenPair tokenPair = results.get(0);
		if(tokenPair.isExpired()) {
			return false;
		}
		if(!tokenPair.getClientId().contentEquals(clientId)) {
			return false;
		}
		if(!headerToken.contentEquals(tokenPair.getHeaderToken())) {
			return false;
		}
		this.id = tokenPair.getId();
		this.isExpired = tokenPair.isExpired();
		return true;
	}

	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("cookieToken", cookieToken);
		map.put("headerToken", headerToken);
		map.put("expires", expires);
		map.put("client_id", clientId);
		return map;
	}
	
}
