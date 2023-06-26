package com.example.starter.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;

public class tokenAuth {
  private static final String SECRET_KEY = "your-secret-key";
  private static final String ISSUER = "your-issuer";
  private static final long TOKEN_EXPIRATION_TIME = 3600; // Token expiration time in seconds
  private final JWTAuth jwtAuth;

  public tokenAuth(JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }

  public void privateAuth(RoutingContext routingContext) {
    String token = routingContext.request().getHeader(HttpHeaders.AUTHORIZATION);
    if (token == null || !token.startsWith("Bearer ")) {
      routingContext.response()
        .setStatusCode(401)
        .end("Unauthorized");
      return;
    }

    // Extract the token value
    String jwtToken = token.substring(7);

    // Check if the token is null or empty
    if (jwtToken == null || jwtToken.isEmpty()) {
      routingContext.response()
        .setStatusCode(401)
        .end("Unauthorized");
      return;
    }

    // Verify and decode the token
    if (jwtAuth != null) {
      jwtAuth.authenticate(new JsonObject().put("jwt", jwtToken), res -> {
        System.out.println(res.result());
        if (res.succeeded()) {
          // Token is valid
          String generatedToken = generateToken("huynguyen");
          if (generatedToken.equals(jwtToken)) {
            System.out.println("Token is valid and matches the generated token.");
            routingContext.next();
          } else {
            System.out.println("Token is valid but does not match the generated token.");
            routingContext.response()
              .setStatusCode(401)
              .end("Unauthorized");
          }
        } else {
          System.out.println("Token authentication failed");
          routingContext.response()
            .setStatusCode(401)
            .end(String.valueOf(res.cause()));
        }
      });
    } else {
      routingContext.response()
        .setStatusCode(500)
        .end("Internal Server Error: JWTAuth is not initialized");
    }
  }

  public String generateToken(String username) {
    long expirationTime = System.currentTimeMillis() + (TOKEN_EXPIRATION_TIME * 1000);

    return Jwts.builder()
      .setSubject(username)
      .setIssuer(ISSUER)
      .setIssuedAt(new Date())
      .setExpiration(new Date(expirationTime))
      .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
      .compact();
  }

  private void saveToken(String username, String token) {
    System.out.println("Token saved for user " + username + ": " + token);
  }

  public void sendTokenResponse(HttpServerResponse response, String token) {
    response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end(Json.encodePrettily(new JsonObject().put("token", token)));
  }
}
