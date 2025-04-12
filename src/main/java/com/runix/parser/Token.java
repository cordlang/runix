// src/main/java/com/runix/parser/Token.java
package com.runix.parser;

public class Token {
    public enum Type {
        IDENTIFIER, NUMBER, STRING, OPERATOR, EOF
    }
    public final Type type;
    public final String value;
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }
    @Override
    public String toString() {
        return "[" + type + ": \"" + value + "\"]";
    }
}
