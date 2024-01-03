package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.GetValue;
import org.jfuncmachine.compiler.model.types.SimpleTypes;
import org.jfuncmachine.sexprlang.translate.ModelItem;

import java.util.Map;

@ModelItem(isSymbolExpr = true)
public record VarExpr(String symbol, String filename, int lineNumber) implements IntExpr {
    public Expression generate(Map<String,FunctionDef> functions) {
        return new GetValue(symbol, SimpleTypes.INT, filename, lineNumber);
    }
}
