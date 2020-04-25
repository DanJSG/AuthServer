package com.jsg.authserver.datatypes;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class AuthCode {
	
	private long userId;
	private String clientId;
	private String code;
	private Timestamp expires;
	
	public AuthCode(String clientId, long userId, String code) {
		this.clientId = clientId;
		this.userId = userId;
		this.code = code;
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, 1);
		this.expires = new Timestamp(calendar.getTimeInMillis());
	}
	
	public AuthCode(String clientId, long userId, String code, Timestamp expires) {
		this.clientId = clientId;
		this.userId = userId;
		this.code = code;
		this.expires = expires;
	}
	
	public long getUserId() {
		return this.userId;
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public Timestamp getExpiryDateTime() {
		return this.expires;
	}
	
}
