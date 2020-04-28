package com.jsg.authserver.datatypes;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.jsg.authserver.repositories.AuthCodeRepository;

public class AuthCode {
	
	private long userId;
	private String clientId;
	private String code;
	private Timestamp expires;
	
	public AuthCode(String clientId, long userId, String code) {
		this(clientId, userId, code, createExpiryTimestamp());
	}
	
	public AuthCode(String clientId, long userId, String code, Timestamp expires) {
		this.clientId = clientId;
		this.userId = userId;
		this.code = code;
		this.expires = expires;
	}
	
	public AuthCode(String clientId, String code) {
		this(clientId, -1, code, null);
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
	
	private static Timestamp createExpiryTimestamp() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, 1);
		return new Timestamp(calendar.getTimeInMillis());
	}
	
	public Boolean save(String connectionString, String username, String password) throws Exception {
		AuthCodeRepository authRepo = new AuthCodeRepository(connectionString, username, password);
		Boolean isSaved = authRepo.save(this);
		authRepo.closeConnection();
		return isSaved;
	}
	
	public Boolean verifyAuthCode(AuthCodeRepository authRepo) throws Exception {
		List<AuthCode> authCodes = authRepo.findWhereEqual("code", code, 1);
		if(authCodes == null || authCodes.size() < 1) {
			return false;
		}
		AuthCode authCode = authCodes.get(0);
		if(authCode.getExpiryDateTime().before(new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())) ||
				!authCode.getCode().contentEquals(code)) {
			return false;
		}
		userId = authCode.getUserId();
		expires = authCode.getExpiryDateTime();
		return true;
	}
	
}
