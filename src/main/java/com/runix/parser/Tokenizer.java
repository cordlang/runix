// src/main/java/runix/parser/Tokenizer.java

package com.runix.parser;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private final String input;
    private int position = 0;

    public Tokenizer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char current = input.charAt(position);

            if (Character.isWhitespace(current)) {
                position++;
                continue;
            }

            if (Character.isDigit(current)) {
                tokens.add(readNumber());
                continue;
            }

            if (Character.isLetter(current)) {
                tokens.add(readIdentifier());
                continue;
            }

            // Simples operadores por ahora
            if ("+-*/=(){}".contains(String.valueOf(current))) {
                tokens.add(new Token(Token.Type.OPERATOR, String.valueOf(current)));
                position++;
                continue;
            }

            // Comillas para strings
            if (current == '"') {
                tokens.add(readString());
                continue;
            }

            position++;
        }

        tokens.add(new Token(Token.Type.EOF, ""));
        return tokens;
    }

    private Token readNumber() {
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            sb.append(input.charAt(position++));
        }
        return new Token(Token.Type.NUMBER, sb.toString());
    }

    private Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && Character.isLetterOrDigit(input.charAt(position))) {
            sb.append(input.charAt(position++));
        }
        return new Token(Token.Type.IDENTIFIER, sb.toString());
    }

    private Token readString() {
        position++; // Saltar comilla inicial
        StringBuilder sb = new StringBuilder();
        while (position < input.length() && input.charAt(position) != '"') {
            sb.append(input.charAt(position++));
        }
        position++; // Saltar comilla final
        return new Token(Token.Type.STRING, sb.toString());
    }
}
