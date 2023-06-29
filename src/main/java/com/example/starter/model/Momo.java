package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Momo {
  @JsonAlias("iduser")
  private String iduser;

  @JsonAlias("idmomo")
  private String idMomo;

  @JsonAlias("balance")
  private String balance;

}
