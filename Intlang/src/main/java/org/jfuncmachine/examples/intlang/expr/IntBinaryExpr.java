package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.InlineCall;
import org.jfuncmachine.compiler.model.inline.Inlines;
import org.jfuncmachine.sexprlang.translate.ModelItem;

import java.util.Map;

@ModelItem(includeStartSymbol = true)
public record IntBinaryExpr(ExprType exprType, IntExpr left, IntExpr right, String filename, int lineNumber)
        implements IntExpr {
    @ModelItem(isExprStart = true)
    public enum ExprType {
        Add("+"),
        Sub("-"),
        Mul("*"),
        Div("/");

        public final String symbol;
        ExprType(String symbol) {
            this.symbol = symbol;
        }
    }

    public Expression generate(Map<String, FunctionDef> functions) {
        return new InlineCall(
                switch (exprType) {
                    case Add -> Inlines.IntAdd;
                    case Sub -> Inlines.IntSub;
                    case Mul -> Inlines.IntMul;
                    case Div -> Inlines.IntDiv;
                },
                new Expression[] { left.generate(functions), right.generate(functions) },
                filename, lineNumber);
    }

}
