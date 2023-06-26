package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
  @JsonAlias("phone")
  private String phone;

  @JsonAlias("password")
  private String password;

  @JsonAlias("old_password")
  private List<String> oldPass;

  @JsonAlias("update_pass")
  private String updatePass;
}
