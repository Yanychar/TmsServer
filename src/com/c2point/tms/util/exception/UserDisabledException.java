package com.c2point.tms.util.exception;

public class UserDisabledException extends Exception {
	private static final long serialVersionUID = 8445872601708326759L;

	public UserDisabledException(String message) {
		super(message);
	}
}