// src/main/java/com/runix/ast/LiteralExpr.java
package com.runix.parser.ast;

public class LiteralExpr implements Expr {
    public final Object value;
    public LiteralExpr(Object value) { this.value = value; }
    public <R> R accept(NodeVisitor<R> v) { return v.visitLiteralExpr(this); }
}
