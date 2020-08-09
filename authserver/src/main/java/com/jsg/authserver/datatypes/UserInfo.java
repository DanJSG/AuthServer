package com.jsg.authserver.datatypes;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsg.authserver.libs.sql.SQLEntity;
import com.jsg.authserver.repositories.UserInfoRepository;

public class UserInfo implements SQLEntity {

	@JsonProperty
	private long id;
	
	@JsonProperty
	private String displayName;
	
	@JsonProperty
	private String bio;
	
	public UserInfo() {}
	
	public UserInfo(long id, String name) {
		this(id, name, "");
	}
	
	public UserInfo(long id, String displayName, String bio) {
		this.id = id;
		this.displayName = displayName;
		this.bio = bio;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public String getBio() {
		return this.bio;
	}
	
	public Boolean save(String connectionString, String username, String password) throws Exception {
		UserInfoRepository repo = new UserInfoRepository(connectionString, username, password);
		Boolean isSaved = repo.save(this);
		repo.closeConnection();
		return isSaved;
	}

	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("displayname", displayName);
		map.put("bio", bio);
		return map;
	}
	
}
