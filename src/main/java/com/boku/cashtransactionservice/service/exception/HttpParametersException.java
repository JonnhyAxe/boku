package com.boku.cashtransactionservice.service.exception;

public class HttpParametersException extends RuntimeException {

	public HttpParametersException() {
		super();
	}

	public HttpParametersException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public HttpParametersException(final String message) {
		super(message);
	}

	public HttpParametersException(final Throwable cause) {
		super(cause);
	}
}
