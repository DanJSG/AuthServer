package com.jsg.authserver.api.rest;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.authserver.auth.AuthToken;
import com.jsg.authserver.datatypes.App;
import com.jsg.authserver.datatypes.AppBuilder;
import com.jsg.authserver.helpers.SecureRandomString;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

@RestController
public class AppController extends ApiController {

	@Autowired
	public AppController() {
		super();
	}
	
	@PostMapping(value = "/app/register", consumes = "application/json")
	public ResponseEntity<String> create(@RequestHeader("authorization") AuthToken token, @RequestBody App appDetails) {
		String clientId = SecureRandomString.getAlphaNumeric(12);
		String clientSecret = SecureRandomString.getAlphaNumeric(new SecureRandom().nextInt(19) + 32);
		String accessTokenSecret = SecureRandomString.getAlphaNumeric(128);
		App app = new App(clientId, appDetails.getRedirectUri(), clientSecret, accessTokenSecret, token.getId(), appDetails.getName());
		SQLRepository<App> repo = new MySQLRepository<>(SQLTable.APPS);
		if(!repo.save(app))
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	@GetMapping(value = "/app/getAll")
	public ResponseEntity<String> get(@RequestHeader(HttpHeaders.AUTHORIZATION) AuthToken token) {
		SQLRepository<App> repo = new MySQLRepository<>(SQLTable.APPS);
		List<App> apps = repo.findWhereEqual(SQLColumn.ASSOCIATED_ACCOUNT_ID, token.getId(), new AppBuilder());
		if(apps == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return ResponseEntity.status(HttpStatus.OK).body(mapper.writeValueAsString(apps));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return INTERNAL_SERVER_ERROR_HTTP_RESPONSE;
		}
	}
	
}
