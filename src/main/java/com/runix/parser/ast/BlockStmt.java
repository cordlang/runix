package com.runix.parser.ast;

import java.util.List;

public class BlockStmt extends ASTNode {
    private final List<Node> statements;

    public BlockStmt(List<Node> statements) {
        this.statements = statements;
    }

    public List<Node> getStatements() {
        return statements;
    }

    @Override
    public <R> R accept(NodeVisitor<R> visitor) {
        return visitor.visit(this);
    }
}