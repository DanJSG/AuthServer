package com.jsg.authserver.datatypes;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLEntity;
import com.jsg.authserver.libs.sql.SQLRepository;

public class AuthCode implements SQLEntity {
	
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
	
	public Boolean verifyAuthCode() throws Exception {
		SQLRepository<AuthCode> authRepo = new MySQLRepository<>("auth.codes");
		List<AuthCode> authCodes = authRepo.findWhereEqual("code", code, 1, new AuthCodeBuilder());
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

	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("client_id", clientId);
		map.put("user_id", userId);
		map.put("code", code);
		map.put("expires", expires);
		return map;
	}
	
}
