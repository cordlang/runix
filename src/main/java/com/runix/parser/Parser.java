// src/main/java/com/runix/parser/Parser.java
package com.runix.parser;

import com.runix.ast.*;
import java.util.*;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    public Program parse() {
        List<Node> statements = new ArrayList<>();
        while (!match(Token.Type.EOF)) {
            statements.add(declaration());
        }
        return new Program(statements);
    }

    private Node declaration() {
        if (matchIdentifier("func")) return functionDecl();
        if (matchIdentifier("let")) return varDecl();
        return statement();
    }

    private FunctionDecl functionDecl() {
        String name = consume(Token.Type.IDENTIFIER).value;
        consume(Token.Type.OPERATOR, "(");
        List<String> params = new ArrayList<>();
        if (!check(Token.Type.OPERATOR, ")")) {
            do { params.add(consume(Token.Type.IDENTIFIER).value); }
            while (match(Token.Type.OPERATOR, ","));
        }
        consume(Token.Type.OPERATOR, ")");
        consume(Token.Type.OPERATOR, "{");
        List<Node> body = new ArrayList<>();
        while (!check(Token.Type.OPERATOR, "}")) {
            body.add(declaration());
        }
        consume(Token.Type.OPERATOR, "}");
        return new FunctionDecl(name, params, body);
    }

    private VarDecl varDecl() {
        String name = consume(Token.Type.IDENTIFIER).value;
        consume(Token.Type.OPERATOR, "=");
        Expr initializer = expression();
        return new VarDecl(name, initializer);
    }

    private Node statement() {
        if (matchIdentifier("print")) return printStmt();
        if (matchIdentifier("if")) return ifStmt();
        if (matchIdentifier("while")) return whileStmt();
        return expressionStmt();
    }

    private PrintStmt printStmt() {
        Expr expr = expression();
        return new PrintStmt(expr);
    }

    private IfStmt ifStmt() {
        Expr cond = expression();
        consume(Token.Type.OPERATOR, "{");
        Node thenBranch = declaration();
        consume(Token.Type.OPERATOR, "}");
        Node elseBranch = null;
        if (matchIdentifier("else")) {
            consume(Token.Type.OPERATOR, "{");
            elseBranch = declaration();
            consume(Token.Type.OPERATOR, "}");
        }
        return new IfStmt(cond, thenBranch, elseBranch);
    }

    private WhileStmt whileStmt() {
        Expr cond = expression();
        consume(Token.Type.OPERATOR, "{");
        Node body = declaration();
        consume(Token.Type.OPERATOR, "}");
        return new WhileStmt(cond, body);
    }

    private ExpressionStmt expressionStmt() {
        Expr expr = expression();
        return new ExpressionStmt(expr);
    }

    private Expr expression() { return equality(); }

    private Expr equality() {
        Expr expr = addition();
        while (match(Token.Type.OPERATOR, "==", "!=")) {
            String op = previous().value;
            Expr right = addition();
            expr = new BinaryExpr(expr, op, right);
        }
        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();
        while (match(Token.Type.OPERATOR, "+", "-")) {
            String op = previous().value;
            Expr right = multiplication();
            expr = new BinaryExpr(expr, op, right);
        }
        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();
        while (match(Token.Type.OPERATOR, "*", "/")) {
            String op = previous().value;
            Expr right = unary();
            expr = new BinaryExpr(expr, op, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(Token.Type.OPERATOR, "-")) {
            String op = previous().value;
            Expr right = unary();
            return new BinaryExpr(new LiteralExpr(0), op, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(Token.Type.NUMBER)) {
            return new LiteralExpr(Integer.parseInt(previous().value));
        }
        if (match(Token.Type.STRING)) {
            return new LiteralExpr(previous().value);
        }
        if (match(Token.Type.IDENTIFIER)) {
            String name = previous().value;
            if (match(Token.Type.OPERATOR, "(")) {
                List<Expr> args = new ArrayList<>();
                if (!check(Token.Type.OPERATOR, ")")) {
                    do { args.add(expression()); }
                    while (match(Token.Type.OPERATOR, ","));
                }
                consume(Token.Type.OPERATOR, ")");
                return new CallExpr(name, args);
            }
            return new VariableExpr(name);
        }
        throw new RuntimeException("Esperaba expresión");
    }

    // Helpers
    private boolean match(Token.Type type, String... vals) {
        if (check(type, vals)) { advance(); return true; }
        return false;
    }
    private boolean match(Token.Type type) {
        if (check(type)) { advance(); return true; }
        return false;
    }
    private boolean matchIdentifier(String val) {
        if (check(Token.Type.IDENTIFIER, val)) { advance(); return true; }
        return false;
    }
    private Token consume(Token.Type type, String val) {
        if (check(type, val)) return advance();
        throw new RuntimeException("Se esperaba " + val);
    }
    private Token consume(Token.Type type) {
        if (check(type)) return advance();
        throw new RuntimeException("Se esperaba token de tipo " + type);
    }
    private boolean check(Token.Type type, String... vals) {
        if (isAtEnd()) return false;
        Token t = peek();
        if (t.type != type) return false;
        if (vals.length==0) return true;
        for (String v: vals) if (t.value.equals(v)) return true;
        return false;
    }
    private Token advance() { if (!isAtEnd()) pos++; return previous(); }
    private boolean isAtEnd() { return peek().type==Token.Type.EOF; }
    private Token peek() { return tokens.get(pos); }
    private Token previous() { return tokens.get(pos-1); }
}
