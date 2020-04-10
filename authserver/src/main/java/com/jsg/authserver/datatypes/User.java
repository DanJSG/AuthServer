package com.jsg.authserver.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	
}
