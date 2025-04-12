// src/main/java/com/runix/parser/ast/FunctionDecl.java
package com.runix.parser.ast;

import java.util.List;

public class FunctionDecl implements Node {
    public final String name;
    public final List<String> params;
    public final BlockStmt body;
    
    public FunctionDecl(String name, List<String> params, BlockStmt body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    @Override
    public <R> R accept(NodeVisitor<R> v) {
        return v.visitFunctionDecl(this);
    }
}
