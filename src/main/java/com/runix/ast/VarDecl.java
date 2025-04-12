// src/main/java/com/runix/ast/VarDecl.java
package com.runix.ast;

public class VarDecl implements Node {
    public final String name;
    public final Expr initializer;
    public VarDecl(String name, Expr initializer) {
        this.name = name; this.initializer = initializer;
    }
    public <R> R accept(NodeVisitor<R> v) { return v.visit(this); }
}
