package com.example.starter.repository;

import com.example.starter.config.uploadConfig;
import com.example.starter.model.Credentials;
import com.example.starter.model.UserInfo;
import com.example.starter.model.callback.ResponeCallback;
import com.example.starter.validate.validateConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.Data;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class userRepository {
  private JDBCPool pgPool;
  private validateConfig validateConfig = new validateConfig();
  private ResponeCallback responeCallback = new ResponeCallback();
  private ObjectMapper objectMapper = new ObjectMapper();

  Date date = new Date();

  uploadConfig uploadConfig = new uploadConfig();

  public userRepository(JDBCPool pgPool) {
    this.pgPool = pgPool;
  }

  private Promise<Integer> registerUser(UUID iduser, String fullname, String birth, String citizenid, String phone, String email, String address) {
    Promise<Integer> promise = Promise.promise();
    String query = "INSERT INTO userinfo (iduser, fullname, birth, citizenid, phone, email, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
    pgPool
      .preparedQuery(query)
      .execute(Tuple.of(iduser, fullname, birth, citizenid, phone, email, address))
      .onSuccess(result -> {
        promise.complete(result.rowCount());
      })
      .onFailure(error -> {
        promise.fail(error);
      });

    return promise;
  }

  public void handleRegistration(RoutingContext routingContext) {
    if (!"application/json".equals(routingContext.request().getHeader("Content-Type"))) {
      routingContext.response()
        .setStatusCode(415)
        .end("Unsupported Media Type");
      return;
    }

    JsonObject requestBody;
    try {
      requestBody = routingContext.getBodyAsJson();
      if (requestBody == null) {
        throw new DecodeException("Invalid JSON payload");
      }
    } catch (DecodeException e) {
      routingContext.response()
        .setStatusCode(400)
        .end("Invalid JSON payload");
      return;
    }

    if (!routingContext.get("parsedParametersValidation").equals(true)) {
      routingContext.response()
        .setStatusCode(400)
        .end("Request validation failed");
      return;
    }

    String fullname = requestBody.getString("fullname");
    String birth = requestBody.getString("birth");
    String citizenid = requestBody.getString("citizenid");
    String phone = requestBody.getString("phone");
    String email = requestBody.getString("email");
    String address = requestBody.getString("address");
    UUID iduser = UUID.randomUUID();

    if (fullname == null || birth == null || citizenid == null || phone == null || email == null || address == null) {
      routingContext.response()
        .setStatusCode(400)
        .end("Missing required fields");
      return;
    }

    if (!validateConfig.isValidEmail(email)) {
      String jsonResponse;
      try {
        jsonResponse = responeCallback.responeCallback(1, "Invalid Email", null);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      routingContext.response()
        .setStatusCode(400)
        .putHeader("Content-Type", "application/json")
        .end(jsonResponse);
      return;
    }

    registerUser(iduser, fullname, birth, citizenid, phone, email, address)
      .future()
      .onComplete(result -> {
        if (result.succeeded()) {
          routingContext.response().end("User registered successfully");
        } else {
          Throwable error = result.cause();
          routingContext.response()
            .end(new JsonObject().put("error", "Failed to register user: " + error.getMessage()).encode());
        }
      });
  }

  public void select(RoutingContext context) {
    String pageParam = context.request().getParam("page");
    int pageNumber = 1; // Default page number

    if (pageParam != null) {
      try {
        pageNumber = Integer.parseInt(pageParam);
      } catch (NumberFormatException e) {
        context.response().setStatusCode(400).end("Invalid page number");
        return;
      }
    }

    int pageSize = 1; // Number of records per page
    int offset = (pageNumber - 1) * pageSize;

    pgPool.preparedQuery("SELECT * FROM userinfo LIMIT ? OFFSET ?")
      .execute(Tuple.of(pageSize, offset))
      .onFailure(e -> {
        System.out.println("Failed to execute the query: " + e.getMessage());
        context.response().setStatusCode(500).end("Failed to execute the query.");
      })
      .onSuccess(rows -> {
        List<UserInfo> users = new ArrayList<>();

        for (Row row : rows) {
          JsonObject json = row.toJson();
          UserInfo user;
          try {
            user = objectMapper.readValue(json.toString(), UserInfo.class); //mapping with model userinfo
            users.add(user);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
        responeCallback.responseClient(context, 200, 0, "Select success", users);
      });
  }

  public void handleRegist(RoutingContext context) {
    JsonObject jsonObject = context.getBodyAsJson();
    String phone = jsonObject.getString("phone");
    String password = jsonObject.getString("password");
    System.out.println(jsonObject);

    if (phone.trim().equals("") || password.trim().equals("")) {
      responeCallback.responseClient(context, 400, 1, "Fill is null", null);
    } else {
      pgPool.preparedQuery("select * from userinfo where phone = ?")
        .execute(Tuple.of(phone))
        .onSuccess(result -> {
          handleRegistSuccess(context, result, phone, password);
        })
        .onFailure(fail -> {
          responeCallback.responseClient(context, 400, 1, String.valueOf(fail.getCause()), null);
        })
        .onComplete(res -> {
          if (res.failed()) {
            responeCallback.responseClient(context, 500, 1, String.valueOf(res.cause()), null);
          }
        });
    }
  }

  private void handleRegistSuccess(RoutingContext context, RowSet<Row> result, String phone, String password) {
    if (result.size() > 0)
      responeCallback.responseClient(context, 400, 1, "Account already exist", null);
    else {
      String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
      pgPool.preparedQuery("insert into credential (phone, password) values (?,?)")
        .execute(Tuple.of(phone, hashedPassword))
        .onSuccess(e -> {
          responeCallback.responseClient(context, 200, 0, "Rigist account succss", null);
        })
        .onFailure(e -> {
          responeCallback.responseClient(context, 400, 1, String.valueOf(e.getCause()), null);
        });
    }
  }

  public void handleImageUpload(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    response.setChunked(true);

    JsonObject bodyJson = routingContext.getBodyAsJson();
    String iduser = bodyJson.getString("iduser");
    String frontImageBase64 = bodyJson.getString("front");
    String backgroundImgBase64 = bodyJson.getString("back");

    if (iduser != null && !iduser.isEmpty() && frontImageBase64 != null && !frontImageBase64.isEmpty() &&
      backgroundImgBase64 != null && !backgroundImgBase64.isEmpty()) {

      // Decode base64 strings into byte arrays
      byte[] frontImageBytes = Base64.getDecoder().decode(frontImageBase64);
      byte[] backgroundImgBytes = Base64.getDecoder().decode(backgroundImgBase64);

      FileSystem fileSystem = routingContext.vertx().fileSystem();
      String savePath = "cardImage";

      // Create user folder
      String userFolderPath = savePath + "\\" + iduser + "\\";
      try {
        Path userFolder = Paths.get(userFolderPath);
        Files.createDirectories(userFolder);
      } catch (IOException e) {
        e.printStackTrace();
        response.write("Failed to create user folder.\n");
        response.end();
        return;
      }

      final String[] resultUpload = new String[2];
      AtomicBoolean flag = new AtomicBoolean(true);

      // Check if image files exist
      String frontImagePath = userFolderPath + "front.jpg";
      String backImagePath = userFolderPath + "back.jpg";

      boolean frontImageExists = fileSystem.existsBlocking(frontImagePath);
      boolean backImageExists = fileSystem.existsBlocking(backImagePath);

      // Delete existing image files if they exist
      if (frontImageExists) {
        fileSystem.deleteBlocking(frontImagePath);
      }

      if (backImageExists) {
        fileSystem.deleteBlocking(backImagePath);
      }

      // Generate unique file names for the uploaded images
      String frontImageExtension = uploadConfig.getImageExtension(frontImageBase64);
      String backImageExtension = uploadConfig.getImageExtension(backgroundImgBase64);

      String frontImageName = uploadConfig.generateUniqueFileName() + frontImageExtension;
      String backImageName = uploadConfig.generateUniqueFileName() + backImageExtension;

      // Save front image
      uploadConfig.saveImageToFile(fileSystem, userFolderPath, "front.jpg", frontImageBytes, result -> {
        if (result.succeeded()) {
          resultUpload[0] = "Front image uploaded successfully";
        } else {
          flag.set(false);
          resultUpload[0] = "Front image uploaded failed";
        }

        // Save background image
        uploadConfig.saveImageToFile(fileSystem, userFolderPath, "back.jpg", backgroundImgBytes, bgResult -> {
          if (bgResult.succeeded()) {
            resultUpload[1] = "Back image uploaded successfully";
          } else {
            flag.set(false);
            resultUpload[1] = "Back image uploaded failed";
          }
        });

        if (flag.get() == true) {
          pgPool.preparedQuery("update userinfo set front_id = ?, back_id = ? where iduser = ?")
            .execute(Tuple.of(frontImagePath, backImagePath, iduser))
            .onSuccess(s->{
              responeCallback.responseClient(routingContext, 200, 0, Arrays.toString(resultUpload), null);
            })
            .onFailure(s->{
              responeCallback.responseClient(routingContext, 400, 1, Arrays.toString(resultUpload), null);
            });

        } else {
          responeCallback.responseClient(routingContext, 400, 1, Arrays.toString(resultUpload), null);
        }
      });
    } else {
      responeCallback.responseClient(routingContext, 400, 1, "Invalid or missing base64-encoded image data in the request", null);
    }
  }


  public void updatePassword(RoutingContext context) {
    JsonObject jsonObject = context.getBodyAsJson();
    String iduser = jsonObject.getString("iduser");
    String currentPass = jsonObject.getString("current_pass");
    String newPass = jsonObject.getString("new_pass");
    UserInfo[] userInfo = new UserInfo[1];

    pgPool.preparedQuery("SELECT * FROM userinfo WHERE iduser = ? LIMIT 1")
      .execute(Tuple.of(iduser))
      .onSuccess(rows -> {
        for (Row row : rows) {
          JsonObject json = row.toJson();
          UserInfo user;
          try {
            user = objectMapper.readValue(json.toString(), UserInfo.class);
            userInfo[0] = user;
            break;
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
        if (userInfo[0] != null) {
          UserInfo user = userInfo[0];
          String phone = user.getPhone();

          pgPool.preparedQuery("SELECT * FROM credential WHERE phone = ? LIMIT 1")
            .execute(Tuple.of(phone))
            .onFailure(f -> {
              responeCallback.responseClient(context, 400, 1, "Query Fail", null);
            })
            .onSuccess(rows2 -> {
              boolean foundCredentials = false;
              for (Row row : rows2) {
                JsonObject json2 = row.toJson();
                Credentials credentials;

                try {
                  credentials = objectMapper.readValue(json2.toString(), Credentials.class);
                  foundCredentials = true;
                  boolean checkPass = BCrypt.checkpw(currentPass, credentials.getPassword());
                  System.out.println(checkPass);
                  if (checkPass) {
                    String hashedNewPass;
                    try {
                      hashedNewPass = BCrypt.hashpw(newPass, BCrypt.gensalt());
                    } catch (IllegalArgumentException e) {
                      responeCallback.responseClient(context, 400, 1, "Failed to hash the new password", null);
                      return;
                    }

                    pgPool.preparedQuery("UPDATE credential SET password = ?, old_password = array_append(old_password, ?), update_pass = ? WHERE phone = ?")
                      .execute(Tuple.of(hashedNewPass, credentials.getPassword(), String.valueOf(new Timestamp(date.getTime())), credentials.getPhone()))
                      .onSuccess(updateResult -> {
                        responeCallback.responseClient(context, 200, 0, "Update success", null);
                      })
                      .onFailure(updateFailure -> {
                        responeCallback.responseClient(context, 400, 1, String.valueOf(updateFailure.getMessage()), null);
                      });
                  } else {
                    responeCallback.responseClient(context, 400, 1, "Current password incorrect", null);
                  }

                } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
                }
              }

              if (!foundCredentials) {
                responeCallback.responseClient(context, 400, 1, "User credentials not found", null);
              }
            });
        } else {
          responeCallback.responseClient(context, 400, 1, "User not found", null);
        }
      })
      .onFailure(f -> {
        responeCallback.responseClient(context, 400, 1, "Iduser not found", null);
      });
  }



}
