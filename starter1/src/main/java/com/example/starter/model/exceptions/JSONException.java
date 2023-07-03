package com.example.starter.model.exceptions;

public class JSONException extends FailureOperatorException {

	private static final long serialVersionUID = 1L;

	public JSONException(ErrorCode errorCode) {
		super(errorCode, null);
	}

	public JSONException(ErrorCode errorCode, Throwable throwable) {
		super(errorCode, throwable);
	}

}
