package com.jsg.authserver.api.rest;

import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jsg.authserver.datatypes.User;
import com.jsg.authserver.datatypes.UserInfo;
import com.jsg.authserver.libs.sql.MySQLRepository;
import com.jsg.authserver.libs.sql.SQLColumn;
import com.jsg.authserver.libs.sql.SQLRepository;
import com.jsg.authserver.libs.sql.SQLTable;
import com.jsg.authserver.datatypes.UserBuilder;

@RestController
public class RegistrationController extends ApiController {

	@Autowired
	protected RegistrationController() {
		super();
	}

	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<String> register(@RequestBody Map<String, String> accountMap) 
			throws Exception {
		String email = accountMap.get("email");
		String password = accountMap.get("password");
		String username = accountMap.get("username");
		if(email == null || password == null || username == null || !EmailValidator.getInstance().isValid(email)) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		if(!createAccount(email, password, username)) {
			return BAD_REQUEST_HTTP_RESPONSE;
		}
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
	
	private Boolean createAccount(String email, String password, String username) throws Exception {
		User user = createUser(email, password);
		if(user == null) {
			return false;
		}
		UserInfo info = createUserInfo(user.getId(), username);
		if(info == null) {
			return false;
		}
		return true;
	}
	
	private UserInfo createUserInfo(long id, String username) throws Exception {
		UserInfo info = new UserInfo(id, username);
		SQLRepository<UserInfo> repo = new MySQLRepository<>(SQLTable.INFO);
		if(!repo.save(info)) {
			return null;
		}
		return info;
	}

	private User createUser(String email, String password) throws Exception {
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		User user = new User(email, hashedPassword);
		SQLRepository<User> repo = new MySQLRepository<>(SQLTable.ACCOUNTS);
		if(!repo.save(user)) {
			return null;
		}
		List<User> users = repo.findWhereEqual(SQLColumn.EMAIL, email, 1, new UserBuilder());
		if(users == null) {
			return null;
		}
		user = users.get(0);
		return user;
	}
	
}
