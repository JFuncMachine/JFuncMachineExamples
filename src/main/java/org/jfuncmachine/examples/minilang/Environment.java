package org.jfuncmachine.examples.minilang;

import java.util.HashMap;
import java.util.Map;

public class Environment<T> {
    protected Map<String,T> symbols;
    protected Environment<T> next;

    public Environment() {
        symbols = new HashMap<>();
        next = null;
    }

    public Environment(Environment<T> parent) {
        symbols = new HashMap<>();
        next = parent;
    }

    public T lookup(String key) {
        T val = symbols.get(key);
        if (val != null) return val;
        if (next != null) return next.lookup(key);
        return null;
    }

    public void define(String key, T val) {
        symbols.put(key, val);
    }
}
