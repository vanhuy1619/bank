package com.example.starter.model.exceptions;

public enum TransactionStatusEnum {

	CHUA_XU_LY("CHUA_XU_LY", 0),
	THANH_CONG("THANH_CONG", 1),
	NGHI_VAN("NGHI_VAN", 2),
	THAT_BAI("THAT_BAI", 3),
	LOI("LOI", 4);

	private String message;
	private Integer code;

	TransactionStatusEnum(String message, Integer code) {
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
