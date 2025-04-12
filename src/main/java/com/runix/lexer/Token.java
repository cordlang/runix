// src/main/java/com/runix/lexer/Token.java
package com.runix.lexer;

public class Token {
    public enum Type {
        IDENTIFIER, NUMBER, STRING, OPERATOR, EOF,
        PLUS, MINUS, STAR, SLASH, LT, GT, LTEQ, GTEQ, EQEQ, BANGEQ
    }
    public final Type type;
    public final String value;
    
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
        
        // Validar que los números sean válidos
        if (type == Type.NUMBER) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Número inválido: " + value);
            }
        }
    }
    
    @Override
    public String toString() {
        return "[" + type + ": \"" + value + "\"]";
    }
    
    public int asNumber() {
        if (type != Type.NUMBER) {
            throw new IllegalArgumentException("El token no es un número");
        }
        return Integer.parseInt(value);
    }
}
