package com.example.starter.model.exceptions;

public enum ErrorCodeEnum {
  SUCCESS("Successful", 0),
  FAILURE("Failure", 1),
  SP_ERROR("Error in SP '%s' with resultCode = %s", 2),
  CANNOT_DECRYPT("Can not decrypt password", 3),
  CANNOT_GET_DOMAIN("Can not get domain", 4),
  FORMAT_ERROR("Format error", 5),
  CANNOT_CALL_SP("Can not call SP %s in Java", 6),
  ERROR_CLOB("Error with CLOB in Java", 7),
  VALIDATE_FAILURE("Validate failure. %s", 8),
  NOT_EXISTS("Not Exists", 9),
  AUTH_FAILURE("Authenticate failure", 10),
  CANNOT_CONNECT_DB("Can not connect db %s in Java", 11),
  CANNOT_CLOSE_CONNECTION("Can not close connection db %s in Java", 12),
  USER_DISABLE("User disable", 92),
  XML_ERROR("Parse XML failure. Exception occur in %s method: %s", 93),
  PARSE_DATA_ERROR("Parse data error", 94),
  T24_ERROR("T24 failure. %s", 95),
  T24_TIME_OUT("T24 Time out. %s", 952),
  T24_EXCEPTION_BEFORE_CALL("T24 Exception. %s", 953),
  T24_EXCEPTION_AFTER_CALL("T24 Exception. %s", 954),
  T24_CONNECTION_ERROR("T24 Exception. %s", 954),
  JSON_ERROR("Parse JSON failure. Exception occur in %s method: %s", 96),
  ESB_FAILURE("Exception occur in %s ESB method: %s",97),
  DB_FAILURE("Exception occur in %s DB method: %s",98),
  INTERNAL_ERROR("Exception. Process fail. %s", 99),
  LOGIC_ERROR("Logic error in code java", 100),
  UNEXPECTED_ERROR("Unexpected Error.", 101),
  TRANSACTION_IN_DOUBT_ERROR("Transaction in doubt", 102);

  private String message;
  private Integer code;

  ErrorCodeEnum(String message, Integer code) {
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
