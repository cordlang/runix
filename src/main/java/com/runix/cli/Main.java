// src/main/java/com/runix/cli/Main.java
package com.runix.cli;

import com.runix.parser.Tokenizer;
import com.runix.parser.Parser;
import com.runix.ast.Program;
import com.runix.evaluator.Evaluator;
import com.runix.utils.Helpers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length==0) {
            System.err.println("Uso: java -jar runix.jar <archivo.rx>");
            System.exit(1);
        }
        String code = Files.readString(Path.of(args[0]));
        Helpers.log("📥 Código leído", code);

        List<com.runix.parser.Token> tokens = new Tokenizer(code).tokenize();
        Helpers.log("🔠 Tokens", tokens);

        Program ast = new Parser(tokens).parse();
        Helpers.log("🌲 AST", ast);

        new Evaluator().evaluateProgram(ast);
    }
}
