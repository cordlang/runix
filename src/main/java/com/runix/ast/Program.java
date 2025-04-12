// src/main/java/com/runix/ast/Program.java
package com.runix.ast;

import java.util.List;

public class Program implements Node {
    public final List<Node> statements;
    public Program(List<Node> statements) { this.statements = statements; }
    public <R> R accept(NodeVisitor<R> v) { return v.visit(this); }
}
