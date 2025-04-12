package com.runix.cli;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.runix.lexer.Lexer;
import com.runix.lexer.Token;
import com.runix.parser.Parser;
import com.runix.Evaluator.Evaluator;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java -jar runix.jar archivo.rx");
            return;
        }

        String filename = args[0];
        
        try {
            // Read the file
            List<String> lines = Files.readAllLines(
                Paths.get(filename),
                StandardCharsets.UTF_8
            );
            StringBuilder code = new StringBuilder();
            for (String line : lines) {
                code.append(line).append("\n");
            }
            System.out.println("\n=== Código fuente ===");
            System.out.println(code.toString());
            System.out.println("=== Fin del código ===\n");

            // Lexer
            Lexer lexer = new Lexer(code.toString());
            List<Token> tokens = lexer.tokenize();
            System.out.println("\n=== Tokens ===");
            for (Token token : tokens) {
                System.out.println(token);
            }
            System.out.println("=== Fin de tokens ===\n");
            
            // Parser
            Parser parser = new Parser(tokens);
            System.out.println("\n=== Parseando ===");
            var ast = parser.parse();
            System.out.println("=== Parse completado ===\n");
            
            // Evaluator
            Evaluator evaluator = new Evaluator();
            System.out.println("\n=== Evaluando ===");
            evaluator.visit(ast);
            System.out.println("=== Evaluación completada ===\n");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}