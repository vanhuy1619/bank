package com.example.starter.config;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;

import java.util.UUID;

public class uploadConfig {
  public String getImageExtension(String base64String) {
    String extension = "";
    if (base64String.startsWith("data:image/png")) {
      extension = ".png";
    } else if (base64String.startsWith("data:image/jpeg") || base64String.startsWith("data:image/jpg")) {
      extension = ".jpeg";
    }
    // Add more conditions for other image formats if needed

    return extension;
  }

  public String generateUniqueFileName() {
    return UUID.randomUUID().toString();
  }
  public void saveImageToFile(FileSystem fileSystem, String savePath, String fileName, byte[] imageBytes, io.vertx.core.Handler<io.vertx.core.AsyncResult<Void>> handler) {
    Buffer buffer = Buffer.buffer(imageBytes);
    fileSystem.open(savePath + fileName, new OpenOptions(), openResult -> {
      if (openResult.succeeded()) {
        AsyncFile asyncFile = openResult.result();
        asyncFile.write(buffer, 0, writeResult -> {
          if (writeResult.succeeded()) {
            asyncFile.close(closeResult -> {
              if (closeResult.succeeded()) {
                handler.handle(io.vertx.core.Future.succeededFuture());
              } else {
                handler.handle(io.vertx.core.Future.failedFuture(closeResult.cause()));
              }
            });
          } else {
            asyncFile.close(closeResult -> {
              if (closeResult.succeeded()) {
                handler.handle(io.vertx.core.Future.failedFuture(writeResult.cause()));
              } else {
                handler.handle(io.vertx.core.Future.failedFuture(closeResult.cause()));
              }
            });
          }
        });
      } else {
        handler.handle(io.vertx.core.Future.failedFuture(openResult.cause()));
      }
    });
  }

}
