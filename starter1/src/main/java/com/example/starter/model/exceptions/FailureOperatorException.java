package com.example.starter.model.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FailureOperatorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected ErrorCode errorCode;
	protected Throwable throwable;

	public FailureOperatorException(ErrorCode errorCode, Throwable throwable) {
		super(errorCode.getMessage(), throwable);
		this.errorCode = errorCode;
	}

	public FailureOperatorException(ErrorCode errorCode) {
		this(errorCode, null);
	}

}
