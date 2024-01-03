package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.Invoke;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.LambdaType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

public class LambdaCallExpr extends Expr {
    public final Expr target;
    public final Expr[] arguments;

    public LambdaCallExpr(Expr target, Expr[] arguments, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.target = target;
        this.arguments = arguments;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> targetType = new TypeHolder<>();
        target.unify(targetType, env);
        if (targetType.concreteType == null) {
            throw createException("Unable to determine if target expression is a lambda");
        }

        if (!(targetType.concreteType instanceof LambdaType lambdaType)) {
            throw createException("Target expression is not a lambda");
        }

        if (lambdaType.paramTypes.length != arguments.length) {
            throw createException(String.format(
                    "Lambda requires %d parameters, but %d arguments given",
                    lambdaType.paramTypes.length, arguments.length));
        }

        for (int i=0; i < lambdaType.paramTypes.length; i++) {
            arguments[i].unify(lambdaType.paramTypes[i], env);
        }

        other.unify(lambdaType.returnType);
    }

    public Expression generate() {
        Expression targetExpr = target.generate();
        Expression[] argExprs = new Expression[arguments.length];
        for (int i=0; i < argExprs.length; i++) {
            argExprs[i] = arguments[i].generate();
        }
        return new Invoke(targetExpr, argExprs, filename, lineNumber);

    }
}