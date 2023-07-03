package com.example.starter.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class tokenAuth {
  private static final String SECRET_KEY = "your-secret-key";
  private static final String ISSUER = "your-issuer";
  private static final long TOKEN_EXPIRATION_TIME = 3600; // Token expiration time in seconds
//  private final JWTAuth jwtAuth;

  public tokenAuth() {
//    this.jwtAuth = jwtAuth;
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

    try {
      Claims claims = Jwts.parser()
        .setSigningKey(SECRET_KEY.getBytes())
        .parseClaimsJws(jwtToken)
        .getBody();
      String auth = claims.get("AUTH", String.class);
      System.out.println(auth);
      if (jwtToken.equals(auth)) {
        // Token is valid
        String generatedToken = generateToken("generateToken");
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
          .end("Unauthorized");
      }

    } catch (Exception e) {
      System.out.println("Token verification failed");
      routingContext.response()
        .setStatusCode(401)
        .end("Unauthorized");
    }
//    // Verify and decode the token
//    if (jwtAuth != null) {
//
//    } else {
//      routingContext.response()
//        .setStatusCode(500)
//        .end("Internal Server Error: JWTAuth is not initialized");
//    }
  }

  public static String generateToken(String username) {
    LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(60);
    Date expirationDate = Date.from(expirationTime.toInstant(ZoneOffset.UTC));


    // Generate JWT token
    return Jwts.builder()
      .setExpiration(expirationDate)
      .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
      .compact();
  }

  public static void sendTokenResponse(HttpServerResponse response, String token) {
    response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end(Json.encodePrettily(new JsonObject().put("token", token)));
  }
}
