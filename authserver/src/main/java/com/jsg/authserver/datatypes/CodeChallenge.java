package com.jsg.authserver.datatypes;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CodeChallenge {
	
	private String clientId;
	private String codeChallenge;
	private String state;
	private LocalDateTime expires;
	
	public CodeChallenge(String clientId, String codeChallenge, String state) {
		this.clientId = clientId;
		this.codeChallenge = codeChallenge;
		this.state = state;
		this.expires = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(1);
	}
	
	public CodeChallenge(String clientId, String codeChallenge, String state, LocalDateTime expires) {
		this.clientId = clientId;
		this.codeChallenge = codeChallenge;
		this.state = state;
		this.expires = expires;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public String getCodeChallenge() {
		return this.codeChallenge;
	}
	
	public String getState() {
		return this.state;
	}
	
	public LocalDateTime getExpiryDateTime() {
		return this.expires;
	}
	
}
