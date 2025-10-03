package com.tss.aml.exception;

public class UserApiException extends RuntimeException {

	public UserApiException(String message) {
		super(message);
	}

	public UserApiException(String message, Throwable cause) {
		super(message, cause);
	}
}
