package com.example.starter.validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class validateConfig {
  private static final String EMAIL_PATTERN =
    "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

  private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
  private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^0\\d{9}$");


  public boolean isValidEmail(String email) {
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  public boolean isValidPhone(String phone)
  {
    if (phone.trim().isEmpty())
      return false;
    return PHONE_NUMBER_PATTERN.matcher(phone).matches();
  }

  public String shuffleString(String input) {
    // Chuyển đổi chuỗi thành danh sách các ký tự
    List<Character> charList = new ArrayList<>();
    for (char c : input.toCharArray()) {
      charList.add(c);
    }

    // Trộn các ký tự trong danh sách
    Collections.shuffle(charList);

    // Xây dựng chuỗi mới từ danh sách đã trộn
    StringBuilder shuffledString = new StringBuilder();
    for (char c : charList) {
      shuffledString.append(c);
    }
    return shuffledString.toString();
  }
}
