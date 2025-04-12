// src/main/java/com/runix/ast/WhileStmt.java
package com.runix.parser.ast;

public class WhileStmt implements Node {
    public final Expr condition;
    public final Node body;
    public WhileStmt(Expr condition, Node body) {
        this.condition = condition; this.body = body;
    }
    public <R> R accept(NodeVisitor<R> v) { return v.visit(this); }
}
