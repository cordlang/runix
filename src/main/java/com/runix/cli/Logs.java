// src/main/java/com/runix/cli/Logs.java
package com.runix.cli;

public class Logs {
    // Clase para centralizar mensajes y niveles de log.
    public static void info(String message) {
        System.out.println("[INFO] " + message);
    }
    
    public static void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }
    
    public static void error(String message) {
        System.err.println("[ERROR] " + message);
    }
}
