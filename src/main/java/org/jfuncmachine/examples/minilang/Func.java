package org.jfuncmachine.examples.minilang;

import org.jfuncmachine.compiler.model.Access;
import org.jfuncmachine.compiler.model.MethodDef;
import org.jfuncmachine.compiler.model.types.Field;
import org.jfuncmachine.examples.minilang.expr.Expr;
import org.jfuncmachine.examples.minilang.expr.ParamField;
import org.jfuncmachine.examples.minilang.types.FuncType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(symbol="define")
public class Func {
    public final String name;
    public final ParamField[] paramTypes;
    public final Expr body;
    public final String filename;
    public final int lineNumber;

    protected TypeHolder<Type> returnType;

    public Func(String name, ParamField[] paramTypes, Expr body, String filename, int lineNumber) {
        this.name = name;
        this.paramTypes = paramTypes;
        this.body = body;
        this.filename = filename;
        this.lineNumber = lineNumber;
        returnType = new TypeHolder<>();
    }

    public void unify(Environment<TypeHolder<Type>> env) throws UnificationException {
        Environment<TypeHolder<Type>> newEnv = new Environment<>(env);
        TypeHolder<Type>[] funcTypeParams = new TypeHolder[paramTypes.length];
        for (int i=0; i < paramTypes.length; i++) {
            newEnv.define(paramTypes[i].name, paramTypes[i].type);
            funcTypeParams[i] = paramTypes[i].type;
        }
        newEnv.define(name, new TypeHolder<>(new FuncType(funcTypeParams, returnType, filename, lineNumber)));

        body.unify(returnType, newEnv);
    }

    public MethodDef generate() {
        Field[] methodFields =
                new Field[paramTypes.length];

        if (!returnType.isFull()) {
            throw new MinilangException(
                    String.format("%s %d: Unable to determine return type of func %s",
                            filename, lineNumber, name));
        }

        for (int i=0; i < paramTypes.length; i++) {
            ParamField paramType = paramTypes[i];
            if (!paramType.type.isFull()) {
                throw new MinilangException(
                        String.format("%s %d: Unable to determine type of func %s param %s",
                                filename, lineNumber, name, paramType.name));
            }
            methodFields[i] = new Field(
                    paramType.name, paramType.type.concreteType.toJFMType());
        }
        return new MethodDef(name, Access.PUBLIC + Access.STATIC,
                methodFields, returnType.concreteType.toJFMType(),
                body.generate(), filename, lineNumber);
    }

    public FuncType getType() {
        TypeHolder[] types = new TypeHolder[paramTypes.length];
        for (int i=0; i < types.length; i++) {
            types[i] = paramTypes[i].type;
        }
        return new FuncType(types, returnType, filename, lineNumber);
    }
}
