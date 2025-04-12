package com.runix.parser.ast;

public class ReturnStmt implements Node {
    private final Expr value;

    public ReturnStmt(Expr value) {
        this.value = value;
    }

    public Expr getValue() {
        return value;
    }

    @Override
    public <R> R accept(NodeVisitor<R> visitor) {
        return visitor.visitReturnStmt(this);
    }
}