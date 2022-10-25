package com.interpreter.sahla;

public class ErrorHandler {
    public static boolean hadError = false;
    public static void error(int line,String message)
    {
        report(line,"",message);
    }
    public static void report(int line,String where, String message)
    {
        System.out.println(
                "[Line " + line + "] Error " + where + ": " + message);
        hadError = true;
    }
}
