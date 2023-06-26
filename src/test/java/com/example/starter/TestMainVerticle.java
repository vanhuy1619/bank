package com.example.starter;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.IOException;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    testContext.completeNow();
  }

  @Test
  void base64()
  {
    try {
      // Đường dẫn tới tập tin hình ảnh
      String imagePath = "C:\\Users\\ASUS\\Pictures\\Camera Roll\\WIN_20210905_12_38_39_Pro.jpg";

      // Đọc nội dung của tập tin hình ảnh thành một mảng byte
      byte[] imageBytes = FileUtils.readFileToByteArray(new File(imagePath));

      // Mã hóa mảng byte thành base64
      String base64Image = Base64.encodeBase64String(imageBytes);

      // In ra chuỗi base64 của hình ảnh
      System.out.println(base64Image);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
