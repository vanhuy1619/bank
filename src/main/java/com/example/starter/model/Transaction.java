package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
  @JsonAlias("iduser")
  private String iduser;

  @JsonAlias("account_balance")
  private String accountBalance;

  @JsonAlias("trans_history")
  private JsonObject transactionHistory;
  //type (1: thẻ, 2: phone), to, tên người thụ hưởng, money, date, status, phí chuyển, người chịu phí

  @JsonAlias("date_update")
  private String dateUpdate;
}
