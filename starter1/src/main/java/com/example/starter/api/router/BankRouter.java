package com.example.starter.api.router;

import com.example.starter.api.constant.EndpointConst;
import com.example.starter.auth.AuthHandler;
import com.example.starter.datasource.dataSource;
import com.example.starter.handler.TransactionValidationRequestHandler;
import com.example.starter.schema.TransactionSchemaBuider;
import com.example.starter.services.impl.EwalletRespository;
import com.example.starter.services.impl.TransactionRepository;
import com.example.starter.services.impl.cardRepository;
import com.example.starter.services.impl.userRepository;
import com.google.gson.JsonObject;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.jdbcclient.JDBCPool;

public class BankRouter {
  private userRepository userRepository;
  private cardRepository cardRepository;
  private TransactionRepository transactionRepository;
  private EwalletRespository ewalletRespository;

  public void setData() {
    dataSource dataSource = new dataSource();
    JDBCPool db = dataSource.getPool();

    userRepository = new userRepository(db);
    cardRepository = new cardRepository(db);
    transactionRepository = new TransactionRepository(db);
    ewalletRespository = new EwalletRespository(db);
  }

  public void setRouter(Router router, Vertx vertx) {
    setData();
    router.route().handler(BodyHandler.create());

    setAuth(router, vertx);

    //userinfo
    router.post("/regist-info").handler(userRepository::handleRegistration);
    router.post("/regist").handler(userRepository::handleRegist);
    router.get(EndpointConst.API_INFO_ALL).handler(userRepository::select);
    router.post(EndpointConst.API_UPLOAD_CITIZEN).handler(userRepository::handleImageUpload);
    router.post(EndpointConst.API_UPDATE_PASSWORD).handler(userRepository::updatePassword);

    //card
    router.post(EndpointConst.API_OPEN_CARD).handler(cardRepository::openCardType);

    //transaction
    router.post(EndpointConst.API_TRANFER_BANK2BANK)
      .handler(new TransactionValidationRequestHandler(TransactionSchemaBuider.build())::validate)
      .handler(transactionRepository::TranferMoneyBankToBank);

    try {

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    //EWALLET
    router.post(EndpointConst.API_REGIST_EWALLET).handler(ewalletRespository::registEwallet);
  }

  public void setAuth(Router router, Vertx vertx)
  {
    KeyStoreOptions keyStoreOptions = new KeyStoreOptions()
      .setType("jceks")
      .setPath("keys\\keystore.jceks")
      .setPassword("secret");

    JWTAuthOptions jwtAuthOptions = new JWTAuthOptions()
      .setKeyStore(keyStoreOptions);

    JWTAuth jwtAuth = JWTAuth.create(vertx, jwtAuthOptions);

    AuthHandler authHandler = new AuthHandler();
    router.post(EndpointConst.API_LOGIN).handler(ctx->{
      userRepository.Login(ctx, jwtAuth);
    });
    router.route("/bank/*").handler(authHandler.authHandler(jwtAuth));

  }
}
