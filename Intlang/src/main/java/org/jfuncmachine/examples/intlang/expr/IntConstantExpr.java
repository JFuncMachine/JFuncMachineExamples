package org.jfuncmachine.examples.intlang.expr;

import org.jfuncmachine.compiler.model.expr.Expression;
import org.jfuncmachine.compiler.model.expr.constants.IntConstant;
import org.jfuncmachine.sexprlang.translate.ModelItem;

import java.util.Map;

@ModelItem(isIntConstant = true)
public record IntConstantExpr(int value, String filename, int lineNumber) implements IntExpr {
    public Expression generate(Map<String, FunctionDef> functions) {
        return new IntConstant(value, filename, lineNumber);
    }
}
