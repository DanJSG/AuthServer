package com.jsg.authserver.datatypes;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsg.authserver.repositories.UserRepository;

public class User {
	
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
	
	public Boolean save(String connectionString, String username, String password) throws Exception {
		UserRepository repo = new UserRepository(connectionString, username, password);
		Boolean isSaved = repo.save(this);
		repo.closeConnection();
		return isSaved;
	}
	
	public Boolean verifyCredentials(UserRepository userRepo) throws Exception {
		List<User> results = userRepo.findWhereEqual("email", email, 1);
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
	
}
