package com.jsg.authserver.datatypes;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class AuthCode {
	
	private String clientId;
	private String code;
	private LocalDateTime expires;
	
	public AuthCode(String clientId, String code) {
		this.clientId = clientId;
		this.code = code;
		this.expires = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(1);
	}
	
	public AuthCode(String clientId, String code, LocalDateTime expires) {
		this.clientId = clientId;
		this.code = code;
		this.expires = expires;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public LocalDateTime getExpiryDateTime() {
		return this.expires;
	}
	
}
