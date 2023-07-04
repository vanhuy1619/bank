package com.example.starter;

import com.example.starter.api.constant.PropertiesConfig;
import com.example.starter.api.router.BankRouter;
import com.example.starter.utils.ConfigUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {
  private JWTAuth jwtAuth;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    int port = Integer.parseInt(ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.SERVER_PORT));
    BankRouter bankRouter = new BankRouter();
    Router router = Router.router(vertx);

    bankRouter.setRouter(router, vertx);

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
}
