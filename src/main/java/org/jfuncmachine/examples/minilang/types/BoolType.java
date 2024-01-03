package org.jfuncmachine.examples.minilang.types;

import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.util.unification.Unifiable;
import org.jfuncmachine.util.unification.UnificationException;

public class BoolType extends Type {
    public BoolType(String filename, int lineNumber) {
        super(filename, lineNumber);
    }

    @Override
    public org.jfuncmachine.compiler.model.types.Type toJFMType() {
        return SimpleTypes.BOOLEAN;
    }

    @Override
    public void unify(Unifiable other) throws UnificationException {
        if (!(other instanceof BoolType)) {
            throw createException(other);
        }
    }
}
