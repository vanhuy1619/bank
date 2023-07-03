package com.example.starter.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class StackTraceUtil {
        public static String getStackTrace(Throwable aThrowable)
        {
            try
            {
                final Writer result = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(result);
                aThrowable.printStackTrace(printWriter);
                return result.toString();
            }
            catch(Exception ex)
            {
                return "Null pointer Exception";
            }
        }
}
