package com.example.starter.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class StackTraceUtil {
  public static String getStackTrace(Throwable throwable)
  {
    try
    {
     final Writer result = new StringWriter();
     final PrintWriter printWriter = new PrintWriter(result);
     throwable.printStackTrace(printWriter);
     return result.toString();
    }
    catch (Exception e)
    {
      return "Null printer exception";
    }
  }
}
