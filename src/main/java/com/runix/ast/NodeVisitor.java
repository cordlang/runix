// src/main/java/com/runix/ast/NodeVisitor.java
package com.runix.ast;

/** Visitor para recorrer el AST */
public interface NodeVisitor<R> {
    R visit(Program node);
    R visit(FunctionDecl node);
    R visit(VarDecl node);
    R visit(PrintStmt node);
    R visit(IfStmt node);
    R visit(WhileStmt node);
    R visit(ExpressionStmt node);
    R visit(BinaryExpr node);
    R visit(LiteralExpr node);
    R visit(VariableExpr node);
    R visit(CallExpr node);
}
