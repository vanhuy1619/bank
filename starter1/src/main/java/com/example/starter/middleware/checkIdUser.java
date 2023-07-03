package com.example.starter.middleware;

import com.example.starter.model.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.concurrent.CompletableFuture;

public class checkIdUser {
  private ObjectMapper objectMapper = new ObjectMapper();

  public CompletableFuture<UserInfo> checkIdUser(JDBCPool pgPool, String table, String idUser) {
    CompletableFuture<UserInfo> future = new CompletableFuture<>();

    pgPool.preparedQuery("SELECT * FROM " + table + " WHERE iduser = CAST(? AS UUID)")
      .execute(Tuple.of(idUser))
      .onSuccess(result -> {
        if (result.size() == 0) {
          future.complete(null);
        } else {
          Row row = result.iterator().next();
          JsonObject jsonObject = row.toJson();
          try {
            UserInfo userInfo = objectMapper.readValue(jsonObject.toString(), UserInfo.class);
            future.complete(userInfo);
          } catch (JsonProcessingException e) {
            future.completeExceptionally(e);
          }
        }
      })
      .onFailure(future::completeExceptionally);

    return future;
  }
}
