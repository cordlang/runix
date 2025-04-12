package com.runix.Evaluator;

import java.util.HashMap;
import java.util.Map;

import com.runix.parser.ast.*;

public class Evaluator implements NodeVisitor<Object> {
    private final Map<String, Object> globals = new HashMap<>();

    /** Punto de entrada público */
    public Object evaluate(Node node) {
        return node.accept(this);
    }

    @Override
    public Object visitProgram(Program node) {
        Object result = null;
        for (Node stmt : node.statements) {
            Object temp = stmt.accept(this);
            if (temp != null) {
                result = temp;
            }
        }
        return result;
    }

    @Override
    public Object visitFunctionDecl(FunctionDecl node) {
        globals.put(node.name, node);
        return null;
    }

    @Override
    public Object visitVarDecl(VarDecl node) {
        Object value = node.initializer.accept(this);
        globals.put(node.name, value);
        return value;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt node) {
        return node.getValue().accept(this);
    }

    @Override
    public Object visitPrintStmt(PrintStmt node) {
        Object value = node.expression.accept(this);
        if (value != null) {
            // If we're printing a string concatenation, evaluate it first
            if (node.expression instanceof BinaryExpr && ((BinaryExpr)node.expression).operator.equals("+")) {
                BinaryExpr expr = (BinaryExpr) node.expression;
                Object left = expr.left.accept(this);
                Object right = expr.right.accept(this);
                value = (left == null ? "" : left.toString()) + (right == null ? "" : right.toString());
            }
            System.out.println(value.toString());
        }
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt node) {
        boolean cond = evaluateBoolean(node.condition);
        if (cond) {
            return node.thenBranch.accept(this);
        } else if (node.elseBranch != null) {
            return node.elseBranch.accept(this);
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt node) {
        Object result = null;
        while (evaluateBoolean(node.condition)) {
            result = node.body.accept(this);
        }
        return result;
    }

    @Override
    public Object visitForStmt(ForStmt node) {
        // Evaluate initialization expression
        node.init.accept(this);
        
        // Main loop
        Object result = null;
        while (evaluateBoolean(node.condition)) {
            // Evaluate loop body
            result = node.body.accept(this);
            // Evaluate increment expression
            node.increment.accept(this);
        }
        return result;
    }

    @Override
    public Object visitExpressionStmt(ExpressionStmt node) {
        return node.expression.accept(this);
    }

    @Override
    public Object visitBlockStmt(BlockStmt node) {
        Object result = null;
        for (Node stmt : node.getStatements()) {
            Object stmtResult = stmt.accept(this);
            if (stmtResult != null) {
                result = stmtResult;
            }
        }
        return result;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr node) {
        Object left = node.left.accept(this);
        Object right = node.right.accept(this);
        String op = node.operator;

        // concatenación
        if (op.equals("+")) {
            if (left instanceof String || right instanceof String) {
                return (left == null ? "" : left.toString())
                     + (right == null ? "" : right.toString());
            }
        }

        if (left instanceof Number && right instanceof Number) {
            double l = ((Number) left).doubleValue();
            double r = ((Number) right).doubleValue();

            switch (op) {
                case "+":  return l + r;
                case "-":  return l - r;
                case "*":  return l * r;
                case "/":  return l / r;
                case "<":  return l < r;
                case ">":  return l > r;
                case "<=" : return l <= r;
                case ">=" : return l >= r;
                case "==": return left.equals(right);
                case "!":  return !left.equals(right);
                default: throw new RuntimeException("Operador no soportado: " + op);
            }
        }

        // comparaciones genéricas
        switch (op) {
            case "==": return left.equals(right);
            case "!":  return !left.equals(right);
            default: throw new RuntimeException("Operador no soportado: " + op);
        }
    }

    @Override
    public Object visitLiteralExpr(LiteralExpr node) {
        return node.value;
    }

    @Override
    public Object visitVariableExpr(VariableExpr node) {
        if (!globals.containsKey(node.name)) {
            throw new RuntimeException("Variable no definida: " + node.name);
        }
        return globals.get(node.name);
    }

    @Override
    public Object visitCallExpr(CallExpr node) {
        if (!globals.containsKey(node.callee)) {
            throw new RuntimeException("Función no encontrada: " + node.callee);
        }
        Object callee = globals.get(node.callee);
        if (!(callee instanceof FunctionDecl)) {
            throw new RuntimeException("No se puede llamar a algo que no es una función");
        }
        FunctionDecl func = (FunctionDecl) callee;

        // evaluar args
        Object[] args = new Object[node.arguments.size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = node.arguments.get(i).accept(this);
        }

        // guardar entorno
        Map<String,Object> saved = new HashMap<>(globals);
        try {
            for (int i = 0; i < func.params.size(); i++) {
                globals.put(func.params.get(i), args[i]);
            }
            Object result = func.body.accept(this);
            return result; // Return the result of the function call
        } finally {
            globals.clear();
            globals.putAll(saved);
        }
    }

    /** Evalúa y garantiza boolean */
    private boolean evaluateBoolean(Expr expr) {
        Object val = expr.accept(this);
        if (!(val instanceof Boolean)) {
            throw new RuntimeException("Expresión booleana inválida: " + val);
        }
        return (boolean) val;
    }
}
