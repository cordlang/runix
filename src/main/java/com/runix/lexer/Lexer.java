package com.runix.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private String input;
    private int position;
    private int currentChar;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        if (input.length() > 0) {
            this.currentChar = input.charAt(position);
        }
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        
        while (position < input.length()) {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }
            
            if (Character.isDigit(currentChar)) {
                tokens.add(number());
                continue;
            }
            
            if (Character.isLetter(currentChar)) {
                tokens.add(identifier());
                continue;
            }
            
            if (currentChar == '"') {
                tokens.add(string());
                continue;
            }
            
            // Handle operators
            if (isOperator((char)currentChar)) {
                tokens.add(new Token(Token.Type.OPERATOR, String.valueOf(currentChar)));
                position++;
                if (position < input.length()) {
                    currentChar = input.charAt(position);
                }
                continue;
            }
            
            // If we get here, it's an unexpected character
            throw new RuntimeException("Unexpected character: " + currentChar);
        }
        
        tokens.add(new Token(Token.Type.EOF, ""));
        return tokens;
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(currentChar)) {
            position++;
            if (position < input.length()) {
                currentChar = input.charAt(position);
            }
        }
    }

    private Token number() {
        StringBuilder number = new StringBuilder();
        while (position < input.length() && Character.isDigit(currentChar)) {
            number.append(currentChar);
            position++;
            if (position < input.length()) {
                currentChar = input.charAt(position);
            }
        }
        return new Token(Token.Type.NUMBER, number.toString());
    }

    private Token identifier() {
        StringBuilder identifier = new StringBuilder();
        while (position < input.length() && 
               (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            identifier.append((char)currentChar);
            position++;
            if (position < input.length()) {
                currentChar = input.charAt(position);
            }
        }
        return new Token(Token.Type.IDENTIFIER, identifier.toString());
    }

    private Token string() {
        StringBuilder string = new StringBuilder();
        position++; // Skip the opening quote
        if (position < input.length()) {
            currentChar = input.charAt(position);
        }
        
        while (position < input.length() && currentChar != '"') {
            if (currentChar == '\\') {
                position++; // Skip the backslash
                if (position < input.length()) {
                    currentChar = input.charAt(position);
                    // Handle escape sequences
                    switch (currentChar) {
                        case 'n': string.append('\n'); break;
                        case 't': string.append('\t'); break;
                        case '"': string.append('"'); break;
                        case '\\': string.append('\\'); break;
                        default: string.append((char)currentChar); break;
                    }
                }
            } else {
                string.append((char)currentChar);
            }
            position++;
            if (position < input.length()) {
                currentChar = input.charAt(position);
            }
        }
        
        if (currentChar != '"') {
            throw new RuntimeException("Unterminated string literal");
        }
        
        position++; // Skip the closing quote
        if (position < input.length()) {
            currentChar = input.charAt(position);
        }
        
        return new Token(Token.Type.STRING, string.toString());
    }

    private boolean isOperator(char c) {
        // Add your operators here
        return c == '+' || c == '-' || c == '*' || c == '/' || 
               c == '=' || c == '(' || c == ')' || c == '{' || c == '}' ||
               c == ';' || c == ',' || c == ':' || c == '.' ||
               c == '&' || c == '|' || c == '!' || c == '>' || c == '<';
    }
}
