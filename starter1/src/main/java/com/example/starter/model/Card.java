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
public class Card {
  @JsonAlias("iduser")
  private String iduser;


  @JsonAlias("idcard")
  private String idcard;

  @JsonAlias("cif")
  private String cif;

  @JsonAlias("date_regist")
  private String dateRegist;

  @JsonAlias("date_approve")
  private String dateApprove;

  @JsonAlias("cvv")
  private String cvv;

  @JsonAlias("typecard")
  private List<Integer> typeCard;

  @JsonAlias("link")
  private List<String> link;

  @JsonAlias("signature")
  private String signature;

  @JsonAlias("status_approve")
  private String statusApprove;

  @JsonAlias("typecard_action")
  private List<Integer> typeCardAction;
}
