package com.interpreter.sahla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.interpreter.sahla.TokenType.*;
import static com.interpreter.sahla.ErrorHandler.*;

public class SahlaScanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("funct",    FUNCT);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("Aktob",  AKTOB);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }
    public SahlaScanner(String source) {
        this.source = source;
    }
    public List<Token> scanCodeSource()
    {
        while(!(isAtEnd()))
        {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF,"",null,line));
        return tokens;
    }
    public void scanToken()
    {
        char c = advance();
        switch(c)
        {
            case '(' : addToken(LEFT_PAREN); break;
            case ')' : addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '/':
                if(match('/'))
                {
                    while(source.charAt(current) != '\n' && !isAtEnd()) {
                        advance();
                    }
                }
                else if(match('*'))
                {
                    while ( (peek() != '*' && peekNext() != '/') && !isAtEnd())
                    {
                        if(source.charAt(current) == '\n')
                            line++;
                        advance();
                    }
                    if(isAtEnd())
                        ErrorHandler.error(line,"Unterminated Comment.");
                }
                else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            case 'O':
                if(match('R'))
                    addToken(OR);
                break;


            // MSahla interpreter will report error if an unused character is in sourceCode
            default:
                if(isDigit(c))
                    number();
                else if (isAlpha(c))
                    identifier();
                else
                    error(line,"Chawala hada ? mafhmtch sahbi");
                break;

        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        addToken(IDENTIFIER);
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private void number()
    {
        while(isDigit(peek())) advance();

        // looking for fractional part
        if(peek() == '.' && isDigit(source.charAt(current + 1)))
        {
            advance(); // advance to get to the fractional part
            while(isDigit(peek())) advance();
        }
        addToken(NUMBER,Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    /*
        while ( (peek() != '*' && peekNext() != '/') && !isAtEnd())
        {

     */
    private void string() {
        while(peek() != '"' && !isAtEnd())
        {
            if(source.charAt(current) == '\n')
                line++;
            advance();
        }
        if(isAtEnd())
        {
            ErrorHandler.error(line,"Unterminated String.");
            return;
        }
        String value = source.substring(start + 1 , current);
        addToken(STRING,value);
        advance();
    }

    private char peek() {
        if(isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    /*
        Using match(), we recognize these lexemes in two stages. When we reach, for example
        we jump to its switch case. That means we know the lexeme starts with !.
        Then we look at the next character to determine if we’re on a != or merely a !.
     */

    private boolean match(char expected) {
        if(isAtEnd())
            return false;
        if(source.charAt(current) != expected)
            return false;
        current++;
        return true;
    }
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }
    // the first addToken() method is used for tokens without literal value

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    private boolean isAtEnd() {
        return current >= source.length();
    }
    private char advance() {
        return source.charAt(current++);
    }

}
