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
import com.jsg.authserver.libs.sql.SQLEntity;
import com.jsg.authserver.repositories.CodeChallengeRepository;

public class CodeChallenge implements SQLEntity {
	
	private String clientId;
	private String codeChallenge;
	private String state;
	private Timestamp expires;
	
	public CodeChallenge(String clientId, String codeChallenge, String state) {
		this(clientId, codeChallenge, state, createExpiryTimestamp());
	}
	
	public CodeChallenge(String clientId, String codeChallenge, String state, Timestamp expires) {
		this.clientId = clientId;
		this.codeChallenge = codeChallenge;
		this.state = state;
		this.expires = expires;
	}
	
	public CodeChallenge(String clientId, String state) {
		this(clientId, null, state, null);
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
	
	private static Timestamp createExpiryTimestamp() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.MINUTE, 1);
		return new Timestamp(calendar.getTimeInMillis());
	}
	
	public Boolean save(String connectionString, String username, String password) throws Exception {
		CodeChallengeRepository challengeRepo = new CodeChallengeRepository(connectionString, username, password);
		Boolean isSaved = challengeRepo.save(this);
		challengeRepo.closeConnection();
		return isSaved;
	}
	
	public Boolean verifyCodeChallenge(CodeChallengeRepository challengeRepo, String code_verifier) throws Exception {
		List<CodeChallenge> codeChallenges = challengeRepo.findWhereEqual("state", state);
		if(codeChallenges == null || codeChallenges.size() < 1) {
			return false;
		}
		CodeChallenge codeChallenge = codeChallenges.get(0);
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
		map.put("code_challenge", codeChallenge);
		map.put("state", state);
		map.put("expires", expires);
		return map;
	}
	
}
