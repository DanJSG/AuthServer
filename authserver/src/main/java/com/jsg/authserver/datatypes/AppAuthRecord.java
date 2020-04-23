package com.jsg.authserver.datatypes;

public class AppAuthRecord {
	
	private String clientId;
	private String redirectUri;
	private String clientSecret;
	
	public AppAuthRecord(String clientId, String redirectUri, String clientSecret) {
		this.clientId = clientId;
		this.redirectUri = redirectUri;
		this.clientSecret = clientSecret;
	}
	
	public AppAuthRecord(String clientId, String redirectUri) {
		this(clientId, redirectUri, null);
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public String getRedirectUri() {
		return this.redirectUri;
	}
	
	public String getClientSecret() {
		return this.clientSecret;
	}
	
}
