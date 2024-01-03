package org.jfuncmachine.examples.intlang.expr;

public class IntLangException extends RuntimeException {
    public IntLangException(String filename, int lineNumber, String message) {
        super(String.format("%s %d: %s", filename, lineNumber, message));
    }
}
