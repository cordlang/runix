// src/main/java/com/runix/ast/ExpressionStmt.java
package com.runix.ast;

public class ExpressionStmt implements Node {
    public final Expr expression;
    public ExpressionStmt(Expr expression) { this.expression = expression; }
    public <R> R accept(NodeVisitor<R> v) { return v.visit(this); }
}
