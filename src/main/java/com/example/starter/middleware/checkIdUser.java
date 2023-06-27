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
//  public CompletableFuture<Boolean> checkIdUser(JDBCPool pgPool, String idUser) {
//    CompletableFuture<Boolean> future = new CompletableFuture<>();
//
//    pgPool.preparedQuery("SELECT * FROM userinfo WHERE iduser = ?")
//      .execute(Tuple.of(idUser))
//      .onSuccess(result -> {
//        RowSet<?> rowSet = result;
//        boolean hasResult = rowSet.size() > 0;
//        future.complete(hasResult);
//      })
//      .onFailure(error -> {
//        future.complete(false);
//      });
//
//    return future;
//  }

  public CompletableFuture<UserInfo> checkIdUser(JDBCPool pgPool, String idUser) {
    CompletableFuture<UserInfo> future = new CompletableFuture<>();

    pgPool.preparedQuery("SELECT * FROM userinfo WHERE iduser = ?")
      .execute(Tuple.of(idUser))
      .onSuccess(result -> {
        for (Row row : result) {
          JsonObject jsonObject = row.toJson();
          try {
            UserInfo userInfo = objectMapper.readValue(jsonObject.toString(), UserInfo.class);
            future.complete(userInfo);
            return;
          } catch (JsonProcessingException e) {
            future.completeExceptionally(e);
            return;
          }
        }
        future.complete(null); // If no user info found, complete with null
      })
      .onFailure(error -> {
        future.completeExceptionally(error);
      });

    return future;
  }
}
