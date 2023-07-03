package com.example.starter.activity.Impl;

import com.example.starter.activity.IUploadFileExcutor;
import com.example.starter.constant.EndpointConst;
import com.example.starter.constant.PropertiesConfig;
import com.example.starter.types.UploadFileTRespone;
import com.example.starter.utils.ConfigUtils;
import com.google.gson.Gson;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class UploadFileExcutor implements IUploadFileExcutor {
  private final String upload_file_host = ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.upload_file_host);
  private final int upload_file_port = Integer.parseInt(ConfigUtils.getInstance().getProperties().getProperty(PropertiesConfig.upload_file_port));

  private final WebClient vertxClient;
  private final Gson gson = new Gson();

  public UploadFileExcutor(Vertx vertx) {
    vertxClient = WebClient.create(vertx);
  }

  @Override
  public void uploadFileTS(RoutingContext context) {
    JsonObject payload = context.getBodyAsJson();

    vertxClient.post(upload_file_port, upload_file_host, EndpointConst.UPLOAD_FILE_URI)
      .timeout(60000)
      .putHeaders(createHeader())
      .sendJson(payload, ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> response = ar.result();
          UploadFileTRespone uploadFileResponse = gson.fromJson(response.body().toString(), UploadFileTRespone.class);
          context.response()
            .putHeader("Content-Type", "application/json")
            .setStatusCode(200)
            .end(gson.toJson(uploadFileResponse));
        } else {
          UploadFileTRespone res = UploadFileTRespone.builder()
            .data(null)
            .code(1)
            .message(ar.cause().getMessage())
            .build();
          context.response()
            .putHeader("Content-Type", "application/json")
            .setStatusCode(500)
            .end(gson.toJson(res));
        }
      });
  }

  private MultiMap createHeader() {
    MultiMap inputHeaders = new HeadersMultiMap();
    inputHeaders.set("Content-Type", "application/json");
    inputHeaders.set("Accept", "application/json");
    return inputHeaders;
  }
}
