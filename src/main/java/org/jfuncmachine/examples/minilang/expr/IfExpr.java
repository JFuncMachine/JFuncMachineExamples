package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.If;
import org.jfuncmachine.compiler.model.expr.bool.UnaryComparison;
import org.jfuncmachine.compiler.model.expr.bool.tests.Tests;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.BoolType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(symbol="if")
public class IfExpr extends Expr {
    public final Expr test;
    public final Expr truePath;
    public final Expr falsePath;

    public IfExpr(Expr test, Expr truePath, Expr falsePath, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.test = test;
        this.truePath = truePath;
        this.falsePath = falsePath;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> boolType = new TypeHolder<>(new BoolType(filename, lineNumber));
        test.unify(boolType, env);

        TypeHolder<Type> truePathType = new TypeHolder<>();
        truePath.unify(truePathType, env);
        TypeHolder<Type> falsePathType = new TypeHolder<>();
        falsePath.unify(falsePathType, env);
        truePath.type.unify(falsePath.type);
        falsePath.type.unify(other);
        type.unify(falsePath.type);
    }

    public Expression generate() {
        if (test instanceof BoolExpr) {
            return new If(((BoolExpr)test).generateBooleanExpr(), truePath.generate(), falsePath.generate(),
                    filename, lineNumber);
        } else {
            return new If(new UnaryComparison(Tests.IsTrue, test.generate()), truePath.generate(), falsePath.generate(),
                    filename, lineNumber);
        }
    }
}
