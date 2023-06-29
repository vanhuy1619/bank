package com.example.starter.services;

import io.temporal.activity.ActivityInterface;
import io.vertx.ext.web.RoutingContext;

@ActivityInterface
public interface TransactionService {
  public void TranferMoneyBankToBank(RoutingContext context);

}
