// src/main/java/com/runix/parser/ast/ForStmt.java
package com.runix.parser.ast;

/**
 * Representa una declaración de bucle for en el AST
 */
public class ForStmt implements Node {
    public final Expr init;      // Inicialización (ej: i = 0)
    public final Expr condition;  // Condición (ej: i < 10)
    public final Expr increment;  // Incremento (ej: i++)
    public final Node body;  // Cuerpo del bucle

    public ForStmt(Expr init, Expr condition, Expr increment, Node body) {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    @Override
    public <R> R accept(NodeVisitor<R> v) { 
        return v.visitForStmt(this); 
    }
}
