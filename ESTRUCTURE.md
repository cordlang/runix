runix/
├── pom.xml
├── README.md
├── target/
│   └── runix-1.0-SNAPSHOT-jar-with-dependencies.jar
├── example/
│   └── main.rx
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── runix/
│                   ├── cli/
│                   │   └── Main.java
│                   │   └── Commands.java
│                   │   └── Logs.java
|                   ├── Evaluator/
│                   │   ├── Evaluator.java
│                   ├── lexer/
│                   │   ├── Token.java
│                   │   └── Tokenizer.java
│                   ├── parser/
│                   │   ├── Parser.java
│                   │   ├── Token.java
│                   │   ├── Tokenizer.java
│                   │   └── ast/
│                   │       ├── BinaryExpr.java
│                   │       ├── CallExpr.java
│                   │       ├── Expr.java
│                   │       ├── ExpressionStmt.java
│                   │       ├── FunctionDecl.java
│                   │       ├── IfStmt.java
│                   │       ├── LiteralExpr.java
│                   │       ├── Node.java
│                   │       ├── NodeVisitor.java
│                   │       ├── PrintStmt.java
│                   │       ├── Program.java
│                   │       ├── VarDecl.java
│                   │       ├── VariableExpr.java
│                   │       └── WhileStmt.java
│                   ├── runtime/
│                   │   ├── Environment.java
│                   │   └── Interpreter.java
│                   └── util/
│                       └── Helpers.java
