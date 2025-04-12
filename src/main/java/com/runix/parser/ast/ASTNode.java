package com.runix.parser.ast;

public abstract class ASTNode implements Node {
    @Override
    public abstract <R> R accept(NodeVisitor<R> visitor);
}