package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTP {
  @JsonAlias("iduser")
  private String iduser;

  @JsonAlias("otp")
  private String otp;

  @JsonAlias("expired")
  private String expired;
}
