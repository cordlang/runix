// src/main/java/com/runix/runtime/Interpreter.java
package com.runix.runtime;

import com.runix.parser.ast.*;
import com.runix.Evaluator.Evaluator;

public class Interpreter {
    // Removed unused Environment global field
    
    public void interpret(Program program) {
        // En este ejemplo delegamos la evaluación en Evaluator,
        // aunque se podría implementar directamente aquí.
        Evaluator evaluator = new Evaluator();
        evaluator.evaluate(program);
    }
}
