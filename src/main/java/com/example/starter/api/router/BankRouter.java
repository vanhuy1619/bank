package com.example.starter.api.router;

import com.example.starter.api.constant.EndpointConst;
import com.example.starter.datasource.dataSource;
import com.example.starter.services.impl.cardRepository;
import com.example.starter.services.impl.userRepository;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.jdbcclient.JDBCPool;

public class BankRouter {
  private userRepository userRepository;
  private cardRepository cardRepository;
  public void setData()
  {
    dataSource dataSource = new dataSource();
    JDBCPool db = dataSource.getPool();

    userRepository = new userRepository(db);
    cardRepository = new cardRepository(db);
  }

  public void setRouter(Router router)
  {
    setData();
    router.route().handler(BodyHandler.create());

//    router.post("/jwt").handler(this::tt);

    router.post("/regist-info").handler(userRepository::handleRegistration);
    router.post("/regist").handler(userRepository::handleRegist);
    router.get(EndpointConst.API_INFO_ALL).handler(userRepository::select);
    router.post(EndpointConst.API_UPLOAD_CITIZEN).handler(userRepository::handleImageUpload);
    router.post(EndpointConst.API_UPDATE_PASSWORD).handler(userRepository::updatePassword);

    router.post(EndpointConst.API_OPEN_CARD).handler(cardRepository::openCard);
  }

}
