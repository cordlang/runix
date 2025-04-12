// src/main/java/com/runix/ast/IfStmt.java
package com.runix.parser.ast;

public class IfStmt implements Node {
    public final Expr condition;
    public final Node thenBranch, elseBranch;
    public IfStmt(Expr condition, Node thenBranch, Node elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    public <R> R accept(NodeVisitor<R> v) { return v.visit(this); }
}
