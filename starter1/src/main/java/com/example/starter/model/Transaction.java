package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
  @JsonAlias("idcard")
  private String idcard;

  @JsonAlias("account_balance")
  private String accountBalance;

  @JsonAlias("transaction_history_send")
  private List<JsonObject> transactionHistorySend;
  //type (1: thẻ, 2: phone), to, tên người thụ hưởng, money, date, status, phí chuyển, người chịu phí

  @JsonAlias("transaction_history_send_to")
  private List<JsonObject> transactionHistoryTo;

  @JsonAlias("date_update")
  private String dateUpdate;
}
