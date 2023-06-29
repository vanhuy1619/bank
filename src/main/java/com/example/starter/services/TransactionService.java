package com.example.starter.services;

import com.example.starter.model.Ewallet;
import io.temporal.activity.ActivityInterface;
import io.vertx.ext.web.RoutingContext;

@ActivityInterface
public interface TransactionService {
  public void TranferMoneyBankToBank(RoutingContext context);
  public void TranferMoneyBankToEwallet(RoutingContext context, Ewallet ewallet);
}
