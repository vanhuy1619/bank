package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ewallet {
  @JsonAlias("iduser")
  private String iduser;

  @JsonAlias("ewallet")
  private String ewallet; //momo, zalopay

}
