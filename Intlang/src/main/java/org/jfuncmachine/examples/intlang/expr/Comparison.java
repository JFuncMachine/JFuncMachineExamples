package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.compiler.model.expr.bool.BinaryComparison;
import org.jfuncmachine.compiler.model.expr.bool.BooleanExpr;
import org.jfuncmachine.compiler.model.expr.bool.tests.Tests;
import org.jfuncmachine.sexprlang.translate.ModelItem;

import java.util.Map;

@ModelItem(includeStartSymbol = true)
public record Comparison(CompType compType, IntExpr left, IntExpr right) {
    @ModelItem(isExprStart = true)
    public enum CompType {
        Equal("="),
        NotEqual("!="),
        Less("<"),
        LessEqual("<="),
        Greater(">"),
        GreaterEqual(">=");

        public final String symbol;
        CompType(String symbol) {
            this.symbol = symbol;
        }
    }

    public BooleanExpr generate(Map<String,FunctionDef> functions) {
        return new BinaryComparison(
                switch (compType) {
                    case Equal -> Tests.EQ;
                    case NotEqual -> Tests.NE;
                    case Less -> Tests.LT;
                    case LessEqual -> Tests.LE;
                    case Greater -> Tests.GT;
                    case GreaterEqual -> Tests.GE;
                },
                left.generate(functions), right.generate(functions));
    }
}
