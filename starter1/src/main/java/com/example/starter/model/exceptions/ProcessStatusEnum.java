package com.example.starter.model.exceptions;


public enum ProcessStatusEnum {

	DANG_XU_LY("DANG_XU_LY", 1),
	KHONG_XU_LY("KHONG_XU_LY", 0);

	private String message;
	private Integer code;

	ProcessStatusEnum(String message, Integer code) {
		this.message = message;
		this.code = code;
	}

	public ErrorCode get() {
		return get("");
	}

	public ErrorCode get(Object... params) {
		return new ErrorCode() {

			@Override
			public String getMessage() {
				return String.format(message, params);
			}

			@Override
			public Integer getCode() {
				return code;
			}
		};
	}

	public String getMessage() {
		return message;
	}

	public Integer getCode() {
		return code;
	}
}
