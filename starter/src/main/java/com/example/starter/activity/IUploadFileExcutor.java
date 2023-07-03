package com.example.starter.activity;

import io.temporal.activity.ActivityInterface;
import io.vertx.ext.web.RoutingContext;

@ActivityInterface
public interface IUploadFileExcutor {
  public void uploadFileTS(RoutingContext context);
}
