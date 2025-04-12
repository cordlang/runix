// src/main/java/com/runix/runtime/Environment.java
package com.runix.runtime;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();
    public void define(String name, Object value) {
        values.put(name, value);
    }
    public Object get(String name) {
        if (values.containsKey(name))
            return values.get(name);
        throw new RuntimeException("Variable no definida: " + name);
    }
    public void assign(String name, Object value) {
        if (values.containsKey(name)) {
            values.put(name, value);
            return;
        }
        throw new RuntimeException("Variable no definida: " + name);
    }
}
