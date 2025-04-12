// src/main/java/com/runix/lexer/Tokenizer.java
package com.runix.lexer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private final String input;
    private int pos = 0;

    public Tokenizer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }
            if (Character.isDigit(c)) {
                tokens.add(readNumber());
                continue;
            }
            if (Character.isLetter(c)) {
                tokens.add(readIdentifier());
                continue;
            }
            if ("+-*/=(){}<>!,;".indexOf(c) >= 0) {
                tokens.add(new Token(Token.Type.OPERATOR, String.valueOf(c)));
                pos++;
                continue;
            }
            if (c == '"') {
                tokens.add(readString());
                continue;
            }
            pos++;
        }
        tokens.add(new Token(Token.Type.EOF, ""));
        return tokens;
    }

    private Token readNumber() {
        int start = pos;
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) pos++;
        return new Token(Token.Type.NUMBER, input.substring(start, pos));
    }

    private Token readIdentifier() {
        int start = pos;
        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) pos++;
        return new Token(Token.Type.IDENTIFIER, input.substring(start, pos));
    }

    private Token readString() {
        pos++;  // saltar comilla inicial
        int start = pos;
        while (pos < input.length() && input.charAt(pos) != '"') pos++;
        String val = input.substring(start, pos);
        pos++;  // saltar comilla final
        return new Token(Token.Type.STRING, val);
    }
}
