package com.example.starter.schema;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoTransaction {
  @JsonAlias("type")
  private int type;

  @JsonAlias("to_idcard")
  private String toIDCard;

  @JsonAlias("to_name")
  private String toName; //tên người thụ hưởng

  @JsonAlias("money_tranfer")
  private String moneyTranfer;

  @JsonAlias("cost")
  private double cost;

  @JsonAlias("bear_cost")
  private String bearCost;

  @JsonAlias("status")
  private String status;

  @JsonAlias("to_date")
  private String toDate;

}
