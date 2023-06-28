package com.example.starter;

import com.example.starter.api.constant.PropertiesConfig;
import com.example.starter.api.router.BankRouter;
import com.example.starter.auth.tokenAuth;
import com.example.starter.utils.ConfigUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {
  private JWTAuth jwtAuth;
  private tokenAuth tokenAuth;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
//    initializeJwtAuth();
    int port = Integer.parseInt(ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.SERVER_PORT));
    BankRouter bankRouter = new BankRouter();
    Router router = Router.router(vertx);
    bankRouter.setRouter(router);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(config().getInteger("http.port", port), result -> {
        if (result.succeeded()) {
          System.out.println("Starting port in "+port);
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


//  public void tt(RoutingContext routingContext) {
//    String token = tokenAuth.generateToken("huynguyen");
//
//    tokenAuth.sendTokenResponse(routingContext.response(), token);
//  }
}
