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
	
}
