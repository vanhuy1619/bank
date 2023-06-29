package com.example.starter.services.impl;

import com.example.starter.common.TypeCardConst;
import com.example.starter.config.uploadConfig;
import com.example.starter.handler.TransactionValidationRequestHandler;
import com.example.starter.middleware.checkIdUser;
import com.example.starter.model.Cost;
import com.example.starter.model.Ewallet;
import com.example.starter.model.UserInfo;
import com.example.starter.model.callback.ResponeCallback;
import com.example.starter.schema.TransactionSchemaBuider;
import com.example.starter.services.TransactionService;
import com.example.starter.validate.validateConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

//check balance -> check account receive -> tranfer -> OTP -> save transaction
public class TransactionRepository implements TransactionService {
  private JDBCPool pgPool;
  private com.example.starter.validate.validateConfig validateConfig = new validateConfig();
  private ResponeCallback responeCallback = new ResponeCallback();
  private ObjectMapper objectMapper = new ObjectMapper();
  Date date = new Date();
  Timestamp timenow = new Timestamp(date.getTime());
  private checkIdUser checkIdUser = new checkIdUser();

  private String idcardTo ;
  private String idcardFrom;
  private String nameReceive;
  public TransactionRepository(JDBCPool pgPool) {
    this.pgPool = pgPool;
  }

  public TransactionRepository(String idcardTo, String idcardFrom, String nameReceive) {
    this.idcardTo = idcardTo;
    this.idcardFrom = idcardFrom;
    this.nameReceive = nameReceive;
  }

  @Override
  public void TranferMoneyBankToBank(RoutingContext context) {
    if (!"application/json".equals(context.request().getHeader("Content-Type"))) {
      context.response()
        .setStatusCode(415)
        .end("Unsupported Media Type");
      return;
    }

    try {
      JsonObject jsonObject = context.getBodyAsJson();

      JsonObject senderJson = jsonObject.getJsonObject("sender");
      String iduser_from = senderJson.getString("from");
      final double money_transfer = Double.parseDouble(senderJson.getString("money_transfer"));
      String bear_cost = senderJson.getString("bear_cost");

      JsonObject receiverJson = jsonObject.getJsonObject("receiver");
      String to_idcard = receiverJson.getString("to_idcard");
      idcardTo = to_idcard;

      checkIdUser.checkIdUser(pgPool, iduser_from)
        .thenAccept(exist -> {
          if (exist != null)
          {
            double finalMoneyTransfer = money_transfer;
            if (bear_cost.equals("sender")) {
              finalMoneyTransfer = money_transfer + money_transfer * Cost.BANK_TO_BANK;
            }
            if (bear_cost.equals("receiver")) {
              finalMoneyTransfer = money_transfer - money_transfer * Cost.BANK_TO_BANK;
            }
            final double transferAmount = finalMoneyTransfer;
            checkAccountBalanceSender(transferAmount, iduser_from)
              .thenAccept(result -> {
                if (result) {
                  checkTypeCard(iduser_from, TypeCardConst.ATM.getCodeCard())
                    .thenAccept(t->{
                      if(t == true)
                      {
                        checkAcountReceiver(context, idcardTo, ()->{
                          handleTransfer(context, to_idcard,bear_cost.equals("sender")?money_transfer:transferAmount, bear_cost.equals("sender")==true?transferAmount:money_transfer, jsonObject);
                        });
                      }
                      else
                        responeCallback.responseClient(context, 400, 1, "Loại thẻ 1 tài khoản không hỗ trợ", null);
                    });
                } else {
                  responeCallback.responseClient(context, 400, 1, "Insufficient account balance for the transaction", null);
                }
              })
              .exceptionally(e -> {
                responeCallback.responseClient(context, 400, 1, "Fail check balance", null);
                return null;
              });
          }
          else
          {
            responeCallback.responseClient(context, 400, 1, "Not found user", null);
          }
        })
        .exceptionally(e -> {
          responeCallback.responseClient(context, 400, 1, "Fail query", null);
          return null;
        });
    } catch (Exception e) {
      System.out.println("Error");
    }
  }

  public CompletableFuture<Boolean> checkAccountBalanceSender(double moneyTransfer, String iduser) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    pgPool.preparedQuery("SELECT idcard FROM card WHERE iduser = ? LIMIT 1")
      .execute(Tuple.of(iduser))
      .onSuccess(cardRows -> {
        if (cardRows.size() > 0) {
          String idcard = cardRows.iterator().next().getString("idcard");
          idcardFrom = idcard;
          pgPool.preparedQuery("SELECT account_balance FROM transaction WHERE idcard = ? LIMIT 1")
            .execute(Tuple.of(idcard))
            .onSuccess(transactionRows -> {
              if (transactionRows.size() > 0) {
                String accountBalanceStr = transactionRows.iterator().next().getString("account_balance");
                if (accountBalanceStr != null) {
                  double accountBalance = Double.parseDouble(accountBalanceStr.trim());
                  if (moneyTransfer < accountBalance) {
                    future.complete(true);
                    return;
                  }
                }
              }
              future.complete(false);
            })
            .onFailure(error -> future.completeExceptionally(error));
        } else {
          future.complete(false);
        }
      })
      .onFailure(error -> future.completeExceptionally(error));

