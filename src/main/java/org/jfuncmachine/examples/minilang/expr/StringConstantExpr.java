package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.constants.StringConstant;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.StringType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(isStringConstant = true)
public class StringConstantExpr extends StringExpr {
    public final String value;
    public StringConstantExpr(String value, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.value = value;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> stringType = new TypeHolder<>(new StringType(filename, lineNumber));
        other.unify(stringType);
        type.unify(stringType);
    }

    public Expression generate() {
        return new StringConstant(value);
    }
}
