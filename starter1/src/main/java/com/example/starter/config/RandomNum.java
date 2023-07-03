package com.example.starter.config;

import java.util.Random;

public class RandomNum {
  public static String generateRandomNumber(int length) {
    StringBuilder sb = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < length; i++) {
      int digit = random.nextInt(10);
      sb.append(digit);
    }

    return sb.toString();
  }

}
