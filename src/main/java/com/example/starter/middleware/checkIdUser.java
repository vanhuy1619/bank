package com.example.starter.middleware;

import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.concurrent.CompletableFuture;

public class checkIdUser {
  public CompletableFuture<Boolean> checkIdUser(JDBCPool pgPool, String idUser) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    pgPool.preparedQuery("SELECT * FROM userinfo WHERE iduser = ?")
      .execute(Tuple.of(idUser))
      .onSuccess(result -> {
        RowSet<?> rowSet = result;
        boolean hasResult = rowSet.size() > 0;
        future.complete(hasResult);
      })
      .onFailure(error -> {
        future.complete(false);
      });

    return future;
  }
}
