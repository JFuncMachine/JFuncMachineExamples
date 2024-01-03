package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.InlineFunction;
import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.InlineCall;
import org.jfuncmachine.compiler.model.inline.Inlines;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.IntType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(includeStartSymbol = true)
public class IntUnaryExpr extends IntExpr {
    @ModelItem(isExprStart = true)
    public enum ExprType {
        Neg,
    }

    public final ExprType exprType;
    public final Expr expr;
    public IntUnaryExpr(ExprType exprType, Expr expr, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.exprType = exprType;
        this.expr = expr;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> intType = new TypeHolder<>(new IntType(filename, lineNumber));
        other.unify(intType);
        expr.unify(intType, env);
        type.unify(intType);
    }

    public Expression generate() {
        InlineFunction inlineFunc = switch(exprType) {
            case Neg -> Inlines.IntNeg;
        };
        return new InlineCall(inlineFunc, new Expression[] { expr.generate() },
                filename, lineNumber);
    }
}
