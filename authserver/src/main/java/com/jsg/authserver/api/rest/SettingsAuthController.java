package com.jsg.authserver.api.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.authserver.auth.AuthToken;
import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.datatypes.UserBuilder;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;

@RestController
public class SettingsAuthController extends ApiController {
	
	@Autowired
	public SettingsAuthController() {
		super();
	}
	
	
	@PostMapping(value = "/settings/auth", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> authorize(@RequestHeader(HttpHeaders.AUTHORIZATION) AuthToken token) {
		long id = token.getId();
		SQLRepository<User> repo = new MySQLRepository<User>(SQLTable.ACCOUNTS);
		List<User> users = repo.findWhereEqual(SQLColumn.ID, id, new UserBuilder());
		if(users == null)
			return UNAUTHORIZED_HTTP_RESPONSE;
		User user = users.get(0);
		return ResponseEntity.status(HttpStatus.OK).body(user.writeValueAsString());
	}
	
	
	
}
