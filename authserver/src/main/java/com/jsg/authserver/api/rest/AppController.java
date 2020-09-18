package com.jsg.authserver.api.rest;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.authserver.auth.AuthToken;
import com.jsg.authserver.datatypes.App;
import com.jsg.authserver.helpers.SecureRandomString;
import com.jsg.authserver.libs.sql.MySQLRepository;
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
	
}
