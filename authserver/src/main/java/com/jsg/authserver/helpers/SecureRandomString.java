package com.jsg.authserver.helpers;

import java.security.SecureRandom;

public final class SecureRandomString {
	
	private static final String ALPHA_NUM_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String getAlphaNumeric(int length) {
		SecureRandom randomProvider = new SecureRandom();
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0; i < length; i++) {
			stringBuilder.append(ALPHA_NUM_CHARS.charAt(randomProvider.nextInt(ALPHA_NUM_CHARS.length())));
		}
		return stringBuilder.toString();
	}
	
}
