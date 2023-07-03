package com.example.starter.model.callback;

import io.vertx.core.json.JsonArray;

public interface DataQueryCallback {
  void OnSuccess(JsonArray result);
  void OnError (Throwable throwable);
}