    return future;
  }

  private void handleTransfer(RoutingContext context, String to_idcard, double money_transfer, double minus, JsonObject jsonObject) {
    try {
      pgPool.preparedQuery("UPDATE transaction SET account_balance = account_balance::numeric + ?, date_update = ? WHERE idcard = ?")
        .execute(Tuple.of(money_transfer, timenow, to_idcard))
        .onSuccess(s -> {
          minusAccountSend(context, minus, jsonObject);
        })
        .onFailure(error -> {
          responeCallback.responseClient(context, 400, 1, "Send fail", null);
        });
    } catch (Exception e) {
      responeCallback.responseClient(context, 400, 1, "Send fail", null);
    }
  }

  private void minusAccountSend(RoutingContext context, Double minus, JsonObject jsonObject) {
    try {
      pgPool.preparedQuery("UPDATE transaction SET account_balance = account_balance::numeric - ?, date_update = ? WHERE idcard = ?")
        .execute(Tuple.of(minus, timenow, idcardFrom))
        .onSuccess(s -> {
          saveTransactionHistory(context, idcardFrom, idcardTo, jsonObject);
        })
        .onFailure(error -> {
          responeCallback.responseClient(context, 400, 1, "Send fail", null);
        });
    } catch (Exception e) {
      responeCallback.responseClient(context, 400, 1, "Send fail", null);
    }
  }

  public void saveTransactionHistory(RoutingContext context, String idcardFrom, String idcardTo, JsonObject jsonObject) {
    JsonObject jsonObjectSender = jsonObject.getJsonObject("sender");
    jsonObjectSender.put("from", idcardFrom);
    jsonObjectSender.put("date_transaction", LocalDateTime.now().toString());
    jsonObjectSender.put("status", "Thành công");

    updateTransaction(context, "transaction_history_send", idcardFrom, jsonObject, () -> {
      updateTransaction(context, "transaction_history_receive", idcardTo, jsonObject, () -> {
        responeCallback.responseClient(context, 200, 0, "Send success", null);
      });
    });
  }

  public void updateTransaction(RoutingContext context, String transactionField, String idcard, JsonObject jsonObject, Runnable callback) {
    String query = "UPDATE transaction SET " + transactionField + " = COALESCE(" + transactionField + ", '[]'::jsonb) || ?::jsonb WHERE idcard = ?";

    pgPool.preparedQuery(query)
      .execute(Tuple.of(jsonObject.encode(), idcard))
      .onSuccess(rows -> {
        callback.run();
      })
      .onFailure(error -> {
        responeCallback.responseClient(context, 400, 1, error.getMessage(), null);
      });
  }

  public void checkAcountReceiver(RoutingContext context, String to_idcard, Runnable callback) {
    try {
      pgPool.preparedQuery("SELECT * FROM card WHERE idcard = ? LIMIT 1")
        .execute(Tuple.of(to_idcard))
        .onSuccess(s -> {
          if(s.size()>0)
          {
            for(Row row : s)
            {
              String iduser = row.getString("iduser");
              checkTypeCard(iduser, TypeCardConst.ATM.getCodeCard())
                .thenAccept(accept->{
                  if(accept == true)
                  {
                    callback.run();
                  }
                  else
                    responeCallback.responseClient(context, 400, 1, "Loại thẻ 1 tài khoản thụ hưởng không hỗ trợ", null);
                });
            }
          }
          else
            responeCallback.responseClient(context, 400, 1, "Account receiver does not exist", null);
        })
        .onFailure(f -> {
          responeCallback.responseClient(context, 400, 1, "Account receiver does not exist", null);
        });
    } catch (Exception e) {
      responeCallback.responseClient(context, 500, 1, "Error occurred while checking account receiver", null);
    }
  }

  public CompletableFuture<Boolean> checkTypeCard(String iduser, int typecard) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    pgPool.preparedQuery("SELECT typecard_action FROM card WHERE iduser = ? LIMIT 1")
      .execute(Tuple.of(iduser))
      .onSuccess(result -> {
        if (result.size() > 0) {
          Row row = result.iterator().next();
          Object typecardObject = row.getValue("typecard_action");

          if (typecardObject != null && typecardObject.getClass().isArray()) {
            int length = Array.getLength(typecardObject);
            Integer[] typecardArray = new Integer[length];
            for (int i = 0; i < length; i++) {
              typecardArray[i] = (Integer) Array.get(typecardObject, i);
            }
            List<Integer> typecardList = Arrays.asList(typecardArray);
            if (typecardList.contains(typecard)) {
              future.complete(true);
            } else {
              future.complete(false);
            }
          } else {
            future.complete(false);
          }
        } else {
          future.complete(false);
        }
      })
      .onFailure(error -> {
        future.complete(false);
      });

    return future;
  }

  @Override //
  public void TranferMoneyBankToEwallet(RoutingContext context, Ewallet ewallet) {


  }
}
