package com.example.starter.auth;

import io.vertx.core.http.Cookie;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class AuthHandler {
  public JWTAuthHandler authHandler(JWTAuth jwtAuth)
  {
    return JWTAuthHandler.create(jwtAuth,"/api/login");
  }
  public void generalToken(RoutingContext context, JWTAuth jwtAuth)
  {
    String token = jwtAuth.generateToken(new JsonObject(), new JWTOptions().setExpiresInSeconds(120));

    Cookie cookie = Cookie.cookie("auth", token);
    cookie.setHttpOnly(true).setPath("/").encode();
    context.addCookie(cookie).response()
      .putHeader("Content-Type","application/json")
      .putHeader("Authorization", token)
      .end(token);
  }
}
