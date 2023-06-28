package com.example.starter.services;

import io.vertx.ext.web.RoutingContext;

public interface UserService {
  public void handleRegistration(RoutingContext context);
  public void handleRegist(RoutingContext context);
  public void handleImageUpload(RoutingContext context);
  public void updatePassword(RoutingContext context);
}
