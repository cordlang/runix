// src/main/java/com/runix/ast/BinaryExpr.java
package com.runix.parser.ast;

public class BinaryExpr implements Expr {
    public final Expr left;
    public final String operator;
    public final Expr right;
    public BinaryExpr(Expr left, String operator, Expr right) {
        this.left = left; this.operator = operator; this.right = right;
    }
    public <R> R accept(NodeVisitor<R> v) { return v.visitBinaryExpr(this); }
}
