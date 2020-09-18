package com.jsg.authserver.datatypes;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.tomcat.util.codec.binary.Base64;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLEntity;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

public class Challenge implements SQLEntity {
	
	private String clientId;
	private String challenge;
	private String state;
	private Timestamp expires;
	
	public Challenge(String clientId, String codeChallenge, String state) {
		this(clientId, codeChallenge, state, createExpiryTimestamp());
	}
	
	public Challenge(String clientId, String codeChallenge, String state, Timestamp expires) {
		this.clientId = clientId;
		this.challenge = codeChallenge;
		this.state = state;
		this.expires = expires;
	}
	
	public Challenge(String clientId, String state) {
		this(clientId, null, state, null);
	}
	
	public String getClientId() {
		return this.clientId;
	}
	
	public String getCodeChallenge() {
		return this.challenge;
	}
	
	public String getState() {
		return this.state;
	}
	
	public Timestamp getExpiryDateTime() {
		return this.expires;
	}
	
	private static Timestamp createExpiryTimestamp() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, 1);
		return new Timestamp(calendar.getTimeInMillis());
	}
	
	public Boolean verifyCodeChallenge(String code_verifier) throws Exception {
		SQLRepository<Challenge> challengeRepo = new MySQLRepository<>(SQLTable.CHALLENGES);
		List<Challenge> codeChallenges = challengeRepo.findWhereEqual(SQLColumn.STATE, state, new CodeChallengeBuilder());
		if(codeChallenges == null || codeChallenges.size() < 1) {
			return false;
		}
		Challenge codeChallenge = codeChallenges.get(0);
		if(codeChallenge.getExpiryDateTime().before(new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())) || 
				!codeChallenge.getClientId().contentEquals(clientId)) {
			return false;
		}
		String codeHash = Hashing.sha256().hashString(code_verifier, Charsets.UTF_8).toString();
		String b64urlCodeHash = Base64.encodeBase64URLSafeString(codeHash.getBytes());
		if(!codeChallenge.getCodeChallenge().contentEquals(b64urlCodeHash)) {
			return false;
		}
		expires = codeChallenge.getExpiryDateTime();
		return true;
	}

	@Override
	public Map<String, Object> toSqlMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("client_id", clientId);
		map.put("code_challenge", challenge);
		map.put("state", state);
		map.put("expires", expires);
		return map;
	}
	
}
