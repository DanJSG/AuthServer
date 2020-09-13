package com.jsg.authserver.datatypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLEntity;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

public class User implements SQLEntity {
	
	@JsonProperty
	private long id;
	
	@JsonProperty
	private String email;
	
	private String password;
		
	public User() {}
	
	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public User(String email, String password, long id) {
		this.email = email;
		this.password = password;
		this.id = id;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void clearPassword() {
		this.password = null;
	}
	
	public Boolean verifyCredentials() throws Exception {
		SQLRepository<User> userRepo = new MySQLRepository<>(SQLTable.ACCOUNTS);
		List<User> results = userRepo.findWhereEqual(SQLColumn.EMAIL, email, 1, new UserBuilder());
		if(results == null || results.size() < 1) {
			return false;
		}
		User user = results.get(0);
		if(!BCrypt.checkpw(password, user.getPassword())) {
			return false;
		}
		user.clearPassword();
		this.id = user.getId();
		return true;
	}

	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("email", email);
		map.put("password", password);
		return map;
	}
	
}
