package com.jsg.authserver.datatypes;

import java.sql.Date;
import java.util.Calendar;

public class AuthCode {
	
	private String clientId;
	private String code;
	private Date expires;
	
	public AuthCode(String clientId, String code) {
		this.clientId = clientId;
		this.code = code;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 1);
		this.expires = new Date(calendar.getTimeInMillis());
	}
	
	public AuthCode(String clientId, String code, Date expires) {
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
	
	public Date getExpiryDateTime() {
		return this.expires;
	}
	
}
