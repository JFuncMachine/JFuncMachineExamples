package org.jfuncmachine.examples.minilang.types;

import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.util.unification.Unifiable;
import org.jfuncmachine.util.unification.UnificationException;

public class UnitType extends Type {
    public UnitType(String filename, int lineNumber) {
        super(filename, lineNumber);
    }

    @Override
    public org.jfuncmachine.compiler.model.types.Type toJFMType() {
        return SimpleTypes.UNIT;
    }

    @Override
    public void unify(Unifiable other) throws UnificationException {
        if (!(other instanceof UnitType)) {
            throw createException(other);
        }
    }
}
