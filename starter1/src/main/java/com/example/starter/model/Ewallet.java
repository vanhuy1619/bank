package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ewallet {
  @JsonAlias("balance")
  private String balance;

  @JsonAlias("date_open")
  private String dateOpen;

  @JsonAlias("transaction_send")
  private List<Object> transactionSend;

  @JsonAlias("transaction_receive")
  private List<Object> transactionReceive;

  @JsonAlias("date_update")
  private String dateUpdate;
}
