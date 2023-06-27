package com.example.starter;

import com.example.starter.auth.tokenAuth;
import com.example.starter.datasource.dataSource;
import com.example.starter.repository.cardRepository;
import com.example.starter.repository.userRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.common.dsl.SchemaBuilder;

public class MainVerticle extends AbstractVerticle {
  private JWTAuth jwtAuth;
  private tokenAuth tokenAuth;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
//    initializeJwtAuth();

    Router router = routes();
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(config().getInteger("http.port", 8888), result -> {
        if (result.succeeded()) {
          System.out.println("Starting port in 8888");
          startPromise.complete();
        } else {
          startPromise.fail(result.cause());
        }
      });
  }

//  public void initializeJwtAuth() {
//    JWTAuthOptions jwtAuthOptions = new JWTAuthOptions();
//
//    // Add public key or secret key configuration here
//    jwtAuthOptions.addPubSecKey(new PubSecKeyOptions()
//      .setAlgorithm("HS256")
//      .setSecretKey("your-secret-key")
//    );
//    jwtAuth = JWTAuth.create(vertx, jwtAuthOptions);
//    tokenAuth = new tokenAuth(jwtAuth); // Pass JWTAuth instance to tokenAuth constructor
//    System.out.println("pk "+tokenAuth.generateToken("huynguyen"));
//  }


  private Router routes() {

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    dataSource dataSource = new dataSource();
    JDBCPool db = dataSource.getPool();

    userRepository userRepository = new userRepository(db);
    cardRepository cardRepository = new cardRepository(db);
//    router.post("/jwt").handler(this::tt);

    router.post("/regist-info").handler(userRepository::handleRegistration);
    router.post("/regist").handler(userRepository::handleRegist);
    router.get("/api").handler(userRepository::select);
    router.post("/upload-id-img").handler(userRepository::handleImageUpload);
    router.post("/update-pass").handler(userRepository::updatePassword);

    router.post("/open-card").handler(cardRepository::openCard);
    return router;
  }

//  public void tt(RoutingContext routingContext) {
//    String token = tokenAuth.generateToken("huynguyen");
//
//    tokenAuth.sendTokenResponse(routingContext.response(), token);
//  }
}
