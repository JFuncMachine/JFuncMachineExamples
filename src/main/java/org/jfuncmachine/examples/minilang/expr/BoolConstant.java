package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.bool.BoolFalse;
import org.jfuncmachine.compiler.model.expr.bool.BoolTrue;
import org.jfuncmachine.compiler.model.expr.bool.BooleanExpr;
import org.jfuncmachine.compiler.model.expr.constants.BooleanConstant;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.BoolType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(includeStartSymbol = true)
public class BoolConstant extends BoolExpr {
    @ModelItem(isExprStart = true)
    public enum ConstantType {
        True("true"),
        False("false");

        public final String symbol;
        ConstantType(String symbol) {
            this.symbol = symbol;
        }
    }

    public final ConstantType constantType;

    public BoolConstant(ConstantType constantType, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.constantType = constantType;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> boolType = new TypeHolder<>(new BoolType(filename, lineNumber));
        other.unify(boolType);
        this.type.unify(boolType);
    }

    public BooleanExpr generateBooleanExpr() {
        return switch (constantType) {
            case True -> new BoolTrue(filename, lineNumber);
            case False -> new BoolFalse(filename, lineNumber);
        };
    }
    public Expression generate() {
        return switch (constantType) {
            case True -> new BooleanConstant(true);
            case False -> new BooleanConstant(false);
        };
    }
}
