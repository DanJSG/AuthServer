package com.jsg.authserver.datatypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.authserver.helpers.JsonObject;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLEntity;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

public class App implements SQLEntity, JsonObject {
	
	@JsonIgnore
	private String clientSecret;
	
	@JsonIgnore
	private String accessTokenSecret;
	
	@JsonIgnore
	private long associatedAccountId;

	@JsonProperty
	private String name;
	
	@JsonProperty
	private String redirectUri;
	
	@JsonProperty
	@JsonInclude(Include.NON_EMPTY)
	private String clientId;

	@JsonCreator
	private App() {}
	
	public App(String clientId, String redirectUri, String clientSecret, 
			String accessTokenSecret, long associatedAccountId, String name) {
		this.clientId = clientId;
		this.redirectUri = redirectUri;
		this.clientSecret = clientSecret;
		this.accessTokenSecret = accessTokenSecret;
		this.associatedAccountId = associatedAccountId;
		this.name = name;
	}
	
	public App(String clientId, String redirectUri) {
		this(clientId, redirectUri, null, null, -1, null);

	}
	
	public App(String clientId, String redirectUri, String name) {
		this(clientId, redirectUri, null, null, -1, name);
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
	
	public long getAssociatedAccountId() {
		return this.associatedAccountId;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Boolean verifyAppAuthRecord() throws Exception {
		SQLRepository<App> appRepo = new MySQLRepository<>(SQLTable.APPS);
		List<App> appList = appRepo.findWhereEqual(SQLColumn.CLIENT_ID, clientId, 1, new AppBuilder());
		if(appList == null || appList.size() < 1) {
			return false;
		}
		App app = appList.get(0);
		if(!app.getRedirectUri().contentEquals(redirectUri)) {
			return false;
		}
		clientSecret = app.getClientSecret();
		return true;
	}

	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("client_id", clientId);
		map.put("redirect_uri", redirectUri);
		map.put("client_secret", clientSecret);
		map.put("access_token_secret", accessTokenSecret);
		map.put("associated_account_id", associatedAccountId);
		map.put("name", name);
		return map;
	}

	@Override
	public String writeValueAsString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
