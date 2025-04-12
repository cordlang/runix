package com.runix.parser;

import java.util.ArrayList;
import java.util.List;

import com.runix.lexer.Token;
import com.runix.parser.ast.BlockStmt;
import com.runix.parser.ast.Expr;
import com.runix.parser.ast.FunctionDecl;
import com.runix.parser.ast.IfStmt;
import com.runix.parser.ast.Node;
import com.runix.parser.ast.PrintStmt;
import com.runix.parser.ast.Program;
import com.runix.parser.ast.ReturnStmt;
import com.runix.parser.ast.VarDecl;
import com.runix.parser.ast.WhileStmt;
import com.runix.parser.ast.BinaryExpr;
import com.runix.parser.ast.LiteralExpr;
import com.runix.parser.ast.VariableExpr;
import com.runix.parser.ast.CallExpr;
import com.runix.parser.ast.ExpressionStmt;

public class Parser {
    private final Token[] tokens;
    private int pos = 0;
    private final int end;
    
    public Parser(List<Token> tokens) {
        this.tokens = tokens.toArray(new Token[0]);
        this.end = tokens.size();
    }

    public Program parse() {
        List<Node> statements = new ArrayList<>();
        while (!isAtEnd()) {
            Node decl = declaration();
            if (decl != null) {
                statements.add(decl);
            }
        }
        return new Program(statements);
    }

    private Node declaration() {
        try {
            if (matchIdentifier("func"))
                return functionDecl();
            if (matchIdentifier("let")) {
                String name = consume(Token.Type.IDENTIFIER).value;
                Expr initializer = null;
                if (match(Token.Type.OPERATOR, "=")) {
                    initializer = expression();
                }
                consume(Token.Type.OPERATOR, ";");
                return new VarDecl(name, initializer);
            }
            if (matchIdentifier("const")) {
                String name = consume(Token.Type.IDENTIFIER).value;
                consume(Token.Type.OPERATOR, "=");
                Expr initializer = expression();
                consume(Token.Type.OPERATOR, ";");
                return new VarDecl(name, initializer);
            }
            return statement();
        } catch (RuntimeException e) {
            synchronize();
            return null;
        }
    }

    private FunctionDecl functionDecl() {
        String name = consume(Token.Type.IDENTIFIER).value;
        consume(Token.Type.OPERATOR, "(");
        List<String> params = new ArrayList<>();
        if (!check(Token.Type.OPERATOR, ")")) {
            do {
                params.add(consume(Token.Type.IDENTIFIER).value);
            } while (match(Token.Type.OPERATOR, ","));
        }
        consume(Token.Type.OPERATOR, ")");
        BlockStmt body = block();
        return new FunctionDecl(name, params, body);
    }

    private Node statement() {
        try {
            if (matchIdentifier("print")) {
                Expr expr = expression();
                consume(Token.Type.OPERATOR, ";");
                return new PrintStmt(expr);
            }
            if (matchIdentifier("if"))
                return ifStmt();
            if (matchIdentifier("while"))
                return whileStmt();
            if (match(Token.Type.OPERATOR, "return")) {
                Expr value = expression();
                consume(Token.Type.OPERATOR, ";");
                return new ReturnStmt(value);
            }
            return expressionStmt();
        } catch (RuntimeException e) {
            synchronize();
            return null;
        }
    }

    private IfStmt ifStmt() {
        Expr condition = expression();
        BlockStmt thenBranch = block();
        BlockStmt elseBranch = null;
        if (matchIdentifier("else")) {
            elseBranch = block();
        }
        return new IfStmt(condition, thenBranch, elseBranch);
    }

    private WhileStmt whileStmt() {
        Expr condition = expression();
        BlockStmt body = block();
        return new WhileStmt(condition, body);
    }

    private ExpressionStmt expressionStmt() {
        Expr expr = expression();
        consume(Token.Type.OPERATOR, ";");
        return new ExpressionStmt(expr);
    }

    private BlockStmt block() {
        consume(Token.Type.OPERATOR, "{");
        List<Node> statements = new ArrayList<>();
        while (!check(Token.Type.OPERATOR, "}")) {
            statements.add(declaration());
        }
        consume(Token.Type.OPERATOR, "}");
        return new BlockStmt(statements);
    }

    // Expresiones con precedencia
    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();
        if (match(Token.Type.OPERATOR, "=")) {
            Expr value = assignment();
            if (expr instanceof VariableExpr) {
                return new BinaryExpr(expr, "=", value);
            }
            throw new RuntimeException("Target de asignación inválido");
        }
        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();
        while (match(Token.Type.OPERATOR, "==", "!=")) {
            String op = previous().value;
            Expr right = comparison();
            expr = new BinaryExpr(expr, op, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();
        while (match(Token.Type.OPERATOR, ">", ">=", "<", "<=")) {
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
        if (match(Token.Type.OPERATOR, "-", "!")) {
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
                    do {
                        args.add(expression());
                    } while (match(Token.Type.OPERATOR, ","));
                }
                consume(Token.Type.OPERATOR, ")");
                return new CallExpr(name, args);
            }
            return new VariableExpr(name);
        }
        if (match(Token.Type.OPERATOR, "(")) {
            Expr expr = expression();
            consume(Token.Type.OPERATOR, ")");
            return expr;
        }
        throw new RuntimeException("Se esperaba una expresión en " + peek());
    }

    // Métodos auxiliares
    private boolean match(Token.Type type, String... vals) {
        if (isAtEnd()) return false;
        Token t = peek();
        if (t.type != type) return false;
        if (vals.length > 0 && !contains(vals, t.value)) return false;
        pos++;
        return true;
    }

    private boolean contains(String[] array, String value) {
        for (String s : array) {
            if (s.equals(value)) return true;
        }
        return false;
    }

    private Token consume(Token.Type type, String val) {
        if (isAtEnd()) throw new RuntimeException("Se esperaba '" + val + "' pero se llegó al final");
        Token t = peek();
        if (t.type != type || !t.value.equals(val)) {
            throw new RuntimeException("Se esperaba '" + val + "' pero encontré '" + t.value + "'");
        }
        return advance();
    }

    private Token consume(Token.Type type) {
        if (isAtEnd()) throw new RuntimeException("Se esperaba token de tipo " + type + " pero se llegó al final");
        Token t = peek();
        if (t.type != type) {
            throw new RuntimeException("Se esperaba token de tipo " + type + " pero encontré " + t.type);
        }
        return advance();
    }

    private Token advance() {
        return tokens[pos++];
    }

    private boolean isAtEnd() {
        return pos >= end;
    }

    private Token peek() {
        return tokens[pos];
    }

    private Token previous() {
        return tokens[pos - 1];
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == Token.Type.OPERATOR && previous().value.equals(";")) return;
            if (previous().type == Token.Type.IDENTIFIER) {
                String val = previous().value;
                if (val.equals("func") || val.equals("let") || val.equals("const") || 
                    val.equals("if") || val.equals("while") || val.equals("print") || 
                    val.equals("return")) {
                    return;
                }
            }
            advance();
        }
    }

    private boolean matchIdentifier(String val) {
        if (isAtEnd()) return false;
        Token t = peek();
        if (t.type != Token.Type.IDENTIFIER || !t.value.equals(val)) return false;
        pos++;
        return true;
    }

    private boolean check(Token.Type type, String... vals) {
        if (isAtEnd()) return false;
        Token t = peek();
        if (t.type != type) return false;
        if (vals.length > 0 && !contains(vals, t.value)) return false;
        return true;
    }
}