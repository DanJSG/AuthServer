package com.jsg.authserver.datatypes;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class CodeChallenge {
	
	private String clientId;
	private String codeChallenge;
	private String state;
	private Timestamp expires;
	
	public CodeChallenge(String clientId, String codeChallenge, String state) {
		this.clientId = clientId;
		this.codeChallenge = codeChallenge;
		this.state = state;
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, 1);
		this.expires = new Timestamp(calendar.getTimeInMillis());
	}
	
	public CodeChallenge(String clientId, String codeChallenge, String state, Timestamp expires) {
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
	
	public Timestamp getExpiryDateTime() {
		return this.expires;
	}
	
}
