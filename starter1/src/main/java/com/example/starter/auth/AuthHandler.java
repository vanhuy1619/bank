package com.example.starter.auth;

import com.example.starter.api.constant.EndpointConst;
import com.example.starter.api.constant.PropertiesConfig;
import com.example.starter.utils.ConfigUtils;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.impl.JWTAuthProviderImpl;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

public class AuthHandler {
  private static final String SECRET_KEY = "your-secret-key";
  int tokenExpired = Integer.parseInt(ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.TOKEN_EXPIRED));
  String endPointLogin = EndpointConst.API_LOGIN;
  public JWTAuthHandler authHandler(JWTAuth jwtAuth)
  {
    return JWTAuthHandler.create(jwtAuth,endPointLogin);
  }

  public void generalToken(RoutingContext context, JWTAuth jwtAuth, String username) {
    try {
      String token=jwtAuth.generateToken(new JsonObject(),
        new JWTOptions()
          .setSubject(username)
          .setExpiresInSeconds(tokenExpired)
          .setAlgorithm("HS256"));

      //save token from client
      Cookie cookie=Cookie.cookie("auth",token);
      cookie.setHttpOnly(true).setPath("/").encode();
      context.addCookie(cookie).response()
        .putHeader("content-type","text/plain")
        .putHeader("Authorization",token)
        .end(Json.encodePrettily(responseToken(200, 0, token)));

    } catch (Exception e) {
      context.response().setStatusCode(401)
        .putHeader("Content-Type", "application/json")
        .end(Json.encodePrettily(responseToken(400, 1, e.getMessage()).encode()));
    }
  }

  public JsonObject responseToken(int statusCode, int code, String mess) {
    JsonObject successResponse = new JsonObject()
      .put("statusCode", statusCode)
      .put("code", code)
      .put("token", mess);
    return successResponse;
  }



}
