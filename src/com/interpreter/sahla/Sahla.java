package com.interpreter.sahla;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Sahla {
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: msahel [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        while(true)
        {
            System.out.printf("> ");
            String line = reader.readLine();
            if(line == null) break;
            run(line);
            ErrorHandler.hadError = false;
        }
    }

    private static void run(String source) {
        SahlaScanner scanner = new SahlaScanner(source);
        List<Token> tokens = scanner.scanCodeSource();
        for(Token token: tokens)
        {
            System.out.println(token);
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes,Charset.defaultCharset()));
        if(ErrorHandler.hadError) System.exit(65); // i refer to exit because of error by 65
    }
}
