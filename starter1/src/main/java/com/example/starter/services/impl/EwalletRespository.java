package com.example.starter.services.impl;

import com.example.starter.common.EwalletConst;
import com.example.starter.config.uploadConfig;
import com.example.starter.middleware.checkIdUser;
import com.example.starter.model.callback.ResponeCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.jdbcclient.JDBCPool;

import java.util.Date;

public class EwalletRespository {
  private JDBCPool pgPool;

  private ResponeCallback responeCallback = new ResponeCallback();
  Date date = new Date();

  private ObjectMapper objectMapper = new ObjectMapper();

  private checkIdUser checkIdUser = new checkIdUser();

  public EwalletRespository(JDBCPool pgPool) {
    this.pgPool = pgPool;
  }
  public void registEwallet(RoutingContext context)
  {
    try{
      JsonObject jsonObject = context.getBodyAsJson();
      String iduser = jsonObject.getString("iduser");
      String ewallet = jsonObject.getString("ewallet");
      if(ewallet.equals(EwalletConst.MOMO))
      {
        checkIdUser.checkIdUser(pgPool, "card", iduser)
          .thenAccept(a->{
            System.out.println();
            if(a != null)
            {
              System.out.println("ok");
            }
            else
              System.out.println("Null");
          });
      }
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }
}
