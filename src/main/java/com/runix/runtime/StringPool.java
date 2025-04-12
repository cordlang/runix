package com.runix.runtime;

import java.util.HashMap;
import java.util.Map;

public class StringPool {
    private static final Map<String, String> pool = new HashMap<>();
    private static final String[] COMMON_STRINGS = {
        "print", "input", "if", "else", "while", "return",
        "true", "false", "null", "let", "func",
        "Registro de nuevo usuario", "Ingrese su nombre de usuario:",
        "Ingrese su contraseña:", "Usuario registrado exitosamente!",
        "Inicio de sesión", "¡Bienvenido ", "!",
        "Credenciales incorrectas", "Sistema de Login",
        "1. Registrar nuevo usuario", "2. Iniciar sesión",
        "3. Salir", "Puede iniciar sesión ahora",
        "No hay usuarios registrados", "Saliendo...",
        "Opción inválida"
    };

    static {
        // Inicializar strings comunes
        for (String str : COMMON_STRINGS) {
            pool.put(str, str);
        }
    }

    public static String intern(String str) {
        if (str == null) return null;
        return pool.computeIfAbsent(str, s -> s);
    }

    public static String intern(String str, boolean force) {
        if (str == null) return null;
        if (force) {
            pool.put(str, str);
        }
        return str;
    }

    public static void clear() {
        pool.clear();
        // Reinicializar strings comunes
        for (String str : COMMON_STRINGS) {
            pool.put(str, str);
        }
    }

    public static int size() {
        return pool.size();
    }
}
