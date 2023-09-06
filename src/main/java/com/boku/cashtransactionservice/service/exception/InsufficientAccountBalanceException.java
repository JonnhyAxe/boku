package com.boku.cashtransactionservice.service.exception;

public class InsufficientAccountBalanceException extends RuntimeException {

	public InsufficientAccountBalanceException() {
		super();
	}

	public InsufficientAccountBalanceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public InsufficientAccountBalanceException(final String message) {
		super(message);
	}

	public InsufficientAccountBalanceException(final Throwable cause) {
		super(cause);
	}
}
