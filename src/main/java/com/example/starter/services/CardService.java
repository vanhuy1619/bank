package com.example.starter.services;

import io.temporal.activity.ActivityInterface;
import io.vertx.ext.web.RoutingContext;

@ActivityInterface
public interface CardService {
  public void openCardType(RoutingContext context);
}
