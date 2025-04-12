package com.runix.Evaluator;

import com.runix.parser.ast.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Evaluator implements NodeVisitor<Object> {
    private final Map<String, Object> globals = new HashMap<>();

    public Object evaluate(Node node) {
        return node.accept(this);
    }

    @Override
    public Object visitReturnStmt(ReturnStmt stmt) {
        return stmt.getValue().accept(this);
    }

    @Override
    public Object visit(Program node) {
        Object result = null;
        for (Node stmt : node.statements) {
            result = stmt.accept(this);
        }
        return result;
    }

    @Override
    public Object visit(FunctionDecl node) {
        globals.put(node.name, node);
        return null;
    }

    @Override
    public Object visit(VarDecl node) {
        Object value = node.initializer.accept(this);
        globals.put(node.name, value);
        return value;
    }

    @Override
    public Object visit(PrintStmt node) {
        Object value = node.expression.accept(this);
        System.out.println(value);
        return null;
    }

    @Override
    public Object visit(IfStmt node) {
        Object condition = node.condition.accept(this);
        if ((boolean) condition) {
            return node.thenBranch.accept(this);
        } else if (node.elseBranch != null) {
            return node.elseBranch.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(WhileStmt node) {
        while ((boolean) node.condition.accept(this)) {
            node.body.accept(this);
        }
        return null;
    }

    @Override
    public Object visit(ExpressionStmt node) {
        return node.expression.accept(this);
    }

    @Override
    public Object visit(BinaryExpr node) {
        Object left = node.left.accept(this);
        Object right = node.right.accept(this);

        switch (node.operator) {
            case "+":
                return ((Number) left).doubleValue() + ((Number) right).doubleValue();
            case "-":
                return ((Number) left).doubleValue() - ((Number) right).doubleValue();
            case "*":
                return ((Number) left).doubleValue() * ((Number) right).doubleValue();
            case "/":
                return ((Number) left).doubleValue() / ((Number) right).doubleValue();
            case "==":
                return left.equals(right);
            case "!=":
                return !left.equals(right);
            case ">":
                return ((Number) left).doubleValue() > ((Number) right).doubleValue();
            case ">=":
                return ((Number) left).doubleValue() >= ((Number) right).doubleValue();
            case "<":
                return ((Number) left).doubleValue() < ((Number) right).doubleValue();
            case "<=":
                return ((Number) left).doubleValue() <= ((Number) right).doubleValue();
            default:
                throw new RuntimeException("Operador no soportado: " + node.operator);
        }
    }

    @Override
    public Object visit(LiteralExpr node) {
        return node.value;
    }

    @Override
    public Object visit(VariableExpr node) {
        if (!globals.containsKey(node.name))
            throw new RuntimeException("Variable no definida: " + node.name);
        return globals.get(node.name);
    }

    @Override
    public Object visit(CallExpr node) {
        if (node.callee.equals("input")) {
            try (Scanner scanner = new Scanner(System.in)) {
                return scanner.nextLine();
            }
        }

        FunctionDecl func = (FunctionDecl) globals.get(node.callee);
        if (func == null) {
            throw new RuntimeException("Función no encontrada: " + node.callee);
        }

        Object[] args = new Object[node.arguments.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = node.arguments.get(i).accept(this);
        }

        // Guardar valores de parámetros
        Map<String, Object> savedGlobals = new HashMap<>(globals);
        for (int i = 0; i < func.params.size(); i++) {
            globals.put(func.params.get(i), args[i]);
        }

        // Ejecutar la función
        Object result = func.body.accept(this);

        // Restaurar valores globales
        globals.clear();
        globals.putAll(savedGlobals);

        return result;
    }

    @Override
    public Object visit(BlockStmt node) {
        Object result = null;
        for (Node stmt : node.getStatements()) {
            result = stmt.accept(this);
        }
        return result;
    }
}