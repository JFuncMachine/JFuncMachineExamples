package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.GetValue;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(isSymbolExpr = true)
public class SymbolExpr extends Expr {
    public final String name;

    public SymbolExpr(String name, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.name = name;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> symbolLookup = env.lookup(name);
        if (symbolLookup == null) {
            throw createException(String.format("Invalid symbol %s", name));
        }
        symbolLookup.unify(other);
        type.unify(other);
    }

    public Expression generate() {
        return new GetValue(name, ((Type)type.concreteType).toJFMType());
    }
}
