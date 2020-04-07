package com.jsg.authserver.datatypes;

public class TokenPair {
	
	private String cookieToken;
	
	private String headerToken;
	
	private long id;
	
	public TokenPair(String cookieToken, String headerToken, long id) {
		this.cookieToken = cookieToken;
		this.headerToken = headerToken;
		this.id = id;
	}
	
	public TokenPair(String cookieToken, String headerToken) {
		this(cookieToken, headerToken, -1);
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
	
}
