package com.example.starter.model.callback;

import com.example.starter.model.UserInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class ResponeCallback<T> {
  private int code;
  private String message;
  private T data;

  public ResponeCallback() {
  }

  public ResponeCallback(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  @SneakyThrows
  public <T> String responeCallback(int code, String message, T data) throws JsonProcessingException {
    List<T> emptyData = new ArrayList<>();
    if (data == null) {
      data = (T) emptyData;
    }
    ResponeCallback<T> response = new ResponeCallback<>(code, message, data);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper.writeValueAsString(response);
  }

  @SneakyThrows
  public void responseClient(RoutingContext context, int statusCode, int code, String mess, T data) {
    ObjectMapper objectMapper = new ObjectMapper();
    ResponeCallback response = new ResponeCallback<>(code, mess, data);
    String responseJson = objectMapper.writeValueAsString(response);
    context.response().setStatusCode(statusCode).putHeader("Content-Type", "application/json").end(responseJson);

    //Ánh xạ đối tượng
//    JsonObject jsonObject = JsonObject.mapFrom();
//    context.response()
//      .setStatusCode(200)
//      .putHeader("Content-Type","application/json")
//      .end(Json.encodePrettily(jsonObject));
  }



  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
