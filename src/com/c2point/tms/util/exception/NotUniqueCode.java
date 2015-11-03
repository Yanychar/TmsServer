package com.c2point.tms.util.exception;

@SuppressWarnings("serial")
public class NotUniqueCode extends Exception {

	public NotUniqueCode( String message ) {
		super( message );
	}

	public NotUniqueCode() {
		super();
	}

}
