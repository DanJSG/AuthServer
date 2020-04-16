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
	
}
