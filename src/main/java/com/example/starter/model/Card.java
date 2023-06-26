package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
  @JsonAlias("iduser")
  private String iduser;


  @JsonAlias("idcard")
  private String idcard;

  @JsonAlias("date_open")
  private String dateOpen;

  @JsonAlias("cvv")
  private String cvv;

  @JsonAlias("typecard")
  private List<Integer> typeCard;
  //1:atm, 2: credit, 3: đảm bảo, 4: prepaid(trả trước), 5: ghi nợ

  @JsonAlias("link")
  private List<String> link;

  @JsonAlias("signature")
  private String signature;
}
