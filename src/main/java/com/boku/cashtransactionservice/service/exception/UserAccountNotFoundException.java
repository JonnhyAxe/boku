package com.boku.cashtransactionservice.service.exception;

public class UserAccountNotFoundException extends RuntimeException {

	public UserAccountNotFoundException() {
		super();
	}

	public UserAccountNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UserAccountNotFoundException(final String message) {
		super(message);
	}

	public UserAccountNotFoundException(final Throwable cause) {
		super(cause);
	}
}
