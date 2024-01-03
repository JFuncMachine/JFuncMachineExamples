package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.constants.IntConstant;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.IntType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(isIntConstant = true)
public class IntConstantExpr extends IntExpr {
    public final int value;

    public IntConstantExpr(int value, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.value = value;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> intType = new TypeHolder<>(new IntType(filename, lineNumber));
        intType.unify(other);
        intType.unify(type);
    }

    public Expression generate() {
        return new IntConstant(value);
    }
}
