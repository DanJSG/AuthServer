package com.jsg.authserver.datatypes;

public class TokenPair {
	
	private String clientId;
	private String cookieToken;
	private String headerToken;
	private long id;
	private Boolean isExpired;
	
	public TokenPair(String clientId, String cookieToken, String headerToken, long id, Boolean isExpired) {
		this.clientId = clientId;
		this.cookieToken = cookieToken;
		this.headerToken = headerToken;
		this.id = id;
		this.isExpired = isExpired;
	}
	
	public TokenPair(String clientId, String cookieToken, String headerToken, Boolean isExpired) {
		this(clientId, cookieToken, headerToken, -1, isExpired);
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
	
	public Boolean isExpired() {
		return isExpired;
	}
	
}
