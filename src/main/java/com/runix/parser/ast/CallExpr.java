// src/main/java/com/runix/ast/CallExpr.java
package com.runix.parser.ast;

import java.util.List;

public class CallExpr implements Expr {
    public final String callee;
    public final List<Expr> arguments;
    public CallExpr(String callee, List<Expr> arguments) {
        this.callee = callee; this.arguments = arguments;
    }
    public <R> R accept(NodeVisitor<R> v) { return v.visit(this); }
}
