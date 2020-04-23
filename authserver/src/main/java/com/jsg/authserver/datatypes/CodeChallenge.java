package com.jsg.authserver.datatypes;

import java.sql.Date;
import java.util.Calendar;

public class CodeChallenge {
	
	private String clientId;
	private String codeChallenge;
	private Date expires;
	
	public CodeChallenge(String clientId, String codeChallenge) {
		this.clientId = clientId;
		this.codeChallenge = codeChallenge;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		this.expires = new Date(calendar.getTimeInMillis());
	}
	
	public CodeChallenge(String clientId, String codeChallenge, Date expires) {
		this.clientId = clientId;
		this.codeChallenge = codeChallenge;
		this.expires = expires;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public String getCodeChallenge() {
		return this.codeChallenge;
	}
	
	public Date getExpiryDateTime() {
		return this.expires;
	}
	
}
