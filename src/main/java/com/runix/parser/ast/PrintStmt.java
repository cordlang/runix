// src/main/java/com/runix/ast/PrintStmt.java
package com.runix.parser.ast;

public class PrintStmt implements Node {
    public final Expr expression;
    public PrintStmt(Expr expression) { this.expression = expression; }
    public <R> R accept(NodeVisitor<R> v) { return v.visitPrintStmt(this); }
}
