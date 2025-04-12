// src/main/java/com/runix/ast/FunctionDecl.java
package com.runix.ast;

import java.util.List;

public class FunctionDecl implements Node {
    public final String name;
    public final List<String> params;
    public final List<Node> body;
    public FunctionDecl(String name, List<String> params, List<Node> body) {
        this.name = name; this.params = params; this.body = body;
    }
    public <R> R accept(NodeVisitor<R> v) { return v.visit(this); }
}
