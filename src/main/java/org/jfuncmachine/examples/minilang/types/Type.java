package org.jfuncmachine.examples.minilang.types;

import org.jfuncmachine.util.unification.Unifiable;
import org.jfuncmachine.util.unification.UnificationException;

public abstract class Type implements Unifiable {
    public final String filename;
    public final int lineNumber;

    public Type(String filename, int lineNumber) {
        this.filename = filename;
        this.lineNumber = lineNumber;
    }

    public abstract org.jfuncmachine.compiler.model.types.Type toJFMType();

    @Override
    public abstract void unify(Unifiable other) throws UnificationException;

    public UnificationException createException(String message) {
        return new UnificationException(String.format("%s %d: %s", filename, lineNumber, message));
    }

    public UnificationException createException(Unifiable other) {
        if (other instanceof Type otherType) {
            return new UnificationException(String.format("%s %d: Unable to unify %s with %s at %s line %d",
                    filename, lineNumber, this.getClass().getSimpleName(), other.getClass().getSimpleName(),
                    otherType.filename, otherType.lineNumber));
        } else {
            return new UnificationException(String.format("%s %d: Unable to unify %s with %s",
                    filename, lineNumber, this.getClass().getSimpleName(), other.getClass().getSimpleName()));
        }
    }

    public UnificationException createException(String message, Unifiable other) {
        if (other instanceof Type otherType) {
            return new UnificationException(String.format("%s %d: %s %s with %s at %s line %d",
                    filename, lineNumber, message, this.getClass().getSimpleName(), other.getClass().getSimpleName(),
                    otherType.filename, otherType.lineNumber));
        } else {
            return new UnificationException(String.format("%s %d: Unable to unify %s with %s",
                    filename, lineNumber, this.getClass().getSimpleName(), other.getClass().getSimpleName()));
        }
    }
}
