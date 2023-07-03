package com.example.starter;

import com.example.starter.router.HandleRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
public class MainVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    HandleRouter handleRouter = new HandleRouter();
    handleRouter.setRoute(router, vertx);

    try {
      vertx.createHttpServer()
        .requestHandler(router)
        .listen(config().getInteger("http.port", 7777), result -> {
          if (result.succeeded()) {
            System.out.println("Server started on port " + result.result().actualPort());
            startPromise.complete();
          } else {
            System.out.println("Failed to start server");
            startPromise.fail(result.cause());
          }
        });
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
