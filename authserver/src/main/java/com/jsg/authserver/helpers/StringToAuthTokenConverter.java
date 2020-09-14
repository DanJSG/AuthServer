package com.jsg.authserver.helpers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.jsg.authserver.auth.AuthHeaderHandler;
import com.jsg.authserver.auth.AuthToken;

@Component
public final class StringToAuthTokenConverter implements Converter<String, AuthToken>{
	
	// add me!
	
	@Override
	public AuthToken convert(String authHeader) {
		return new AuthToken((authHeader.contains("Bearer")) ? AuthHeaderHandler.getBearerToken(authHeader) : authHeader);
	}

}
