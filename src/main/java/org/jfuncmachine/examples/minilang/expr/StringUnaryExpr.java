package org.jfuncmachine.examples.minilang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.javainterop.CallJavaMethod;
import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.examples.minilang.Environment;
import org.jfuncmachine.examples.minilang.types.StringType;
import org.jfuncmachine.examples.minilang.types.Type;
import org.jfuncmachine.sexprlang.translate.ModelItem;
import org.jfuncmachine.util.unification.TypeHolder;
import org.jfuncmachine.util.unification.UnificationException;

@ModelItem(includeStartSymbol = true)
public class StringUnaryExpr extends StringExpr {
    @ModelItem(isExprStart = true)
    public enum ExprType {
        ToUpper("toupper"),
        ToLower("tolower");

        public final String symbol;
        ExprType(String symbol) {
            this.symbol = symbol;
        }
    }

    public final ExprType exprType;
    public final Expr expr;
    public StringUnaryExpr(ExprType exprType, Expr expr, String filename, int lineNumber) {
        super(filename, lineNumber);
        this.exprType = exprType;
        this.expr = expr;
    }

    @Override
    public void unify(TypeHolder<Type> other, Environment<TypeHolder<Type>> env) throws UnificationException {
        TypeHolder<Type> stringType = new TypeHolder<>(new StringType(filename, lineNumber));
        other.unify(stringType);
        expr.unify(stringType, env);
        type.unify(stringType);
    }

    public Expression generate() {
        String methodName = switch(exprType) {
            case ToUpper -> "toUpperCase";
            case ToLower -> "toLowerCase";
        };

        return new CallJavaMethod("java.lang.String", methodName, SimpleTypes.STRING,
                expr.generate(), new Expression[0], filename, lineNumber);
    }
}
