package com.example.starter.router;

import com.example.starter.activity.Impl.UploadFileExcutor;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class HandleRouter {
  public void setRoute(Router router, Vertx vertx)
  {
    router.route().handler(BodyHandler.create());
    router.post("/upload").handler(new UploadFileExcutor(vertx)::uploadFileTS);
  }
}
