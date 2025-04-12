// src/main/java/com/runix/evaluator/Evaluator.java
package com.runix.evaluator;

import com.runix.ast.*;
import java.util.*;

public class Evaluator implements NodeVisitor<Object> {
    private final Map<String, Object> globals = new HashMap<>();
    private final Map<String, FunctionDecl> functions = new HashMap<>();

    public void evaluateProgram(Program program) {
        for (Node stmt : program.statements) {
            stmt.accept(this);
        }
    }

    @Override public Object visit(Program node) {
        evaluateProgram(node);
        return null;
    }

    @Override public Object visit(FunctionDecl node) {
        functions.put(node.name, node);
        return null;
    }

    @Override public Object visit(VarDecl node) {
        Object value = node.initializer.accept(this);
        globals.put(node.name, value);
        return null;
    }

    @Override public Object visit(PrintStmt node) {
        Object val = node.expression.accept(this);
        System.out.println(val);
        return null;
    }

    @Override public Object visit(IfStmt node) {
        boolean cond = (Boolean)node.condition.accept(this);
        if (cond) node.thenBranch.accept(this);
        else if (node.elseBranch!=null) node.elseBranch.accept(this);
        return null;
    }

    @Override public Object visit(WhileStmt node) {
        while ((Boolean)node.condition.accept(this)) {
            node.body.accept(this);
        }
        return null;
    }

    @Override public Object visit(ExpressionStmt node) {
        node.expression.accept(this);
        return null;
    }

    @Override public Object visit(BinaryExpr node) {
        Object left = node.left.accept(this);
        Object right = node.right.accept(this);
        switch (node.operator) {
            case "+": return (left instanceof String) ? left.toString() + right : (Integer)left + (Integer)right;
            case "-": return (Integer)left - (Integer)right;
            case "*": return (Integer)left * (Integer)right;
            case "/": return (Integer)left / (Integer)right;
            case "==": return left.equals(right);
            case "!=": return !left.equals(right);
            case "<": return (Integer)left < (Integer)right;
            case ">": return (Integer)left > (Integer)right;
            default: throw new RuntimeException("Operador desconocido " + node.operator);
        }
    }

    @Override public Object visit(LiteralExpr node) {
        return node.value;
    }

    @Override public Object visit(VariableExpr node) {
        if (!globals.containsKey(node.name)) throw new RuntimeException("Variable no definida: " + node.name);
        return globals.get(node.name);
    }

    @Override public Object visit(CallExpr node) {
        FunctionDecl fn = functions.get(node.callee);
        if (fn==null) throw new RuntimeException("Función no definida: " + node.callee);
        // crear entorno local
        Map<String,Object> saved = new HashMap<>(globals);
        for (int i=0;i<fn.params.size();i++) {
            globals.put(fn.params.get(i), node.arguments.get(i).accept(this));
        }
        // ejecutar
        for (Node stmt: fn.body) {
            stmt.accept(this);
            // Note: ReturnStmt handling omitted; implement return support if needed.
        }
        globals.clear();
        globals.putAll(saved);
        return null;
    }

    // ReturnStmt omitted for brevity: you can implement similarly
}
