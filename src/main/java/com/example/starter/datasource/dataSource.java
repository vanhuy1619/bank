package com.example.starter.datasource;

import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;

public class dataSource {
  private JDBCPool pool;

  public dataSource() {
    Vertx vertx = Vertx.vertx();

    JDBCConnectOptions connectOptions = new JDBCConnectOptions()
      .setJdbcUrl("jdbc:postgresql://localhost:5433/bank")
      .setUser("yugabyte")
      .setPassword("");

    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(100);

    pool = JDBCPool.pool(vertx, connectOptions, poolOptions);
  }

  public JDBCPool getPool() {
    if (pool != null) {
      System.out.println("Data connection successful.");
    } else {
      System.out.println("Failed to connect to the data source.");
    }
    return pool;
  }
}
