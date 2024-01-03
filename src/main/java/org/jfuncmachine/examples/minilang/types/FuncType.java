package org.jfuncmachine.examples.minilang.types;

import org.jfuncmachine.compiler.model.types.FunctionType;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.Unifiable;
import org.jfuncmachine.util.unification.UnificationException;

public class FuncType extends Type {
    public final TypeHolder[] paramTypes;
    public final TypeHolder returnType;
    public FuncType(TypeHolder[] types, TypeHolder returnType, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.paramTypes = types;
        this.returnType = returnType;
    }

    @Override
    public org.jfuncmachine.compiler.model.types.Type toJFMType() {
        org.jfuncmachine.compiler.model.types.Type[] jfmParamTypes =
                new org.jfuncmachine.compiler.model.types.Type[paramTypes.length];
        for (int i=0; i < paramTypes.length; i++) {
            jfmParamTypes[i] = ((Type)paramTypes[i].concreteType).toJFMType();
        }
        return new FunctionType(jfmParamTypes,
                ((Type)returnType.concreteType).toJFMType());
    }

    @Override
    public void unify(Unifiable other) throws UnificationException {
        if (!(other instanceof FuncType otherFunc)) {
            throw createException(other);
        }

        if (otherFunc.paramTypes.length != paramTypes.length) {
            throw createException(String.format("Parameter count mismatch %d vs %d", paramTypes.length,
                    otherFunc.paramTypes.length));
        }

        for (int i=0; i < paramTypes.length; i++) {
            paramTypes[i].unify(otherFunc.paramTypes[i]);
        }

        returnType.unify(otherFunc.returnType);
    }
}
