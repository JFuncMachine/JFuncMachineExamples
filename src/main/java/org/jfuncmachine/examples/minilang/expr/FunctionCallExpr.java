package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.CallStaticMethod;
import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.types.FunctionType;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.FuncType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(defaultForClass = Expr.class, includeStartSymbol = true, varargStart = 1)
public class FunctionCallExpr extends Expr {
    public final String name;
    public final Expr[] arguments;
    protected FuncType targetType;


    public FunctionCallExpr(String name, Expr[] arguments, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> funcType = env.lookup(name);
        if (funcType == null) {
            throw createException(String.format("Unknown function %s", name));
        }

        if (funcType.concreteType == null) {
            throw createException(String.format("Function %s is undefined", name));
        }

        if (!(funcType.concreteType instanceof FuncType functionType)) {
            throw createException(String.format("%s is not a function", name));
        }

        if (functionType.paramTypes.length != arguments.length) {
            throw createException(String.format("Function %s takes %d parameters, %d arguments were given",
                    name, functionType.paramTypes.length, arguments.length));
        }

        for (int i=0; i < arguments.length; i++) {
            arguments[i].unify(functionType.paramTypes[i], env);
        }
        other.unify(functionType.returnType);
        type.unify(functionType.returnType);
        targetType = functionType;
    }

    public Expression generate() {
        Expression[] callArgs = new Expression[arguments.length];
        for (int i=0; i < callArgs.length; i++) {
            callArgs[i] = arguments[i].generate();
        }

        FunctionType funcType = (FunctionType)(targetType.toJFMType());
        return new CallStaticMethod(name, funcType.parameterTypes, funcType.returnType,
                callArgs, filename, lineNumber);
    }
}
