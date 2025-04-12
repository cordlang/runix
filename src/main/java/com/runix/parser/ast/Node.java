// src/main/java/com/runix/ast/Node.java
package com.runix.parser.ast;

/** Nodo base de AST */
public interface Node {
    <R> R accept(NodeVisitor<R> visitor);
}
