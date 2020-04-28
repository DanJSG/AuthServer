package com.jsg.authserver.datatypes;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.jsg.authserver.repositories.TokenPairRepository;
import com.jsg.authserver.tokenhandlers.JWTHandler;

public class TokenPair {
	
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
	
	public Boolean save(String connectionString, String username, String password) throws Exception {
		TokenPairRepository repo = new TokenPairRepository(connectionString, username, password);
		Boolean isSaved = repo.save(this);
		repo.closeConnection();
		return isSaved;
	}
	
	public Boolean verifyRefreshTokens(TokenPairRepository tokenRepo, String secret) {
		if(!JWTHandler.tokenIsValid(cookieToken, secret) || !JWTHandler.tokenIsValid(headerToken, secret)) {
			return false;
		}
		List<TokenPair> results = tokenRepo.findWhereEqual("cookieToken", cookieToken, 1);
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
	
}
