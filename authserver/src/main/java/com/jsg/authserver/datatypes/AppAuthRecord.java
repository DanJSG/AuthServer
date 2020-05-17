package com.jsg.authserver.datatypes;

import java.util.List;

import com.jsg.authserver.repositories.AppAuthRecordRepository;

public class AppAuthRecord {
	
	private String clientId;
	private String redirectUri;
	private String clientSecret;
	private String accessTokenSecret;
	
	public AppAuthRecord(String clientId, String redirectUri, String clientSecret, String accessTokenSecret) {
		this.clientId = clientId;
		this.redirectUri = redirectUri;
		this.clientSecret = clientSecret;
		this.accessTokenSecret = accessTokenSecret;
	}
	
	public AppAuthRecord(String clientId, String redirectUri) {
		this(clientId, redirectUri, null, null);
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
	
	public String getAccessTokenSecret() {
		return this.accessTokenSecret;
	}
	
	public Boolean verifyAppAuthRecord(AppAuthRecordRepository appRepo) throws Exception {
		List<AppAuthRecord> appList = appRepo.findWhereEqual("client_id", clientId, 1);
		if(appList == null || appList.size() < 1) {
			return false;
		}
		AppAuthRecord app = appList.get(0);
		if(!app.getRedirectUri().contentEquals(redirectUri)) {
			return false;
		}
		clientSecret = app.getClientSecret();
		return true;
	}
	
}
