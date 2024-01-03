package org.jfuncmachine.examples.minilang;

public class MinilangException extends RuntimeException {
    public MinilangException() {
    }

    public MinilangException(String message) {
        super(message);
    }

    public MinilangException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinilangException(Throwable cause) {
        super(cause);
    }
}
