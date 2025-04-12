package com.runix.parser.ast;

/** Visitor para recorrer el AST */
public interface NodeVisitor<R> {
    R visitProgram(Program node);
    R visitFunctionDecl(FunctionDecl node);
    R visitVarDecl(VarDecl node);
    R visitReturnStmt(ReturnStmt node);
    R visitPrintStmt(PrintStmt node);
    R visitIfStmt(IfStmt node);
    R visitWhileStmt(WhileStmt node);
    R visitForStmt(ForStmt node);
    R visitExpressionStmt(ExpressionStmt node);
    R visitBlockStmt(BlockStmt node);
    R visitBinaryExpr(BinaryExpr node);
    R visitLiteralExpr(LiteralExpr node);
    R visitVariableExpr(VariableExpr node);
    R visitCallExpr(CallExpr node);
}
