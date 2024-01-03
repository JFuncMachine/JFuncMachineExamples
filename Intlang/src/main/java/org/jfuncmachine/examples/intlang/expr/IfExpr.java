package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.If;
import org.jfuncmachine.sexprlang.translate.ModelItem;

import java.util.Map;

@ModelItem(symbol = "if")
public record IfExpr(Comparison comparison, IntExpr truePath, IntExpr falsePath) implements IntExpr {
    public Expression generate(Map<String, FunctionDef> functions) {
        return new If(comparison.generate(functions), truePath.generate(functions), falsePath.generate(functions));
    }
}
