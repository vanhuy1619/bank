package com.example.starter.repository;

import com.example.starter.config.uploadConfig;
import com.example.starter.model.callback.ResponeCallback;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import com.example.starter.middleware.checkIdUser;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class cardRepository {
  private JDBCPool pgPool;

  checkIdUser checkIdUser = new checkIdUser();
  private uploadConfig uploadConfig = new uploadConfig();
  private ResponeCallback responeCallback = new ResponeCallback();
  Date date = new Date();

  private static String generateRandomNumber(int length) {
    StringBuilder sb = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < length; i++) {
      int digit = random.nextInt(10);
      sb.append(digit);
    }

    return sb.toString();
  }

  public cardRepository(JDBCPool pgPool) {
    this.pgPool = pgPool;
  }

  @SneakyThrows
  public void openCard(RoutingContext context) {
    if (!"application/json".equals(context.request().getHeader("Content-Type"))) {
      context.response()
        .setStatusCode(415)
        .end("Unsupported Media Type");
      return;
    }

    JsonObject object = context.getBodyAsJson();
    String iduser = object.getString("iduser");
    String typecard = object.getString("typecard");
    String cif = generateRandomNumber(16);
    String idcard = generateRandomNumber(14);
    String dateRegist = String.valueOf(new Timestamp(date.getTime()));

    checkIdUser.checkIdUser(pgPool, iduser)
      .thenAccept(userExists -> {
        String imagePath = handleImageUpload(context, iduser);

        if (userExists) {
          pgPool.preparedQuery("SELECT typecard FROM card WHERE iduser = ?")
            .execute(Tuple.of(iduser))
            .onSuccess(result -> {
              boolean typecardExists = false;
              List<Integer> existingTypecards = new ArrayList<>();

              if (result.size() == 0) {
                pgPool.preparedQuery("INSERT INTO card (iduser, idcard, date_regist, cvv, typecard, signature, cif) VALUES (?,?,?,?,ARRAY[?]::integer[],?,?)")
                  .execute(Tuple.of(iduser, idcard, dateRegist, 123, new String[]{typecard}, imagePath, cif))
                  .onSuccess(s -> {
                    responeCallback.responseClient(context, 200, 0, "Waiting for registering", null);
                  })
                  .onFailure(f -> {
                    responeCallback.responseClient(context, 400, 1, f.getMessage(), null);
                  });
              } else {
                for (Row row : result) {
                  List<Integer> typecardList = row.getJsonArray("typecard").getList();
                  existingTypecards.addAll(typecardList);

                  if (typecardList.contains(Integer.parseInt(typecard))) {
                    typecardExists = true;
                    break;
                  }
                }

                if (typecardExists) {
                  responeCallback.responseClient(context, 400, 1, "Record with the same typecard already exists", null);
                } else {
                  existingTypecards.add(Integer.parseInt(typecard));

                  pgPool.preparedQuery("UPDATE card SET typecard = ? WHERE iduser = ?")
                    .execute(Tuple.of(existingTypecards, iduser))
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
