package com.example.starter.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

  @JsonAlias("iduser")
  private UUID iduser;

  @JsonAlias("fullname")
  private String fullname;

  @JsonAlias("birth")
  private String birth;

  @JsonAlias("citizenid")
  private String citizenId;

  @JsonAlias("phone")
  private String phone;

  @JsonAlias("email")
  private String email;

  @JsonAlias("address")
  private String address;

  @JsonAlias("front_id")
  private String fontID;

  @JsonAlias("back_id")
  private String backID;
}
