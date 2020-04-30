package com.jsg.authserver.api.rest;

import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.repositories.UserRepository;

@RestController
public class RegistrationController extends ApiController {

	protected RegistrationController(@Value("${sql.username}") String sqlUsername,
								@Value("${sql.password}") String sqlPassword,
								@Value("${sql.connectionstring}") String sqlConnectionString) {
		super(sqlUsername, sqlPassword, sqlConnectionString);
	}

	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> createAccount(@RequestBody String email, @RequestBody String password, 
			@RequestBody String username) 
			throws Exception {
		if(email == null || password == null || username == null || !EmailValidator.getInstance().isValid(email)) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		createUser(email, password);
		return UNAUTHORIZED_HTTP_RESPONSE;
	}

	private User createUser(String email, String password) throws Exception {
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		User user = new User(email, hashedPassword);
		if(!user.save(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD)) {
			return null;
		}
		UserRepository repo = new UserRepository(SQL_CONNECTION_STRING, SQL_USERNAME, SQL_PASSWORD);
		user = repo.findWhereEqual("email", "value", 1).get(0);
		repo.closeConnection();
		return user;
	}
	
}
