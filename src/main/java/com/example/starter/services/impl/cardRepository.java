package com.example.starter.services.impl;

import com.example.starter.config.RandomNum;
import com.example.starter.config.uploadConfig;
import com.example.starter.model.Card;
import com.example.starter.model.callback.ResponeCallback;
import com.example.starter.services.CardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import com.example.starter.middleware.checkIdUser;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class cardRepository implements CardService {
  private JDBCPool pgPool;

  checkIdUser checkIdUser = new checkIdUser();
  private uploadConfig uploadConfig = new uploadConfig();
  private ResponeCallback responeCallback = new ResponeCallback();
  Date date = new Date();

  private ObjectMapper objectMapper = new ObjectMapper();

  public cardRepository(JDBCPool pgPool) {
    this.pgPool = pgPool;
  }

  @SneakyThrows
  @Override
  public void openCardType(RoutingContext context) {
    if (!"application/json".equals(context.request().getHeader("Content-Type"))) {
      context.response()
        .setStatusCode(415)
        .end("Unsupported Media Type");
      return;
    }

    JsonObject object = context.getBodyAsJson();
    String iduser = object.getString("iduser");
    Integer typecard = Integer.parseInt(object.getString("typecard"));
    String cif = RandomNum.generateRandomNumber(16);
    String idcard = RandomNum.generateRandomNumber(14);
    String dateRegist = String.valueOf(new Timestamp(date.getTime()));

    checkIdUser.checkIdUser(pgPool, iduser)
      .thenAccept(userExists -> {
        if (userExists != null)
        {
          pgPool.preparedQuery("SELECT * FROM card WHERE iduser = ?")
            .execute(Tuple.of(iduser))
            .onSuccess(result -> {
              if (result.size() == 0)
              {
                // create and save image
                String imagePath = handleImageUpload(context, iduser);
                pgPool.preparedQuery("INSERT INTO card (iduser, idcard, date_regist, cvv, typecard, signature, cif, status_approve) VALUES (?,?,?,?,ARRAY[?]::integer[],?,?,?)")
                  .execute(Tuple.of(iduser, idcard, dateRegist, 123, typecard, imagePath, cif,"Waiting Approve All"))
                  .onSuccess(s -> {
                    responeCallback.responseClient(context, 200, 0, "Waiting for registering", null);
                  })
                  .onFailure(f -> {
                    responeCallback.responseClient(context, 400, 1, f.getMessage(), null);
                  });
              } else {
                List<Integer> typeCards = new ArrayList<>();
                for (Row row : result) {
                  JsonObject json = row.toJson();
                  try {
                    Card card = objectMapper.readValue(json.toString(), Card.class);
                    typeCards.addAll(card.getTypeCard());
                  } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                  }
                }
                if (typeCards.contains(typecard)) {
                  responeCallback.responseClient(context, 200, 1,"Typecard card has registered", null);
                } else {
                  typeCards.add(typecard);
                  pgPool.preparedQuery("UPDATE card SET typecard = array_append(typecard,?) WHERE iduser = ?")
                    .execute(Tuple.of(typecard, iduser))
                    .onSuccess(updateResult -> {
                      responeCallback.responseClient(context, 200, 0, "Typecard appended successfully", null);
                    })
                    .onFailure(f -> {
                      responeCallback.responseClient(context, 400, 1, f.getMessage(), null);
                    });
                }
              }
            })
            .onFailure(f -> {
              responeCallback.responseClient(context, 400, 1, f.getMessage(), null);
            });
        } else {
          // Handle case when user does not exist
          responeCallback.responseClient(context, 400, 1,"User not found", null);
        }
      });
  }

  public String handleImageUpload(RoutingContext routingContext, String iduser) {
    HttpServerResponse response = routingContext.response();
    response.setChunked(true);

    JsonObject bodyJson = routingContext.getBodyAsJson();
    String signature = bodyJson.getString("signature");

    if (iduser != null && !iduser.isEmpty() && signature != null && !signature.isEmpty()) {
      byte[] signatureBytes = Base64.getDecoder().decode(signature);

      FileSystem fileSystem = routingContext.vertx().fileSystem();
      String savePath = "cardImage";

      // Create user folder
      String userFolderPath = savePath + File.separator + iduser + File.separator;
      try {
        Path userFolder = Paths.get(userFolderPath);
        Files.createDirectories(userFolder);
      } catch (IOException e) {
        e.printStackTrace();
        response.write("Failed to create user folder.\n");
        response.end();
        return null;
      }

      final String[] resultUpload = new String[2];
      AtomicBoolean flag = new AtomicBoolean(true);

      // Check if image files exist
      String signatureImagePath = userFolderPath + "signature.jpg";

      boolean frontImageExists = fileSystem.existsBlocking(signatureImagePath);

      // Delete existing image files if they exist
      if (frontImageExists) {
        fileSystem.deleteBlocking(signatureImagePath);
      }

      uploadConfig.saveImageToFile(fileSystem, userFolderPath, "signature.jpg", signatureBytes, bgResult -> {
        if (bgResult.succeeded()) {
          resultUpload[0] = "Image uploaded successfully";
        } else {
          flag.set(false);
          resultUpload[0] = "Image upload failed";
        }
      });

      return signatureImagePath;
    } else {
      responeCallback.responseClient(routingContext, 400, 1, "Invalid or missing base64-encoded image data in the request", null);
      return null;
    }
  }

}
