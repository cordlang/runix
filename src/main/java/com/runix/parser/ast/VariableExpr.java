// src/main/java/com/runix/ast/VariableExpr.java
package com.runix.parser.ast;

public class VariableExpr implements Expr {
    public final String name;
    public VariableExpr(String name) { this.name = name; }
    public <R> R accept(NodeVisitor<R> v) { return v.visitVariableExpr(this); }
}
