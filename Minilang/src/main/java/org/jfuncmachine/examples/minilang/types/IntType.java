package org.jfuncmachine.examples.minilang.types;

import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.util.unification.Unifiable;
import org.jfuncmachine.util.unification.UnificationException;

public class IntType extends Type {
    public IntType(String filename, int lineNumber) {
        super(filename, lineNumber);
    }

    @Override
    public org.jfuncmachine.compiler.model.types.Type toJFMType() {
        return SimpleTypes.INT;
    }

    @Override
    public void unify(Unifiable other) throws UnificationException {
        if (!(other instanceof IntType)) {
            throw createException(other);
        }
    }
}
