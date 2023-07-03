package com.example.starter.router;

import com.example.starter.activity.Impl.UploadFileExcutor;
import com.example.starter.constant.EndpointConst;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class HandleRouter {
  public void setRoute(Router router, Vertx vertx)
  {
    router.route().handler(BodyHandler.create());
    router.post(EndpointConst.API_UPLOAD_FILE).handler(new UploadFileExcutor(vertx)::uploadFileTS);
  }
}
